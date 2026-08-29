import test from 'node:test';
import assert from 'node:assert/strict';
import { readResources, validateResources } from './resources.mjs';

const doc = readResources();

test('transport resources have seven unique IDs and six official links', () => {
  assert.equal(validateResources(doc), 7);
  assert.equal(doc.collection.items.length, 6);
  assert.equal(doc.collection.id, 'resources.transport');
});

test('rejects unknown resource field', () => {
  const copy = structuredClone(doc);
  copy.collection.items[0].textPt = 'x';
  assert.throws(() => validateResources(copy));
});

test('rejects non-https or unlisted URL', () => {
  const copy = structuredClone(doc);
  copy.collection.items[0].url = 'http://example.com';
  assert.throws(() => validateResources(copy));
});
