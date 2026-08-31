import test from 'node:test';
import assert from 'node:assert/strict';
import { merge } from './production-content.mjs';
import { assertEveryLessonQuiz, lessonById, questionsForLessonId } from './quiz-integrity.mjs';

const { result } = merge();
const course = result.course;

const expected = {
  'module.level-1': 'Level 1',
  'module.level-2': 'Level 2',
  'module.level-3': 'Level 3',
  'module.children': 'Children',
  'module.grammar': 'Grammar',
};

test('every production lesson ID resolves to exactly 3 quiz questions', () => {
  const counts = assertEveryLessonQuiz(course, 3);
  assert.equal(counts.lessons, 50);
  assert.equal(counts.questions, 150);
});

for (const [moduleId, label] of Object.entries(expected)) {
  test(`${label}: 10/10 lessons have a working 3-question quiz`, () => {
    const module = course.modules.find(m => m.id === moduleId);
    assert.ok(module, moduleId);
    assert.equal(module.lessons.length, 10);
    for (const lesson of module.lessons) {
      const questions = questionsForLessonId(course, lesson.id);
      assert.equal(questions.length, 3, lesson.id);
      assert.ok(lesson.quiz, `${lesson.id} missing quiz reference`);
      assert.equal(lessonById(course, lesson.id)?.quiz.id, lesson.quiz.id);
    }
  });
}

test('fails if a lesson can produce an empty quiz', () => {
  const copy = structuredClone(course);
  copy.modules[0].lessons[0].quiz.questions = [];
  assert.throws(() => assertEveryLessonQuiz(copy, 3), /empty or incomplete quiz/);
});

test('fails if a lesson quiz reference is missing', () => {
  const copy = structuredClone(course);
  delete copy.modules[1].lessons[4].quiz;
  assert.throws(() => assertEveryLessonQuiz(copy, 3), /empty or incomplete quiz/);
});

test('fails if question text is blank', () => {
  const copy = structuredClone(course);
  copy.modules[2].lessons[0].quiz.questions[0].prompt.support = '   ';
  assert.throws(() => assertEveryLessonQuiz(copy, 3), /empty question text/);
});
