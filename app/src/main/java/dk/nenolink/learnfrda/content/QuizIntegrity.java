package dk.nenolink.learnfrda.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.Course;
import dk.nenolink.learnfrda.content.ContentModels.Lesson;
import dk.nenolink.learnfrda.content.ContentModels.Module;
import dk.nenolink.learnfrda.content.ContentModels.Question;

/**
 * Runtime mapping used by the lesson quiz screen: lesson → quiz → questions.
 * Production lessons must resolve to exactly three displayable questions.
 */
public final class QuizIntegrity {
    public static final int PRODUCTION_QUESTIONS_PER_LESSON = 3;

    private QuizIntegrity() {}

    public static List<Question> questionsForLesson(Lesson lesson) {
        if (lesson == null || lesson.quiz == null || lesson.quiz.questions == null) {
            return Collections.emptyList();
        }
        return lesson.quiz.questions;
    }

    public static Lesson lessonById(Course course, String lessonId) throws ContentContractException {
        if (course == null || lessonId == null) {
            throw new ContentContractException("lesson lookup requires a course and lesson ID");
        }
        for (Module module : course.modules) {
            for (Lesson lesson : module.lessons) {
                if (lessonId.equals(lesson.id)) return lesson;
            }
        }
        throw new ContentContractException("unknown lesson ID: " + lessonId);
    }

    public static List<Question> questionsForLessonId(Course course, String lessonId)
            throws ContentContractException {
        return questionsForLesson(lessonById(course, lessonId));
    }

    public static List<String> productionLessonIds(Course course) {
        List<String> ids = new ArrayList<>();
        if (course == null) return ids;
        for (Module module : course.modules) {
            for (Lesson lesson : module.lessons) ids.add(lesson.id);
        }
        return ids;
    }

    public static void requireEveryLessonQuiz(Course course) throws ContentContractException {
        requireEveryLessonQuiz(course, PRODUCTION_QUESTIONS_PER_LESSON);
    }

    public static void requireEveryLessonQuiz(Course course, int expectedQuestions)
            throws ContentContractException {
        if (course == null) throw new ContentContractException("course required for quiz integrity");
        int lessons = 0;
        for (Module module : course.modules) {
            for (Lesson lesson : module.lessons) {
                lessons++;
                requireResolvedQuiz(module.id, lesson, expectedQuestions);
            }
        }
        if (lessons == 0) throw new ContentContractException("course has no lessons");
    }

    private static void requireResolvedQuiz(String moduleId, Lesson lesson, int expectedQuestions)
            throws ContentContractException {
        if (lesson.quiz == null) {
            throw new ContentContractException(lesson.id + ": lesson has no quiz");
        }
        if (!moduleId.equals(lesson.moduleId)) {
            throw new ContentContractException(lesson.id + ": moduleId does not match " + moduleId);
        }
        List<Question> questions = questionsForLesson(lesson);
        if (questions.size() != expectedQuestions) {
            throw new ContentContractException(lesson.id + ": expected " + expectedQuestions
                    + " quiz questions, found " + questions.size());
        }
        int previousOrder = 0;
        for (Question question : questions) {
            requireQuestion(lesson.id, question);
            if (question.order <= previousOrder) {
                throw new ContentContractException(question.id + ": question order is not strictly increasing");
            }
            previousOrder = question.order;
        }
    }

    private static void requireQuestion(String lessonId, Question question) throws ContentContractException {
        if (question == null) throw new ContentContractException(lessonId + ": null quiz question");
        if (blank(question.prompt.support) || blank(question.prompt.target)) {
            throw new ContentContractException(question.id + ": empty question text");
        }
        if (question.answers == null || question.answers.size() < 2) {
            throw new ContentContractException(question.id + ": missing answer choices");
        }
        int correct = 0;
        for (Answer answer : question.answers) {
            if (blank(answer.text.support) || blank(answer.text.target)) {
                throw new ContentContractException(answer.id + ": empty answer text");
            }
            if (answer.correct) correct++;
        }
        if (correct != 1) {
            throw new ContentContractException(question.id + ": exactly one correct answer required");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
