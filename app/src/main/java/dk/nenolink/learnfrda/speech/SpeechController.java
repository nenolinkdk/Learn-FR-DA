package dk.nenolink.learnfrda.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public final class SpeechController {
    public interface Listener {
        void onUnavailable(String localeTag);
        void onFailure();
    }

    private final Context context;
    private TextToSpeech engine;
    private Pending pending;
    private boolean initializing;

    public SpeechController(Context context) {
        this.context = context.getApplicationContext();
    }

    public void speak(String text, String localeTag, Listener listener) {
        if (text == null || text.trim().isEmpty()) {
            listener.onFailure();
            return;
        }
        pending = new Pending(text, localeTag, listener);
        if (engine == null) {
            initialize();
        } else {
            speakPending();
        }
    }

    private void initialize() {
        if (initializing) return;
        initializing = true;
        engine = new TextToSpeech(context, status -> {
            initializing = false;
            if (status != TextToSpeech.SUCCESS) {
                Pending request = pending;
                pending = null;
                if (request != null) request.listener.onFailure();
                shutdown();
                return;
            }
            speakPending();
        });
    }

    private void speakPending() {
        Pending request = pending;
        pending = null;
        if (request == null || engine == null) return;
        Locale locale = Locale.forLanguageTag(request.localeTag);
        int available = engine.isLanguageAvailable(locale);
        if (available == TextToSpeech.LANG_MISSING_DATA || available == TextToSpeech.LANG_NOT_SUPPORTED) {
            request.listener.onUnavailable(request.localeTag);
            return;
        }
        int selected = engine.setLanguage(locale);
        if (selected == TextToSpeech.LANG_MISSING_DATA
                || selected == TextToSpeech.LANG_NOT_SUPPORTED) {
            request.listener.onUnavailable(request.localeTag);
            return;
        }
        int result = engine.speak(request.text, TextToSpeech.QUEUE_FLUSH, null, "nenoling-" + System.nanoTime());
        if (result == TextToSpeech.ERROR) request.listener.onFailure();
    }

    public void shutdown() {
        if (engine != null) {
            engine.stop();
            engine.shutdown();
            engine = null;
        }
        initializing = false;
        pending = null;
    }

    private static final class Pending {
        final String text;
        final String localeTag;
        final Listener listener;

        Pending(String text, String localeTag, Listener listener) {
            this.text = text;
            this.localeTag = localeTag;
            this.listener = listener;
        }
    }
}
