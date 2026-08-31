import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const asset = path.join(root, 'app/src/main/assets/content/fr-da/course.json');
const idPattern = /^[a-z0-9]+(?:[.-][a-z0-9]+)*$/;
const tagPattern = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;
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
function pair(x, p) { obj(x,p,['support','target']); str(x.support,p+'.support'); str(x.target,p+'.target'); }
function array(x,p) { check(Array.isArray(x),p,'array required'); }
function tags(x,p) { array(x,p); check(x.every(t=>typeof t==='string' && tagPattern.test(t)) && new Set(x).size===x.length,p,'invalid/duplicate tags'); }
function ordered(xs,p) {
  array(xs,p); const orders=new Set();
  for(const x of xs) { check(Number.isInteger(x.order)&&x.order>0&&!orders.has(x.order),p,'invalid/duplicate order'); orders.add(x.order); }
}
export function validate(doc, file='document') {
  const ids=new Set();
  const id=(x,p)=>{check(typeof x==='string'&&idPattern.test(x)&&!ids.has(x),p,'invalid/duplicate ID');ids.add(x);};
  obj(doc,file,['schemaVersion','contentVersion','course']); check(doc.schemaVersion===1,file,'schemaVersion must be 1'); str(doc.contentVersion,file+'.contentVersion');
  const c=doc.course,p=file+'.course'; obj(c,p,['id','courseLocale','languages','tts','title','modules']); id(c.id,p+'.id');
  check(c.id==='course.fr-da'&&c.courseLocale==='fr-FR',p,'wrong course/direction');
  assert.deepEqual(c.languages,{support:{language:'fr',locale:'fr-FR'},target:{language:'da',locale:'da-DK'}},p+'.languages');
  assert.deepEqual(c.tts,{primaryRole:'target',targetLocale:'da-DK',supportLocale:'fr-FR'},p+'.tts'); pair(c.title,p+'.title'); array(c.modules,p+'.modules'); check(c.modules.length,p,'empty modules');
  for(const m of c.modules) {
    const mp=p+'.'+m.id; obj(m,mp,['id','type','audience','title','tags','lessons'],['level']); id(m.id,mp); pair(m.title,mp+'.title');tags(m.tags,mp+'.tags');
    check(['level','children','grammar','quiz'].includes(m.type),mp,'invalid module type');
    check(m.type==='children'?m.audience==='children'&&!Object.hasOwn(m,'level'):m.audience==='general',mp,'invalid audience/level');
    if(m.type==='level') check([1,2,3].includes(m.level),mp,'invalid level');
    ordered(m.lessons,mp+'.lessons');
    for(const l of m.lessons) {
      const lp=mp+'.'+l.id; obj(l,lp,['id','moduleId','order','title','situation','tags','items'],['quiz']); id(l.id,lp);check(l.moduleId===m.id,lp,'invalid module reference'); pair(l.title,lp+'.title');pair(l.situation,lp+'.situation');tags(l.tags,lp+'.tags');ordered(l.items,lp+'.items');check(l.items.length,lp,'empty items');
      for(const i of l.items) {
        const ip=lp+'.'+i.id; obj(i,ip,['id','order','type','text','tts','tags'],['speaker','notes']);id(i.id,ip);pair(i.text,ip+'.text');tags(i.tags,ip+'.tags');
        check(['phrase','dialogue-turn','grammar-example','digital-scenario'].includes(i.type),ip,'invalid item type');if(Object.hasOwn(i,'speaker'))str(i.speaker,ip+'.speaker');
        obj(i.tts,ip+'.tts',['role','locale','enabled']);check(['target','support'].includes(i.tts.role)&&i.tts.locale===c.languages[i.tts.role].locale&&typeof i.tts.enabled==='boolean',ip,'invalid TTS');
        if(Object.hasOwn(i,'notes')) { obj(i.notes,ip+'.notes',[],['grammar','cultural','digital','pronunciation']);for(const [kind,n] of Object.entries(i.notes)) {const extra=kind==='grammar'?['targetExample']:kind==='pronunciation'?['targetText']:[];obj(n,ip+'.notes.'+kind,['support'],extra);for(const [k,v] of Object.entries(n))str(v,ip+'.notes.'+kind+'.'+k);} }
      }
      if(Object.hasOwn(l,'quiz')) {
        const q=l.quiz,qp=lp+'.quiz';obj(q,qp,['id','title','questions']);id(q.id,qp);pair(q.title,qp+'.title');ordered(q.questions,qp+'.questions');check(q.questions.length,qp,'empty quiz');
        for(const question of q.questions) {
          const p=qp+'.'+question.id;obj(question,p,['id','order','type','prompt','answers','explanation','tags']);id(question.id,p);check(question.type==='single-choice',p,'invalid question type');pair(question.prompt,p+'.prompt');pair(question.explanation,p+'.explanation');tags(question.tags,p+'.tags');array(question.answers,p+'.answers');check(question.answers.length>=2,p,'too few answers');
          for(const a of question.answers) {obj(a,p+'.'+a.id,['id','text','correct']);id(a.id,p+'.'+a.id);pair(a.text,p+'.'+a.id+'.text');check(typeof a.correct==='boolean',p,'correct must be boolean');}
          check(question.answers.filter(a=>a.correct).length===1,p,'exactly one correct answer required');
        }
      }
    }
  }
  return ids.size;
}
function read(file) { const text=new TextDecoder('utf-8',{fatal:true}).decode(fs.readFileSync(file));check(!/textPt|pt-PT|Portuguese|portugis|lesson\.pt[.-]|lesson\.portuguese/i.test(text),file,'Portuguese remnant'); return JSON.parse(text); }
export function merge() {
  const files=fs.readdirSync(path.join(root,'linguistic/production')).filter(f=>f.endsWith('.json')).sort();
  let result;const modules=new Map();
  for(const file of files) {
    const doc=read(path.join(root,'linguistic/production',file));validate(doc,file);
    const {modules:parts,...metadata}=doc.course;
    if(!result)result={schemaVersion:1,contentVersion:'1.0.0-production',course:{...metadata,modules:[]}};
    const {modules:ignored,...expected}=result.course;assert.deepEqual(metadata,expected,file+': course metadata differs');
    for(const m of parts) {
      const {lessons,...meta}=m;
      if(!modules.has(m.id))modules.set(m.id,{...meta,lessons:[]});
      const {lessons:prior,...existing}=modules.get(m.id);assert.deepEqual(meta,existing,file+': module metadata differs');prior.push(...lessons);
    }
  }
  const moduleIds=['module.level-1','module.level-2','module.level-3','module.children','module.grammar'];assert.deepEqual([...modules.keys()].sort(),[...moduleIds].sort());
  result.course.modules=moduleIds.map(id=>modules.get(id));
  for(const m of result.course.modules) {
    m.lessons.sort((a,b)=>a.order-b.order);assert.deepEqual(m.lessons.map(l=>l.order),[1,2,3,4,5,6,7,8,9,10]);
    for(const l of m.lessons) {l.items.sort((a,b)=>a.order-b.order);check(l.quiz?.questions.length===3,l.id,'production requires three quiz questions');l.quiz.questions.sort((a,b)=>a.order-b.order);for(const i of l.items)check(i.tts.role==='target'&&i.tts.locale==='da-DK'&&i.tts.enabled,i.id,'production requires primary Danish TTS');}
  }
  validate(result,'merged production');return {result,files};
}
export function counts(doc) {
  return doc.course.modules.map(m=>{
    const row={module:m.id,modules:1,lessons:m.lessons.length,items:0,quizzes:0,questions:0,answers:0,grammar:0,cultural:0,digital:0,pronunciation:0};
    for(const l of m.lessons) {row.items+=l.items.length;if(l.quiz){row.quizzes++;row.questions+=l.quiz.questions.length;row.answers+=l.quiz.questions.reduce((n,q)=>n+q.answers.length,0);}for(const i of l.items)for(const kind of Object.keys(i.notes??{}))row[kind]++;}return row;
  });
}
if(process.argv[1] && path.resolve(process.argv[1])===fileURLToPath(import.meta.url)) {
  const {result,files}=merge();
  if(process.argv.includes('--write')) {fs.mkdirSync(path.dirname(asset),{recursive:true});fs.writeFileSync(asset,JSON.stringify(result,null,2)+'\n');}
  else assert.deepEqual(read(asset),result,'Canonical asset is stale; run with --write');
  console.log(`PASS: ${files.length} source files; ${validate(result)} globally unique canonical IDs; zero duplicate entity IDs.`);console.table(counts(result));
}
