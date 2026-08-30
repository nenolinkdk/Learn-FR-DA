import assert from 'node:assert/strict';

function blank(value) {
  return typeof value !== 'string' || value.trim().length === 0;
}

export function questionsForLesson(lesson) {
  return lesson?.quiz?.questions ?? [];
}

export function lessonById(course, lessonId) {
  for (const module of course.modules) {
    for (const lesson of module.lessons) {
      if (lesson.id === lessonId) return lesson;
    }
  }
  return null;
}

export function questionsForLessonId(course, lessonId) {
  return questionsForLesson(lessonById(course, lessonId));
}

/** Per-lesson mapping: every lesson ID must resolve to exactly three displayable questions. */
export function assertEveryLessonQuiz(course, expectedQuestions = 3) {
  assert.ok(course?.modules?.length, 'course has no modules');
  const ids = new Set();
  let lessons = 0;
  let questions = 0;
  for (const module of course.modules) {
    for (const lesson of module.lessons) {
      lessons++;
      assert.equal(lesson.moduleId, module.id, `${lesson.id}: moduleId mismatch`);
      assert.notEqual(lessonById(course, lesson.id), null, `${lesson.id}: lesson ID does not resolve`);
      const resolved = questionsForLessonId(course, lesson.id);
      assert.equal(resolved.length, expectedQuestions, `${lesson.id}: empty or incomplete quiz`);
      assert.ok(lesson.quiz?.id, `${lesson.id}: missing quiz id`);
      let previousOrder = 0;
      for (const question of resolved) {
        questions++;
        assert.ok(question.id && !ids.has(question.id), `${question.id}: invalid/duplicate question ID`);
        ids.add(question.id);
        assert.ok(!blank(question.prompt?.support) && !blank(question.prompt?.target), `${question.id}: empty question text`);
        assert.ok(Array.isArray(question.answers) && question.answers.length >= 2, `${question.id}: missing answers`);
        let correct = 0;
        for (const answer of question.answers) {
          assert.ok(answer.id && !ids.has(answer.id), `${answer.id}: invalid/duplicate answer ID`);
          ids.add(answer.id);
          assert.ok(!blank(answer.text?.support) && !blank(answer.text?.target), `${answer.id}: empty answer text`);
          if (answer.correct) correct++;
        }
        assert.equal(correct, 1, `${question.id}: exactly one correct answer required`);
        assert.ok(Number.isInteger(question.order) && question.order > previousOrder, `${question.id}: order mismatch`);
        previousOrder = question.order;
      }
    }
  }
  return { lessons, questions };
}
