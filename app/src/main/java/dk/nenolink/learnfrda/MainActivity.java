package dk.nenolink.learnfrda;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import dk.nenolink.learnfrda.content.ContentContractException;
import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.Course;
import dk.nenolink.learnfrda.content.ContentModels.Item;
import dk.nenolink.learnfrda.content.ContentModels.Lesson;
import dk.nenolink.learnfrda.content.ContentModels.Module;
import dk.nenolink.learnfrda.content.ContentModels.Note;
import dk.nenolink.learnfrda.content.ContentModels.Question;
import dk.nenolink.learnfrda.content.ContentModels.Quiz;
import dk.nenolink.learnfrda.content.ContentRepository;
import dk.nenolink.learnfrda.progress.ProgressStore;
import dk.nenolink.learnfrda.speech.SpeechController;

public final class MainActivity extends Activity implements SpeechController.Listener {
    private static final int SPACE = 12;

    private Course course;
    private ProgressStore progress;
    private SpeechController speech;
    private LinearLayout content;
    private Module selectedModule;
    private Lesson selectedLesson;
    private int itemIndex;
    private int questionIndex;
    private int quizScore;
    private boolean questionAnswered;
    private Screen screen = Screen.MODULES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressStore(this);
        speech = new SpeechController(this);
        try {
            course = new ContentRepository(this).loadProductionCourse();
            setContentView(buildShell());
            showModules();
        } catch (ContentContractException exception) {
            showFatalError(exception.getMessage());
        }
    }

    private View buildShell() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setPadding(dp(20), dp(24), dp(20), dp(32));
        shell.setBackgroundColor(color(R.color.background));
        scroll.addView(shell, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView appTitle = text(getString(R.string.app_name), 28, R.color.primary_dark, true);
        appTitle.setGravity(Gravity.CENTER);
        shell.addView(appTitle, matchWrap());

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        shell.addView(content, matchWrap());
        return scroll;
    }

    private void showModules() {
        screen = Screen.MODULES;
        selectedModule = null;
        selectedLesson = null;
        clear();
        heading(getString(R.string.modules_title));
        centered(getString(R.string.modules_intro));

        for (Module module : course.modules) {
            String progressText = moduleProgress(module);
            Button button = primaryButton(module.title.support + "\n" + module.title.target + progressText);
            button.setOnClickListener(view -> showLessons(module));
            content.addView(button, matchWrapWithTop());
        }

        Button grammar = secondaryButton(getString(R.string.planned_grammar));
        grammar.setOnClickListener(view -> planned());
        content.addView(grammar, matchWrapWithTop());

        String last = progress.getLastPosition();
        if (!last.isEmpty()) {
            panel("Dernière position enregistrée\n" + last, R.color.panel);
        }
    }

    private String moduleProgress(Module module) {
        int completed = 0;
        int total = 0;
        for (Lesson lesson : module.lessons) {
            completed += progress.countCompleted(course.id, module.id, lesson.id);
            total += lesson.items.size();
        }
        return "\n" + completed + "/" + total + " éléments terminés";
    }

    private void planned() {
        Toast.makeText(this, R.string.planned_message, Toast.LENGTH_LONG).show();
    }

    private void showLessons(Module module) {
        selectedModule = module;
        selectedLesson = null;
        screen = Screen.LESSONS;
        clear();
        backButton(this::showModules);
        heading(module.title.support);
        targetLabel(module.title.target);

        for (Lesson lesson : module.lessons) {
            int completed = progress.countCompleted(course.id, module.id, lesson.id);
            Button lessonButton = primaryButton(lesson.title.support + "\n" + lesson.title.target
                    + "\n" + completed + "/" + lesson.items.size() + " éléments terminés");
            lessonButton.setOnClickListener(view -> showLessonOverview(lesson));
            content.addView(lessonButton, matchWrapWithTop());
        }
    }

    private void showLessonOverview(Lesson lesson) {
        selectedLesson = lesson;
        screen = Screen.LESSON;
        clear();
        backButton(() -> showLessons(selectedModule));
        heading(lesson.title.support);
        targetLabel(lesson.title.target);
        panel(lesson.situation.support + "\n\n" + lesson.situation.target, R.color.panel);

        int completed = progress.countCompleted(course.id, selectedModule.id, lesson.id);
        centered(completed + "/" + lesson.items.size() + " éléments terminés");

        Button start = primaryButton("Commencer la leçon");
        start.setOnClickListener(view -> showItem(0));
        content.addView(start, matchWrapWithTop());

        if (lesson.quiz != null) {
            Button quiz = accentButton(getString(R.string.open_quiz) + quizSavedSuffix(lesson.quiz));
            quiz.setOnClickListener(view -> startQuiz());
            content.addView(quiz, matchWrapWithTop());
        }
    }

    private String quizSavedSuffix(Quiz quiz) {
        String id = progress.progressId(course.id, selectedModule.id, selectedLesson.id, quiz.id);
        String result = progress.quizResult(id);
        return result.isEmpty() ? "" : "\nRésultat enregistré : " + result;
    }

    private void showItem(int requestedIndex) {
        if (selectedLesson == null || selectedLesson.items.isEmpty()) return;
        itemIndex = Math.max(0, Math.min(requestedIndex, selectedLesson.items.size() - 1));
        screen = Screen.ITEM;
        Item item = selectedLesson.items.get(itemIndex);
        String progressId = progress.progressId(course.id, selectedModule.id, selectedLesson.id, item.id);
        progress.saveLastPosition(progressId);

        clear();
        backButton(() -> showLessonOverview(selectedLesson));
        centered("Élément " + (itemIndex + 1) + " sur " + selectedLesson.items.size());
        if (!item.speaker.isEmpty()) centered("Personnage " + item.speaker);

        TextView target = text(item.text.target, 30, R.color.text, true);
        target.setGravity(Gravity.CENTER);
        target.setPadding(dp(16), dp(24), dp(16), dp(24));
        target.setBackground(panelBackground(R.color.panel));
        content.addView(target, matchWrapWithTop());

        TextView support = text(item.text.support, 18, R.color.muted, false);
        support.setGravity(Gravity.CENTER);
        support.setPadding(dp(8), dp(14), dp(8), dp(8));
        content.addView(support, matchWrap());

        addNotes(item.notes);

        if (item.speech.enabled) {
            Button danish = primaryButton(getString(R.string.listen_danish));
            danish.setOnClickListener(view -> speech.speak("support".equals(item.speech.role) ? item.text.support : item.text.target, item.speech.locale, this));
            content.addView(danish, matchWrapWithTop());
        }
        Button french = secondaryButton(getString(R.string.listen_french));
        french.setOnClickListener(view -> speech.speak(item.text.support, course.speech.supportLocale, this));
        content.addView(french, matchWrapWithTop());

        boolean complete = progress.isItemComplete(progressId);
        Button completion = accentButton(complete ? getString(R.string.completed) : getString(R.string.mark_complete));
        completion.setEnabled(!complete);
        completion.setOnClickListener(view -> {
            progress.markItemComplete(progressId);
            showItem(itemIndex);
        });
        content.addView(completion, matchWrapWithTop());

        LinearLayout navigation = row();
        Button previous = secondaryButton(getString(R.string.previous));
        previous.setEnabled(itemIndex > 0);
        previous.setOnClickListener(view -> showItem(itemIndex - 1));
        navigation.addView(previous, weighted());
        Button next = primaryButton(itemIndex + 1 < selectedLesson.items.size()
                ? getString(R.string.next) : getString(R.string.open_quiz));
        next.setOnClickListener(view -> {
            if (itemIndex + 1 < selectedLesson.items.size()) showItem(itemIndex + 1);
            else if (selectedLesson.quiz != null) startQuiz();
            else showLessonOverview(selectedLesson);
        });
        navigation.addView(next, weightedWithLeft());
        content.addView(navigation, matchWrapWithTop());
    }

    private void addNotes(Map<String, Note> notes) {
        addNote(notes, "grammar", "Grammaire");
        addNote(notes, "cultural", "Contexte culturel");
        addNote(notes, "digital", "Danemark numérique");
        addNote(notes, "pronunciation", "Prononciation");
    }

    private void addNote(Map<String, Note> notes, String key, String label) {
        Note note = notes.get(key);
        if (note == null) return;
        String text = label + "\n" + note.support;
        if (!note.targetDetail.isEmpty()) text += "\n" + note.targetDetail;
        panel(text, R.color.panel);
    }

    private void startQuiz() {
        if (selectedLesson == null || selectedLesson.quiz == null) return;
        questionIndex = 0;
        quizScore = 0;
        questionAnswered = false;
        showQuestion();
    }

    private void showQuestion() {
        Quiz quiz = selectedLesson.quiz;
        if (questionIndex >= quiz.questions.size()) {
            showQuizResult();
            return;
        }
        screen = Screen.QUIZ;
        questionAnswered = false;
        Question question = quiz.questions.get(questionIndex);
        clear();
        backButton(() -> showLessonOverview(selectedLesson));
        heading(quiz.title.support);
        centered("Question " + (questionIndex + 1) + " sur " + quiz.questions.size());
        panel(question.prompt.support + "\n\n" + question.prompt.target, R.color.panel);

        for (Answer answer : question.answers) {
            Button option = primaryButton(answer.text.target + "\n" + answer.text.support);
            option.setOnClickListener(view -> handleAnswer(answer, question));
            content.addView(option, matchWrapWithTop());
        }
    }

    private void handleAnswer(Answer answer, Question question) {
        if (questionAnswered) return;
        questionAnswered = true;
        if (answer.correct) quizScore++;
        clear();
        backButton(() -> showLessonOverview(selectedLesson));
        heading(answer.correct ? getString(R.string.correct) : getString(R.string.incorrect));
        panel(question.explanation.support + "\n\n" + question.explanation.target,
                answer.correct ? R.color.primary : R.color.panel);
        Button next = primaryButton(getString(R.string.next_question));
        next.setOnClickListener(view -> {
            questionIndex++;
            showQuestion();
        });
        content.addView(next, matchWrapWithTop());
    }

    private void showQuizResult() {
        screen = Screen.QUIZ_RESULT;
        Quiz quiz = selectedLesson.quiz;
        String progressId = progress.progressId(course.id, selectedModule.id, selectedLesson.id, quiz.id);
        progress.saveQuizResult(progressId, quizScore, quiz.questions.size());
        clear();
        heading(getString(R.string.quiz_result, quizScore, quiz.questions.size()));
        centered(getString(R.string.quiz_saved));
        Button repeat = primaryButton(getString(R.string.repeat_quiz));
        repeat.setOnClickListener(view -> startQuiz());
        content.addView(repeat, matchWrapWithTop());
        Button lesson = secondaryButton(getString(R.string.back));
        lesson.setOnClickListener(view -> showLessonOverview(selectedLesson));
        content.addView(lesson, matchWrapWithTop());
    }

    @Override
    public void onUnavailable(String localeTag) {
        Toast.makeText(this, getString(R.string.tts_unavailable, localeTag), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, R.string.tts_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        switch (screen) {
            case MODULES:
                super.onBackPressed();
                break;
            case LESSONS:
                showModules();
                break;
            case LESSON:
                showLessons(selectedModule);
                break;
            case ITEM:
            case QUIZ:
            case QUIZ_RESULT:
                showLessonOverview(selectedLesson);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        speech.shutdown();
        super.onDestroy();
    }

    private void showFatalError(String detail) {
        LinearLayout error = new LinearLayout(this);
        error.setOrientation(LinearLayout.VERTICAL);
        error.setPadding(dp(24), dp(32), dp(24), dp(32));
        error.setBackgroundColor(color(R.color.background));
        error.addView(text(getString(R.string.content_error), 24, R.color.accent, true), matchWrap());
        error.addView(text(detail, 14, R.color.text, false), matchWrapWithTop());
        setContentView(error);
    }

    private void clear() {
        content.removeAllViews();
    }

    private void heading(String value) {
        TextView heading = text(value, 24, R.color.primary_dark, true);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, dp(10), 0, dp(8));
        content.addView(heading, matchWrap());
    }

    private void targetLabel(String value) {
        TextView target = text(value, 18, R.color.text, true);
        target.setGravity(Gravity.CENTER);
        content.addView(target, matchWrap());
    }

    private void centered(String value) {
        TextView view = text(value, 15, R.color.muted, false);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, dp(6), 0, dp(8));
        content.addView(view, matchWrap());
    }

    private void panel(String value, int colorResource) {
        TextView panel = text(value, 15, R.color.text, false);
        panel.setPadding(dp(16), dp(14), dp(16), dp(14));
        panel.setBackground(panelBackground(colorResource));
        content.addView(panel, matchWrapWithTop());
    }

    private void backButton(Runnable action) {
        Button back = secondaryButton(getString(R.string.back));
        back.setOnClickListener(view -> action.run());
        content.addView(back, wrapWithBottom());
    }

    private TextView text(String value, int sp, int colorResource, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(color(colorResource));
        view.setLineSpacing(0, 1.12f);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private Button primaryButton(String label) {
        return button(label, R.color.primary, R.color.primary_dark);
    }

    private Button secondaryButton(String label) {
        return button(label, R.color.panel, R.color.primary_dark);
    }

    private Button accentButton(String label) {
        return button(label, R.color.accent, android.R.color.white);
    }

    private Button button(String label, int backgroundColor, int textColor) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(16);
        button.setTextColor(color(textColor));
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(52));
        button.setPadding(dp(14), dp(10), dp(14), dp(10));
        button.setBackground(panelBackground(backgroundColor));
        return button;
    }

    private GradientDrawable panelBackground(int colorResource) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(color(colorResource));
        background.setCornerRadius(dp(14));
        return background;
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private int color(int resource) {
        return getResources().getColor(resource, getTheme());
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams matchWrapWithTop() {
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(SPACE);
        return params;
    }

    private LinearLayout.LayoutParams wrapWithBottom() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(SPACE);
        return params;
    }

    private LinearLayout.LayoutParams weighted() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
    }

    private LinearLayout.LayoutParams weightedWithLeft() {
        LinearLayout.LayoutParams params = weighted();
        params.leftMargin = dp(SPACE);
        return params;
    }

    private enum Screen {
        MODULES, LESSONS, LESSON, ITEM, QUIZ, QUIZ_RESULT
    }
}
