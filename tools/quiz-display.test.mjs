import test from 'node:test';
import assert from 'node:assert/strict';
import { merge } from './production-content.mjs';
import {
  assertAnswerDisplay,
  classifyQuestion,
  displayedAnswerText,
  leaksBothRoles
} from './quiz-display.mjs';

const { result } = merge();

test('target+support button text is leakage when sides differ', () => {
  const pair = { support: 'conditions', target: 'betingelser' };
  assert.equal(leaksBothRoles(pair, 'betingelser\nconditions'), true);
  assert.equal(leaksBothRoles(pair, 'conditions'), false);
  assert.equal(leaksBothRoles(pair, 'betingelser'), false);
  assert.equal(leaksBothRoles({ support: 'pause', target: 'pause' }, 'pause'), false);
  assert.equal(leaksBothRoles({ support: 'récréation/pause', target: 'pause' }, 'récréation/pause'), false);
});

test('betingelser question displays French support only', () => {
  const lesson = result.course.modules
    .flatMap(m => m.lessons)
    .find(l => l.id === 'lesson.level-3.contract');
  const question = lesson.quiz.questions.find(q => q.id === 'question.level-3.contract-betingelser');
  assert.equal(question.answerDisplayRole, 'support');
  assert.deepEqual(
    question.answers.map(a => displayedAnswerText(question, a)),
    ['conditions', 'chiffre d’affaires', 'virement']
  );
  assertAnswerDisplay(question);
});

test('every production question has a resolved display role and no leakage', () => {
  let questions = 0;
  for (const module of result.course.modules) {
    for (const lesson of module.lessons) {
      for (const question of lesson.quiz.questions) {
        questions++;
        const expected = classifyQuestion(module.id, question);
        assert.equal(question.answerDisplayRole, expected.role, question.id);
        assertAnswerDisplay(question);
      }
    }
  }
  assert.equal(questions, 150);
});
