package dk.nenolink.learnfrda.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.Course;
import dk.nenolink.learnfrda.content.ContentModels.Language;
import dk.nenolink.learnfrda.content.ContentModels.Lesson;
import dk.nenolink.learnfrda.content.ContentModels.Module;
import dk.nenolink.learnfrda.content.ContentModels.Question;
import dk.nenolink.learnfrda.content.ContentModels.Quiz;
import dk.nenolink.learnfrda.content.ContentModels.SpeechDefaults;
import dk.nenolink.learnfrda.content.ContentModels.TextPair;

public class QuizIntegrityTest {
    @Test
    public void lessonIdResolvesToThreeQuestions() throws Exception {
        Course course = courseWith(lesson("lesson.one", quizWith(3)));
        assertEquals(3, QuizIntegrity.questionsForLessonId(course, "lesson.one").size());
        QuizIntegrity.requireEveryLessonQuiz(course);
    }

    @Test
    public void emptyQuizFailsIntegrity() {
        Course course = courseWith(lesson("lesson.empty", quizWith(0)));
        assertTrue(questionsForLessonIdSilent(course, "lesson.empty").isEmpty());
        try {
            QuizIntegrity.requireEveryLessonQuiz(course);
            fail("empty quiz must fail");
        } catch (ContentContractException expected) {
            assertTrue(expected.getMessage().contains("lesson.empty"));
        }
    }

    @Test
    public void missingQuizFailsIntegrity() {
        Course course = courseWith(new Lesson(
                "lesson.none", "module.one", 1,
                new TextPair("T", "T"), new TextPair("S", "S"),
                Collections.emptyList(), Collections.emptyList(), null));
        try {
            QuizIntegrity.requireEveryLessonQuiz(course);
            fail("missing quiz must fail");
        } catch (ContentContractException expected) {
            assertTrue(expected.getMessage().contains("lesson.none"));
        }
    }

    private static List<Question> questionsForLessonIdSilent(Course course, String lessonId) {
        try {
            return QuizIntegrity.questionsForLessonId(course, lessonId);
        } catch (ContentContractException exception) {
            return Collections.emptyList();
        }
    }

    private static Course courseWith(Lesson lesson) {
        Module module = new Module("module.one", "level", 1, "general",
                new TextPair("M", "M"), Collections.emptyList(), Collections.singletonList(lesson));
        return new Course(1, "test", "course.fr-da", "fr-FR",
                new Language("fr", "fr-FR"), new Language("da", "da-DK"),
                new SpeechDefaults("target", "da-DK", "fr-FR"),
                new TextPair("C", "C"), Collections.singletonList(module));
    }

    private static Lesson lesson(String id, Quiz quiz) {
        return new Lesson(id, "module.one", 1, new TextPair("L", "L"), new TextPair("S", "S"),
                Collections.emptyList(), Collections.emptyList(), quiz);
    }

    private static Quiz quizWith(int count) {
        List<Question> questions = new java.util.ArrayList<>();
        for (int index = 1; index <= count; index++) {
            questions.add(new Question(
                    "question." + index, index, "single-choice",
                    new TextPair("Prompt FR " + index, "Prompt DA " + index),
                    Arrays.asList(
                            new Answer("answer." + index + ".c", new TextPair("Oui", "Ja"), true),
                            new Answer("answer." + index + ".w", new TextPair("Non", "Nej"), false)
                    ),
                    new TextPair("Explication", "Forklaring"),
                    Collections.emptyList()));
        }
        return new Quiz("quiz.one", new TextPair("Quiz", "Quiz"), questions);
    }
}
