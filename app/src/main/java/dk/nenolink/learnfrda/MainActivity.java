package dk.nenolink.learnfrda;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import dk.nenolink.nenoling.config.EngineConfig;
import dk.nenolink.nenoling.content.ContentContractException;
import dk.nenolink.nenoling.content.ContentModels.Course;
import dk.nenolink.nenoling.content.ContentRepository;
import dk.nenolink.nenoling.content.ResourceModels.ResourceCollection;
import dk.nenolink.nenoling.content.ResourceRepository;
import dk.nenolink.nenoling.progress.ProgressStore;
import dk.nenolink.nenoling.shell.LessonNoteLabels;
import dk.nenolink.nenoling.shell.ResourcePlacement;
import dk.nenolink.nenoling.shell.ShellConfig;
import dk.nenolink.nenoling.shell.ShellCoordinator;
import dk.nenolink.nenoling.shell.ShellHost;
import dk.nenolink.nenoling.shell.ShellTheme;
import dk.nenolink.nenoling.speech.SpeechController;
import dk.nenolink.nenoling.ui.ExternalResourceLauncher;

/** FR→DA product host. Course behavior/navigation live in the reusable Nenoling engine/shell. */
public final class MainActivity extends Activity implements ShellHost, SpeechController.Listener {
    private ShellCoordinator coordinator;
    private SpeechController speech;
    private LinearLayout content;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildProductShell());
        EngineConfig engineConfig = new EngineConfig("content/fr-da/course.json", "content/fr-da/resources.json", 3);
        speech = new SpeechController(this);
        try {
            Course course = new ContentRepository(this, engineConfig).loadProductionCourse();
            ResourceCollection resourceCollection = new ResourceRepository(this, engineConfig).loadConfigured();
            ShellTheme theme = new ShellTheme(0xFFFFF9F2, 0xFFFFFFFF, 0xFFA9C7E8, 0xFF244766,
                    0xFFB85C63, 0xFF252A30, 0xFF5E6670, 0xFFFFFFFF);
            coordinator = new ShellCoordinator(this, this, shellConfig(), theme, noteLabels(),
                    ResourcePlacement.modulesOnly(), course,
                    resourceCollection == null ? Collections.emptyList() : Collections.singletonList(resourceCollection),
                    new ProgressStore(this), speech, this);
            coordinator.start();
        } catch (ContentContractException exception) {
            Toast.makeText(this, getString(R.string.content_error) + "\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View buildProductShell() {
        int pad = dp(16);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFFFF9F2);
        root.setPadding(pad, pad, pad, dp(12));

        TextView title = label(getString(R.string.app_name), 22, 0xFF244766, true);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView version = label(getString(R.string.app_version_line, BuildConfig.VERSION_NAME, BuildConfig.RELEASE_DATE), 11, 0xFF5E6670, false);
        version.setGravity(Gravity.CENTER);
        version.setPadding(0, dp(2), 0, dp(4));
        root.addView(version, matchWrap());

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setGravity(Gravity.CENTER_HORIZONTAL);
        footer.setPadding(0, dp(10), 0, dp(2));
        TextView credit = label(getString(R.string.footer_credit), 11, 0xFF5E6670, false);
        credit.setGravity(Gravity.CENTER);
        footer.addView(credit, matchWrap());
        TextView link = label(getString(R.string.footer_link_label), 12, 0xFF244766, false);
        link.setGravity(Gravity.CENTER);
        link.setPadding(0, dp(2), 0, 0);
        link.setPaintFlags(link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        link.setOnClickListener(v -> {
            if (!ExternalResourceLauncher.open(this, getString(R.string.footer_link_url)))
                Toast.makeText(this, R.string.link_unavailable, Toast.LENGTH_SHORT).show();
        });
        footer.addView(link, matchWrap());
        root.addView(footer, matchWrap());
        return root;
    }

    private ShellConfig shellConfig() {
        ShellConfig.UiText ui = new ShellConfig.UiText(
                getString(R.string.modules_title), getString(R.string.modules_intro), "Commencer la leçon",
                getString(R.string.open_quiz), getString(R.string.completed), getString(R.string.mark_complete),
                getString(R.string.previous), getString(R.string.next), getString(R.string.back), "Élément", "sur",
                "Question", getString(R.string.quiz_saved), getString(R.string.listen_french), getString(R.string.listen_danish),
                getString(R.string.practical_links_transport), getString(R.string.open_official_site), getString(R.string.link_unavailable));
        return new ShellConfig(getString(R.string.app_name), getString(R.string.app_version_line),
                getString(R.string.footer_credit), getString(R.string.footer_link_label), getString(R.string.footer_link_url), ui);
    }

    private LessonNoteLabels noteLabels() {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("grammar", "Grammaire"); labels.put("cultural", "Contexte culturel");
        labels.put("digital", "Contexte numérique"); labels.put("pronunciation", "Prononciation");
        return new LessonNoteLabels(labels);
    }

    @Override public void show(View view) {
        content.removeAllViews();
        content.addView(view, matchWrap());
        scroll.scrollTo(0, 0);
        scroll.post(() -> scroll.scrollTo(0, 0));
    }

    @Override public void onBackPressed() { if (coordinator == null || !coordinator.back()) super.onBackPressed(); }
    @Override public void onUnavailable(String localeTag) { Toast.makeText(this, getString(R.string.tts_unavailable, localeTag), Toast.LENGTH_SHORT).show(); }
    @Override public void onFailure() { Toast.makeText(this, R.string.tts_failed, Toast.LENGTH_SHORT).show(); }
    @Override protected void onDestroy() { if (speech != null) speech.shutdown(); super.onDestroy(); }

    private TextView label(String value, int sp, int color, boolean bold) {
        TextView view = new TextView(this); view.setText(value); view.setTextSize(sp); view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return view;
    }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
