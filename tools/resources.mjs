import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const source = path.join(root, 'linguistic/resources/transport.json');
const asset = path.join(root, 'app/src/main/assets/content/fr-da/resources.json');
const idPattern = /^[a-z0-9]+(?:[.-][a-z0-9]+)*$/;
const tagPattern = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;
const allowedUrls = new Set([
  'https://rejseplanen.dk',
  'https://www.dsb.dk',
  'https://dinoffentligetransport.dk',
  'https://m.dk',
  'https://publictransport.dk/app',
  'https://www.visitcopenhagen.com'
]);

function check(ok, p, message) { if (!ok) throw new Error(`${p}: ${message}`); }
function obj(x, p, required, optional = []) {
  check(x && typeof x === 'object' && !Array.isArray(x), p, 'expected object');
  for (const k of required) check(Object.hasOwn(x, k), `${p}.${k}`, 'required field');
  for (const k of Object.keys(x)) {
    check(required.includes(k) || optional.includes(k), `${p}.${k}`, 'unknown field');
    check(x[k] !== null, `${p}.${k}`, 'null forbidden');
  }
}
function str(x, p) { check(typeof x === 'string' && x.trim().length, p, 'nonblank string required'); }
function pair(x, p) { obj(x, p, ['support', 'target']); str(x.support, p + '.support'); str(x.target, p + '.target'); }

export function validateResources(doc, file = 'resources') {
  const ids = new Set();
  obj(doc, file, ['schemaVersion', 'contentVersion', 'collection']);
  check(doc.schemaVersion === 1, file, 'schemaVersion must be 1');
  str(doc.contentVersion, file + '.contentVersion');
  const c = doc.collection, p = file + '.collection';
  obj(c, p, ['id', 'category', 'title', 'intro', 'items']);
  check(typeof c.id === 'string' && idPattern.test(c.id) && !ids.has(c.id), p + '.id', 'invalid/duplicate ID');
  ids.add(c.id);
  str(c.category, p + '.category');
  pair(c.title, p + '.title');
  pair(c.intro, p + '.intro');
  check(Array.isArray(c.items) && c.items.length, p + '.items', 'empty items');
  const orders = new Set();
  for (const item of c.items) {
    const ip = p + '.' + item.id;
    obj(item, ip, ['id', 'order', 'name', 'title', 'url', 'tags']);
    check(typeof item.id === 'string' && idPattern.test(item.id) && !ids.has(item.id), ip, 'invalid/duplicate ID');
    ids.add(item.id);
    check(Number.isInteger(item.order) && item.order > 0 && !orders.has(item.order), ip, 'invalid/duplicate order');
    orders.add(item.order);
    str(item.name, ip + '.name');
    pair(item.title, ip + '.title');
    str(item.url, ip + '.url');
    check(item.url.startsWith('https://'), ip + '.url', 'https URL required');
    check(allowedUrls.has(item.url), ip + '.url', 'URL must be an approved official destination');
    check(Array.isArray(item.tags) && item.tags.every(t => typeof t === 'string' && tagPattern.test(t)) && new Set(item.tags).size === item.tags.length, ip + '.tags', 'invalid/duplicate tags');
  }
  return ids.size;
}

export function readResources() {
  const text = new TextDecoder('utf-8', { fatal: true }).decode(fs.readFileSync(source));
  check(!/textPt|pt-PT|Portuguese|portugis/i.test(text), source, 'Portuguese remnant');
  const doc = JSON.parse(text);
  validateResources(doc, path.basename(source));
  return doc;
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const doc = readResources();
  if (process.argv.includes('--write')) {
    fs.mkdirSync(path.dirname(asset), { recursive: true });
    fs.writeFileSync(asset, JSON.stringify(doc, null, 2) + '\n');
  } else {
    const bundled = JSON.parse(new TextDecoder('utf-8', { fatal: true }).decode(fs.readFileSync(asset)));
    assert.deepEqual(bundled, doc, 'Canonical resources asset is stale; run with --write');
  }
  console.log(`PASS: ${validateResources(doc)} practical-resource IDs; ${doc.collection.items.length} external links.`);
}
