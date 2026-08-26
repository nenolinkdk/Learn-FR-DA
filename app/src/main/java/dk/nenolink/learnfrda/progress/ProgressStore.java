package dk.nenolink.learnfrda.progress;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ProgressStore {
    private static final String PREFERENCES = "nenoling_progress_v1";
    private static final String COMPLETED_ITEMS = "completed_items";
    private static final String LAST_POSITION = "last_position";
    private static final String QUIZ_SCORE_PREFIX = "quiz_score.";
    private static final String QUIZ_TOTAL_PREFIX = "quiz_total.";

    private final SharedPreferences preferences;

    public ProgressStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String progressId(String courseId, String moduleId, String lessonId, String entityId) {
        return courseId + "|" + moduleId + "|" + lessonId + "|" + entityId;
    }

    public void markItemComplete(String progressId) {
        Set<String> completed = new HashSet<>(preferences.getStringSet(COMPLETED_ITEMS, Collections.emptySet()));
        completed.add(progressId);
        preferences.edit().putStringSet(COMPLETED_ITEMS, completed).apply();
    }

    public boolean isItemComplete(String progressId) {
        return preferences.getStringSet(COMPLETED_ITEMS, Collections.emptySet()).contains(progressId);
    }

    public int countCompleted(String courseId, String moduleId, String lessonId) {
        String prefix = courseId + "|" + moduleId + "|" + lessonId + "|";
        int count = 0;
        for (String id : preferences.getStringSet(COMPLETED_ITEMS, Collections.emptySet())) {
            if (id.startsWith(prefix)) count++;
        }
        return count;
    }

    public void saveLastPosition(String progressId) {
        preferences.edit().putString(LAST_POSITION, progressId).apply();
    }

    public String getLastPosition() {
        return preferences.getString(LAST_POSITION, "");
    }

    public void saveQuizResult(String quizProgressId, int score, int total) {
        preferences.edit()
                .putInt(QUIZ_SCORE_PREFIX + quizProgressId, score)
                .putInt(QUIZ_TOTAL_PREFIX + quizProgressId, total)
                .apply();
    }

    public boolean hasQuizResult(String quizProgressId) {
        return preferences.contains(QUIZ_TOTAL_PREFIX + quizProgressId);
    }

    public String quizResult(String quizProgressId) {
        if (!hasQuizResult(quizProgressId)) return "";
        return preferences.getInt(QUIZ_SCORE_PREFIX + quizProgressId, 0)
                + "/" + preferences.getInt(QUIZ_TOTAL_PREFIX + quizProgressId, 0);
    }
}
