package dk.nenolink.learnfrda.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.Course;
import dk.nenolink.learnfrda.content.ContentModels.Language;
import dk.nenolink.learnfrda.content.ContentModels.Lesson;
import dk.nenolink.learnfrda.content.ContentModels.Module;
import dk.nenolink.learnfrda.content.ContentModels.Question;
import dk.nenolink.learnfrda.content.ContentModels.Quiz;
import dk.nenolink.learnfrda.content.ContentModels.SpeechDefaults;
import dk.nenolink.learnfrda.content.ContentModels.TextPair;

/**
 * Walks the bundled production course with the same lesson → quiz → questions
 * mapping MainActivity uses ({@link QuizIntegrity}).
 */
public class ProductionCourseQuizTest {
    @Test
    public void everyBundledLessonIdResolvesToThreeQuestions() throws Exception {
        Path asset = Path.of("src/main/assets/content/fr-da/course.json");
        String json = new String(Files.readAllBytes(asset), StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(json);
        Course course = courseFrom(root);
        List<String> lessonIds = QuizIntegrity.productionLessonIds(course);
        assertEquals(50, lessonIds.size());
        int questions = 0;
        for (String lessonId : lessonIds) {
            Lesson lesson = QuizIntegrity.lessonById(course, lessonId);
            assertNotNull(lessonId, lesson.quiz);
            List<Question> resolved = QuizIntegrity.questionsForLessonId(course, lessonId);
            assertEquals(lessonId + " must not produce an empty quiz", 3, resolved.size());
            for (Question question : resolved) {
                assertTrue(question.prompt.support.trim().length() > 0);
                assertTrue(question.prompt.target.trim().length() > 0);
                assertTrue(question.answers.size() >= 2);
                QuizIntegrity.requireAnswerDisplayRole(question.answerDisplayRole, question.id);
                java.util.Set<String> displayed = new java.util.HashSet<>();
                int correct = 0;
                for (Answer answer : question.answers) {
                    String button = QuizIntegrity.displayedAnswerText(question, answer);
                    assertTrue(question.id, button.trim().length() > 0);
                    assertTrue(question.id + " duplicate displayed answer", displayed.add(button.trim()));
                    assertTrue(question.id + " must not leak both roles",
                            !QuizIntegrity.leaksBothRoles(answer.text, button));
                    assertEquals(button, question.answerDisplayRole.equals("support")
                            ? answer.text.support : answer.text.target);
                    if (answer.correct) correct++;
                }
                assertEquals(question.id + ": exactly one correct answer", 1, correct);
            }
            questions += resolved.size();
        }
        assertEquals(150, questions);
        QuizIntegrity.requireEveryLessonQuiz(course);
    }

    @Test
    public void everyProductionQuestionShufflesWithoutLosingIdentity() throws Exception {
        Course course = courseFrom(root());
        assertEquals(5, course.modules.size());
        assertEquals(50, QuizIntegrity.productionLessonIds(course).size());
        boolean[] seen = new boolean[3];
        int firstPositionHits = 0;
        int shuffleTrials = 0;
        int questions = 0;
        for (String lessonId : QuizIntegrity.productionLessonIds(course)) {
            List<Question> resolved = QuizIntegrity.questionsForLessonId(course, lessonId);
            assertEquals(3, resolved.size());
            int lessonScoreCorrect = 0;
            int lessonScoreWrong = 0;
            for (Question question : resolved) {
                questions++;
                QuizIntegrity.requireAnswerDisplayRole(question.answerDisplayRole, question.id);
                assertEquals(3, question.answers.size());
                String storedCorrectId = correctId(question.answers);
                Set<String> storedIds = ids(question.answers);
                List<String> storedOrder = idsInOrder(question.answers);
                boolean[] questionSeen = new boolean[3];
                for (int seed = 0; seed < 80; seed++) {
                    List<Answer> displayed = AnswerOrder.shuffleAnswers(question.answers, new Random(seed));
                    assertEquals(question.id, 3, displayed.size());
                    assertEquals(question.id, storedIds, ids(displayed));
                    assertEquals(question.id, 1, countCorrect(displayed));
                    assertEquals(question.id, storedCorrectId, correctId(displayed));
                    assertEquals(storedOrder, idsInOrder(question.answers));
                    String button = QuizIntegrity.displayedAnswerText(question, displayed.get(AnswerOrder.indexOfCorrect(displayed)));
                    assertTrue(question.id, button.trim().length() > 0);
                    assertFalse(question.id, QuizIntegrity.leaksBothRoles(
                            displayed.get(AnswerOrder.indexOfCorrect(displayed)).text, button));
                    int position = AnswerOrder.indexOfCorrect(displayed);
                    questionSeen[position] = true;
                    seen[position] = true;
                    shuffleTrials++;
                    if (position == 0) firstPositionHits++;
                }
                assertTrue(question.id + " position 1", questionSeen[0]);
                assertTrue(question.id + " position 2", questionSeen[1]);
                assertTrue(question.id + " position 3", questionSeen[2]);

                List<Answer> play = AnswerOrder.shuffleAnswers(question.answers, new Random(lessonId.hashCode() + question.order));
                Answer correct = play.get(AnswerOrder.indexOfCorrect(play));
                Answer wrong = play.get(AnswerOrder.indexOfCorrect(play) == 0 ? 1 : 0);
                assertTrue(AnswerOrder.scoresCorrect(correct));
                assertFalse(AnswerOrder.scoresCorrect(wrong));
                if (AnswerOrder.scoresCorrect(correct)) lessonScoreCorrect++;
                if (!AnswerOrder.scoresCorrect(wrong)) lessonScoreWrong++;
            }
            assertEquals(lessonId, 3, lessonScoreCorrect);
            assertEquals(lessonId, 3, lessonScoreWrong);
        }
        assertEquals(150, questions);
        assertTrue("position 1 never correct across the course", seen[0]);
        assertTrue("position 2 never correct across the course", seen[1]);
        assertTrue("position 3 never correct across the course", seen[2]);
        assertTrue("correct answer was systematically first", firstPositionHits < shuffleTrials);
        assertTrue("correct answer never first", firstPositionHits > 0);
    }

    private static JSONObject root() throws Exception {
        Path asset = Path.of("src/main/assets/content/fr-da/course.json");
        return new JSONObject(new String(Files.readAllBytes(asset), StandardCharsets.UTF_8));
    }

    private static Set<String> ids(List<Answer> answers) {
        Set<String> ids = new HashSet<>();
        for (Answer answer : answers) ids.add(answer.id);
        return ids;
    }

    private static List<String> idsInOrder(List<Answer> answers) {
        List<String> ids = new ArrayList<>();
        for (Answer answer : answers) ids.add(answer.id);
        return ids;
    }

    private static int countCorrect(List<Answer> answers) {
        int count = 0;
        for (Answer answer : answers) if (answer.correct) count++;
        return count;
    }

    private static String correctId(List<Answer> answers) {
        for (Answer answer : answers) if (answer.correct) return answer.id;
        return null;
    }

    private static Course courseFrom(JSONObject root) throws Exception {
        JSONObject courseJson = root.getJSONObject("course");
        JSONArray modulesJson = courseJson.getJSONArray("modules");
        List<Module> modules = new ArrayList<>();
        for (int m = 0; m < modulesJson.length(); m++) {
            JSONObject moduleJson = modulesJson.getJSONObject(m);
            JSONArray lessonsJson = moduleJson.getJSONArray("lessons");
            List<Lesson> lessons = new ArrayList<>();
            for (int l = 0; l < lessonsJson.length(); l++) {
                lessons.add(lessonFrom(moduleJson.getJSONArray("lessons").getJSONObject(l)));
            }
            Integer level = moduleJson.has("level") ? moduleJson.getInt("level") : null;
            modules.add(new Module(
                    moduleJson.getString("id"),
                    moduleJson.getString("type"),
                    level,
                    moduleJson.getString("audience"),
                    pair(moduleJson.getJSONObject("title")),
                    Collections.emptyList(),
                    lessons));
        }
        return new Course(
                root.getInt("schemaVersion"),
                root.getString("contentVersion"),
                courseJson.getString("id"),
                courseJson.getString("courseLocale"),
                new Language("fr", "fr-FR"),
                new Language("da", "da-DK"),
                new SpeechDefaults("target", "da-DK", "fr-FR"),
                pair(courseJson.getJSONObject("title")),
                modules);
    }

    private static Lesson lessonFrom(JSONObject json) throws Exception {
        Quiz quiz = null;
        if (json.has("quiz")) {
            JSONObject quizJson = json.getJSONObject("quiz");
            JSONArray questionsJson = quizJson.getJSONArray("questions");
            List<Question> questions = new ArrayList<>();
            for (int q = 0; q < questionsJson.length(); q++) {
                JSONObject questionJson = questionsJson.getJSONObject(q);
                JSONArray answersJson = questionJson.getJSONArray("answers");
                List<Answer> answers = new ArrayList<>();
                for (int a = 0; a < answersJson.length(); a++) {
                    JSONObject answerJson = answersJson.getJSONObject(a);
                    answers.add(new Answer(
                            answerJson.getString("id"),
                            pair(answerJson.getJSONObject("text")),
                            answerJson.getBoolean("correct")));
                }
                questions.add(new Question(
                        questionJson.getString("id"),
                        questionJson.getInt("order"),
                        questionJson.getString("type"),
                        questionJson.getString("answerDisplayRole"),
                        pair(questionJson.getJSONObject("prompt")),
                        answers,
                        pair(questionJson.getJSONObject("explanation")),
                        Collections.emptyList()));
            }
            quiz = new Quiz(quizJson.getString("id"), pair(quizJson.getJSONObject("title")), questions);
        }
        return new Lesson(
                json.getString("id"),
                json.getString("moduleId"),
                json.getInt("order"),
                pair(json.getJSONObject("title")),
                pair(json.getJSONObject("situation")),
                Collections.emptyList(),
                Collections.emptyList(),
                quiz);
    }

    private static TextPair pair(JSONObject json) throws Exception {
        return new TextPair(json.getString("support"), json.getString("target"));
    }
}
