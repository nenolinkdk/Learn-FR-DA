package dk.nenolink.learnfrda.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dk.nenolink.learnfrda.content.ContentModels.Answer;

/**
 * Display-time answer permutation. Stored course lists stay in JSON order;
 * scoring uses {@link Answer#correct} / answer IDs, never the button index.
 */
public final class AnswerOrder {
    private AnswerOrder() {}

    public static List<Answer> shuffleAnswers(List<Answer> stored) {
        return shuffleAnswers(stored, new Random());
    }

    public static List<Answer> shuffleAnswers(List<Answer> stored, Random random) {
        if (stored == null) {
            throw new IllegalArgumentException("stored answers required");
        }
        if (random == null) {
            throw new IllegalArgumentException("random required");
        }
        List<Answer> displayed = new ArrayList<>(stored);
        Collections.shuffle(displayed, random);
        return Collections.unmodifiableList(displayed);
    }

    public static boolean scoresCorrect(Answer selected) {
        return selected != null && selected.correct;
    }

    public static int indexOfCorrect(List<Answer> displayed) {
        if (displayed == null) return -1;
        for (int index = 0; index < displayed.size(); index++) {
            if (displayed.get(index) != null && displayed.get(index).correct) return index;
        }
        return -1;
    }
}
