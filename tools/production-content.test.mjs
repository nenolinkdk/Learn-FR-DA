import test from 'node:test';
import fs from 'node:fs';
import assert from 'node:assert/strict';
import { merge, validate } from './production-content.mjs';
import { assertEveryLessonQuiz } from './quiz-integrity.mjs';
const { result } = merge();
test('canonical course has 1206 valid globally unique IDs', () => assert.equal(validate(result), 1206));
test('every lesson resolves to exactly 3 quiz questions', () => {
  const counts = assertEveryLessonQuiz(result.course, 3);
  assert.equal(counts.lessons, 50);
  assert.equal(counts.questions, 150);
});
test('production totals include Level 3', () => {
  assert.deepEqual(result.course.modules.map(m => m.id),
    ['module.level-1', 'module.level-2', 'module.level-3', 'module.children', 'module.grammar']);
  const lessons = result.course.modules.flatMap(m => m.lessons);
  assert.equal(lessons.length, 50);
  assert.equal(lessons.reduce((n, l) => n + l.items.length, 0), 500);
  assert.equal(lessons.reduce((n, l) => n + (l.quiz?.questions.length ?? 0), 0), 150);
  const level3 = result.course.modules.find(m => m.id === 'module.level-3');
  assert.equal(level3.level, 3);
  assert.equal(level3.lessons.length, 10);
  assert.equal(level3.lessons.reduce((n, l) => n + l.items.length, 0), 100);
  assert.equal(level3.lessons.reduce((n, l) => n + l.quiz.questions.length, 0), 30);
});
const cases = {
  'unknown field': d => { d.course.modules[0].lessons[0].items[0].textPt = 'x'; },
  'null optional notes': d => { d.course.modules[0].lessons[0].items[0].notes = null; },
  'duplicate ID': d => { d.course.modules[1].lessons[0].id = d.course.modules[0].lessons[0].id; },
  'invalid ID': d => { d.course.modules[0].lessons[0].id = 'Bad ID'; },
  'duplicate order': d => { d.course.modules[0].lessons[1].order = 1; },
  'fractional order': d => { d.course.modules[0].lessons[0].order = 1.5; },
  'invalid reference': d => { d.course.modules[0].lessons[0].moduleId = 'module.children'; },
  'empty text': d => { d.course.modules[0].lessons[0].items[0].text.support = ' '; },
  'wrong locale': d => { d.course.modules[0].lessons[0].items[0].tts.locale = 'fr-FR'; },
  'missing TTS': d => { delete d.course.modules[0].lessons[0].items[0].tts; },
  'invalid tags': d => { d.course.modules[0].tags = ['Bad tag']; },
  'duplicate tags': d => { d.course.modules[0].tags = ['travel', 'travel']; },
  'multiple correct answers': d => { d.course.modules[0].lessons[0].quiz.questions[0].answers.forEach(a => a.correct = true); },
  'missing answerDisplayRole': d => { delete d.course.modules[0].lessons[0].quiz.questions[0].answerDisplayRole; },
  'invalid answerDisplayRole': d => { d.course.modules[0].lessons[0].quiz.questions[0].answerDisplayRole = 'french'; },
  'no correct answer': d => { d.course.modules[0].lessons[0].quiz.questions[0].answers.forEach(a => a.correct = false); },
  'reversed languages': d => { [d.course.languages.target, d.course.languages.support] = [d.course.languages.support, d.course.languages.target]; },
  'children audience': d => { d.course.modules[3].audience = 'general'; }
};
for (const [name, mutate] of Object.entries(cases)) test(`rejects ${name}`, () => {
  const copy = structuredClone(result); mutate(copy); assert.throws(() => validate(copy));
});

test('every source lesson is preserved unchanged', () => {
  const directory = new URL('../linguistic/production/', import.meta.url);
  const actual = new Map(result.course.modules.flatMap(m => m.lessons).map(l => [l.id, l]));
  for (const file of fs.readdirSync(directory).filter(f => f.endsWith('.json'))) {
    const source = JSON.parse(fs.readFileSync(new URL(file, directory), 'utf8'));
    for (const module of source.course.modules) for (const lesson of module.lessons)
      assert.deepEqual(actual.get(lesson.id), lesson, lesson.id);
  }
});
