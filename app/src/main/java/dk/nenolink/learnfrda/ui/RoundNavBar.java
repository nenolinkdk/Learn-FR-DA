package dk.nenolink.learnfrda.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Compact round previous/next controls for lesson-item navigation.
 * Reusable across Nenoling apps: callers supply labels, enabled state and actions.
 */
public final class RoundNavBar extends LinearLayout {
    public static final String PREVIOUS_SYMBOL = "←";
    public static final String NEXT_SYMBOL = "→";

    public interface Actions {
        void onPrevious();
        void onNext();
    }

    private final Button previousButton;
    private final Button nextButton;
    private final int enabledBackground;
    private final int disabledBackground;
    private final int enabledText;
    private final int disabledText;

    public RoundNavBar(Context context, int enabledBackground, int disabledBackground,
                       int enabledText, int disabledText) {
        super(context);
        this.enabledBackground = enabledBackground;
        this.disabledBackground = disabledBackground;
        this.enabledText = enabledText;
        this.disabledText = disabledText;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        int gap = dp(16);
        previousButton = roundButton(PREVIOUS_SYMBOL);
        nextButton = roundButton(NEXT_SYMBOL);
        addView(previousButton, buttonParams(0));
        addView(nextButton, buttonParams(gap));
    }

    public void bind(boolean previousEnabled, boolean nextEnabled,
                     CharSequence previousDescription, CharSequence nextDescription,
                     Actions actions) {
        applyState(previousButton, previousEnabled, previousDescription, view -> actions.onPrevious());
        applyState(nextButton, nextEnabled, nextDescription, view -> actions.onNext());
    }

    private void applyState(Button button, boolean enabled, CharSequence description, View.OnClickListener listener) {
        button.setEnabled(enabled);
        button.setTextColor(enabled ? enabledText : disabledText);
        button.setBackground(circle(enabled ? enabledBackground : disabledBackground));
        button.setContentDescription(description);
        button.setOnClickListener(enabled ? listener : null);
    }

    private Button roundButton(String symbol) {
        int size = dp(48);
        Button button = new Button(getContext());
        button.setText(symbol);
        button.setTextSize(22);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(false);
        button.setMinWidth(size);
        button.setMinimumWidth(size);
        button.setMinHeight(size);
        button.setMinimumHeight(size);
        button.setPadding(0, 0, 0, 0);
        button.setStateListAnimator(null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        button.setLayoutParams(params);
        return button;
    }

    private LinearLayout.LayoutParams buttonParams(int leftMargin) {
        int size = dp(48);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.leftMargin = leftMargin;
        return params;
    }

    private GradientDrawable circle(int color) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(color);
        return background;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
