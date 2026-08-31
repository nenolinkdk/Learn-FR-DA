package dk.nenolink.learnfrda;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
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
import dk.nenolink.learnfrda.content.QuizIntegrity;
import dk.nenolink.learnfrda.content.ResourceModels.ExternalResource;
import dk.nenolink.learnfrda.content.ResourceModels.ResourceCollection;
import dk.nenolink.learnfrda.content.ResourceRepository;
import dk.nenolink.learnfrda.progress.ProgressStore;
import dk.nenolink.learnfrda.speech.SpeechController;
import dk.nenolink.learnfrda.ui.RoundNavBar;

public final class MainActivity extends Activity implements SpeechController.Listener {
    private static final int SPACE = 8;

    private Course course;
    private ResourceCollection transportResources;
    private ProgressStore progress;
    private SpeechController speech;
    private LinearLayout content;
    private ScrollView scroll;
    private View footer;
    private Module selectedModule;
    private Lesson selectedLesson;
    private int itemIndex;
    private int questionIndex;
    private int quizScore;
    private boolean questionAnswered;
    private Screen screen = Screen.MODULES;
    private Screen resourcesReturn = Screen.MODULES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressStore(this);
        speech = new SpeechController(this);
        try {
            course = new ContentRepository(this).loadProductionCourse();
            transportResources = new ResourceRepository(this).loadTransport();
            setContentView(buildShell());
            showModules();
        } catch (ContentContractException exception) {
            showFatalError(exception.getMessage());
        }
    }

    private View buildShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(color(R.color.background));
        root.setPadding(dp(16), dp(16), dp(16), dp(12));

        TextView appTitle = text(getString(R.string.app_name), 22, R.color.primary_dark, true);
        appTitle.setGravity(Gravity.CENTER);
        root.addView(appTitle, matchWrap());

        TextView version = text(
                getString(R.string.app_version_line, BuildConfig.VERSION_NAME, BuildConfig.RELEASE_DATE),
                11, R.color.muted, false);
        version.setGravity(Gravity.CENTER);
        version.setPadding(0, dp(2), 0, dp(4));
        root.addView(version, matchWrap());

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(scroll, scrollParams);

        footer = buildFooter();
        root.addView(footer, matchWrap());
        return root;
    }

    private View buildFooter() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.VERTICAL);
        bar.setGravity(Gravity.CENTER_HORIZONTAL);
        bar.setPadding(0, dp(10), 0, dp(2));

        TextView credit = text(getString(R.string.footer_credit), 11, R.color.muted, false);
        credit.setGravity(Gravity.CENTER);
        bar.addView(credit, matchWrap());

        TextView link = text(getString(R.string.footer_link_label), 12, R.color.primary_dark, false);
        link.setGravity(Gravity.CENTER);
        link.setPadding(0, dp(2), 0, 0);
        link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        link.setClickable(true);
        link.setFocusable(true);
        link.setOnClickListener(view -> openExternalUrl(getString(R.string.footer_link_url)));
        bar.addView(link, matchWrap());
        return bar;
    }

    private void openExternalUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.link_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void showModules() {
        screen = Screen.MODULES;
        selectedModule = null;
        selectedLesson = null;
        footer.setVisibility(View.VISIBLE);
        clear();
        heading(getString(R.string.modules_title));
        centered(getString(R.string.modules_intro));

        for (Module module : orderedModules()) {
            String progressText = moduleProgress(module);
            Button button = primaryButton(module.title.support + "\n" + module.title.target + progressText);
            button.setOnClickListener(view -> showLessons(module));
            content.addView(button, matchWrapWithTop());
        }
        addPracticalLinksButton(Screen.MODULES);
    }

    private List<Module> orderedModules() {
        List<Module> ordered = new ArrayList<>(course.modules);
        ordered.sort(Comparator.comparingInt(this::moduleHomeOrder));
        return ordered;
    }

    /** Level modules by number, then children, then other types, grammar last. */
    private int moduleHomeOrder(Module module) {
        if ("level".equals(module.type)) {
            return module.level == null ? 50 : module.level;
        }
        if ("children".equals(module.type)) return 80;
        if ("grammar".equals(module.type)) return 100;
        return 90;
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
        panel(lesson.situation.support + "\n" + lesson.situation.target, R.color.panel);

        int completed = progress.countCompleted(course.id, selectedModule.id, lesson.id);
        status(completed + "/" + lesson.items.size() + " éléments terminés");

        Button start = primaryButton("Commencer la leçon");
        start.setOnClickListener(view -> showItem(0));
        content.addView(start, matchWrapWithTop());

        if (lesson.quiz != null) {
            Button quiz = accentButton(getString(R.string.open_quiz) + quizSavedSuffix(lesson.quiz));
            quiz.setOnClickListener(view -> startQuiz());
            content.addView(quiz, matchWrapWithTop());
        }
        if (isTransportLesson(lesson)) addPracticalLinksButton(Screen.LESSON);
    }

    private boolean isTransportLesson(Lesson lesson) {
        if (lesson.tags.contains("transport") || lesson.tags.contains("station")) return true;
        return lesson.id.contains("public-transport") || lesson.id.contains("airport-station")
                || lesson.id.contains("commute");
    }

    private void addPracticalLinksButton(Screen returnTo) {
        if (transportResources == null) return;
        Button button = secondaryButton(getString(R.string.practical_links_transport)
                + "\n" + transportResources.title.support + " / " + transportResources.title.target);
        button.setOnClickListener(view -> showPracticalLinks(returnTo));
        content.addView(button, matchWrapWithTop());
    }

    private void showPracticalLinks(Screen returnTo) {
        if (transportResources == null) return;
        resourcesReturn = returnTo;
        screen = Screen.RESOURCES;
        clear();
        if (returnTo == Screen.LESSON && selectedLesson != null) {
            backButton(() -> showLessonOverview(selectedLesson));
        } else {
            backButton(this::showModules);
        }
        heading(transportResources.title.support);
        targetLabel(transportResources.title.target);
        panel(transportResources.intro.support + "\n" + transportResources.intro.target, R.color.panel);
        status(getString(R.string.open_official_site));
        for (ExternalResource resource : transportResources.items) {
            Button entry = primaryButton(resource.name + "\n" + resource.title.support + " / " + resource.title.target);
            entry.setOnClickListener(view -> openExternalUrl(resource.url));
            content.addView(entry, matchWrapWithTop());
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
        status("Élément " + (itemIndex + 1) + " sur " + selectedLesson.items.size());
        if (!item.speaker.isEmpty()) status("Personnage " + item.speaker);

        content.addView(dialogueBlock(item), matchWrapWithTop());
        content.addView(itemNavBar(), matchWrapWithTop());
        content.addView(ttsRow(item), matchWrapWithTop());

        boolean complete = progress.isItemComplete(progressId);
        Button completion = accentButton(complete ? getString(R.string.completed) : getString(R.string.mark_complete));
        completion.setEnabled(!complete);
        completion.setOnClickListener(view -> {
            progress.markItemComplete(progressId);
            showItem(itemIndex);
        });
        content.addView(completion, matchWrapWithTop());

        addNotes(item.notes);
    }

    private View dialogueBlock(Item item) {
        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);

        TextView target = text(item.text.target, 22, R.color.text, true);
        target.setGravity(Gravity.CENTER);
        target.setPadding(dp(12), dp(10), dp(12), dp(10));
        target.setBackground(panelBackground(R.color.panel));
        block.addView(target, matchWrap());

        TextView support = text(item.text.support, 15, R.color.muted, false);
        support.setGravity(Gravity.CENTER);
        support.setPadding(dp(8), dp(4), dp(8), dp(2));
        LinearLayout.LayoutParams supportParams = matchWrap();
        supportParams.topMargin = dp(4);
        block.addView(support, supportParams);
        return block;
    }

    private RoundNavBar itemNavBar() {
        RoundNavBar nav = new RoundNavBar(
                this,
                color(R.color.primary),
                color(R.color.panel),
                color(R.color.primary_dark),
                color(R.color.muted));
        boolean hasNextItem = itemIndex + 1 < selectedLesson.items.size();
        String nextDescription = hasNextItem ? getString(R.string.next) : getString(R.string.open_quiz);
        if (!hasNextItem && selectedLesson.quiz == null) nextDescription = getString(R.string.back);
        nav.bind(
                itemIndex > 0,
                true,
                getString(R.string.previous),
                nextDescription,
                new RoundNavBar.Actions() {
                    @Override
                    public void onPrevious() {
                        showItem(itemIndex - 1);
                    }

                    @Override
                    public void onNext() {
                        if (itemIndex + 1 < selectedLesson.items.size()) showItem(itemIndex + 1);
                        else if (selectedLesson.quiz != null) startQuiz();
                        else showLessonOverview(selectedLesson);
                    }
                });
        return nav;
    }

    private View ttsRow(Item item) {
        LinearLayout row = row();
        Button french = compactTtsButton(getString(R.string.listen_french));
        french.setOnClickListener(view ->
                speech.speak(item.text.support, course.speech.supportLocale, this));
        Button danish = compactTtsButton(getString(R.string.listen_danish));
        danish.setOnClickListener(view ->
                speech.speak(item.text.target, course.speech.targetLocale, this));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams danishParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        danishParams.leftMargin = dp(8);
        row.addView(french, params);
        row.addView(danish, danishParams);
        return row;
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
        if (QuizIntegrity.questionsForLesson(selectedLesson).isEmpty()) {
            showFatalError(selectedLesson.id + ": quiz has no questions");
            return;
        }
        questionIndex = 0;
        quizScore = 0;
        questionAnswered = false;
        showQuestion();
    }

    private void showQuestion() {
        Quiz quiz = selectedLesson.quiz;
        List<Question> questions = QuizIntegrity.questionsForLesson(selectedLesson);
        if (questionIndex >= questions.size()) {
            showQuizResult();
            return;
        }
        screen = Screen.QUIZ;
        questionAnswered = false;
        Question question = questions.get(questionIndex);
        clear();
        backButton(() -> showLessonOverview(selectedLesson));
        heading(quiz.title.support);
        status("Question " + (questionIndex + 1) + " sur " + questions.size());
        panel(question.prompt.support + "\n" + question.prompt.target, R.color.panel);

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
        feedback(answer.correct ? getString(R.string.correct) : getString(R.string.incorrect), answer.correct);
        panel(question.explanation.support + "\n" + question.explanation.target,
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
        status(getString(R.string.quiz_saved));
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
            case RESOURCES:
                if (resourcesReturn == Screen.LESSON && selectedLesson != null) {
                    showLessonOverview(selectedLesson);
                } else {
                    showModules();
                }
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
        if (footer != null && screen != Screen.MODULES) footer.setVisibility(View.GONE);
        resetScrollToTop();
    }

    /** Lesson lists are long; without this, a short quiz can open below the fold and look empty. */
    private void resetScrollToTop() {
        if (scroll == null) return;
        scroll.scrollTo(0, 0);
        scroll.post(() -> {
            if (scroll != null) scroll.scrollTo(0, 0);
        });
    }

    private void heading(String value) {
        TextView heading = text(value, 20, R.color.primary_dark, true);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, dp(6), 0, dp(4));
        content.addView(heading, matchWrap());
    }

    private void targetLabel(String value) {
        TextView target = text(value, 16, R.color.text, true);
        target.setGravity(Gravity.CENTER);
        content.addView(target, matchWrap());
    }

    private void centered(String value) {
        TextView view = text(value, 14, R.color.muted, false);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, dp(2), 0, dp(4));
        content.addView(view, matchWrap());
    }

    private void status(String value) {
        TextView view = text(value, 12, R.color.muted, false);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, dp(1), 0, dp(2));
        content.addView(view, matchWrap());
    }

    private void feedback(String value, boolean positive) {
        TextView view = text(value, 13, positive ? R.color.primary_dark : R.color.accent, true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(0, dp(4), 0, dp(4));
        content.addView(view, matchWrap());
    }

    private void panel(String value, int colorResource) {
        TextView panel = text(value, 14, R.color.text, false);
        panel.setPadding(dp(12), dp(8), dp(12), dp(8));
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
        view.setLineSpacing(0, 1.06f);
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
        button.setTextSize(14);
        button.setTextColor(color(textColor));
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(44));
        button.setMinimumHeight(dp(44));
        button.setPadding(dp(10), dp(6), dp(10), dp(6));
        button.setStateListAnimator(null);
        button.setBackground(panelBackground(backgroundColor));
        return button;
    }

    private Button compactTtsButton(String label) {
        Button button = button(label, R.color.primary, R.color.primary_dark);
        button.setTextSize(13);
        button.setMinHeight(dp(40));
        button.setMinimumHeight(dp(40));
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setSingleLine(true);
        button.setPadding(dp(12), dp(6), dp(12), dp(6));
        return button;
    }

    private GradientDrawable panelBackground(int colorResource) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(color(colorResource));
        background.setCornerRadius(dp(12));
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(SPACE);
        return params;
    }

    private enum Screen {
        MODULES, LESSONS, LESSON, ITEM, QUIZ, QUIZ_RESULT, RESOURCES
    }
}
