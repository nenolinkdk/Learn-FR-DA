package dk.nenolink.learnfrda.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.TextPair;

public class AnswerOrderTest {
    @Test
    public void shuffleDoesNotMutateStoredListOrIdentity() {
        List<Answer> stored = sample();
        List<Answer> displayed = AnswerOrder.shuffleAnswers(stored, new Random(1));
        assertEquals(3, displayed.size());
        assertEquals("answer.a", stored.get(0).id);
        assertTrue(stored.get(0).correct);
        assertFalse(stored.get(1).correct);
        Set<String> storedIds = ids(stored);
        Set<String> displayedIds = ids(displayed);
        assertEquals(storedIds, displayedIds);
        assertNotSame(stored, displayed);
        assertEquals(1, countCorrect(displayed));
        assertEquals("answer.a", correctId(displayed));
    }

    @Test
    public void scoringUsesCorrectFlagNotPosition() {
        List<Answer> stored = sample();
        List<Answer> displayed = AnswerOrder.shuffleAnswers(stored, new Random(42));
        int position = AnswerOrder.indexOfCorrect(displayed);
        assertTrue(position >= 0 && position < 3);
        assertTrue(AnswerOrder.scoresCorrect(displayed.get(position)));
        for (int index = 0; index < displayed.size(); index++) {
            if (index == position) continue;
            assertFalse(AnswerOrder.scoresCorrect(displayed.get(index)));
        }
        int score = 0;
        for (Answer answer : displayed) {
            if (AnswerOrder.scoresCorrect(answer)) score++;
        }
        assertEquals(1, score);
    }

    @Test
    public void correctAnswerCanAppearInEachPosition() {
        List<Answer> stored = sample();
        boolean[] seen = new boolean[3];
        for (int seed = 0; seed < 200; seed++) {
            List<Answer> displayed = AnswerOrder.shuffleAnswers(stored, new Random(seed));
            assertEquals(3, displayed.size());
            assertEquals(ids(stored), ids(displayed));
            assertEquals("answer.a", correctId(displayed));
            seen[AnswerOrder.indexOfCorrect(displayed)] = true;
        }
        assertTrue("position 1 never held the correct answer", seen[0]);
        assertTrue("position 2 never held the correct answer", seen[1]);
        assertTrue("position 3 never held the correct answer", seen[2]);
    }

    @Test
    public void sameSeedKeepsOrderUntilNextPresentation() {
        List<Answer> stored = sample();
        List<Answer> first = AnswerOrder.shuffleAnswers(stored, new Random(7));
        List<Answer> same = AnswerOrder.shuffleAnswers(stored, new Random(7));
        assertEquals(idsInOrder(first), idsInOrder(same));
        List<Answer> nextQuestion = AnswerOrder.shuffleAnswers(stored, new Random(8));
        assertEquals(ids(stored), ids(nextQuestion));
    }

    private static List<Answer> sample() {
        return new ArrayList<>(Arrays.asList(
                new Answer("answer.a", new TextPair("conditions", "betingelser"), true),
                new Answer("answer.b", new TextPair("chiffre d’affaires", "omsætning"), false),
                new Answer("answer.c", new TextPair("virement", "bankoverførsel"), false)
        ));
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
}
