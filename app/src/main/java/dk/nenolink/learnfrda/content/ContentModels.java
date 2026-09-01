package dk.nenolink.learnfrda.content;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ContentModels {
    private ContentModels() {}

    private static <T> List<T> immutable(List<T> values) {
        return Collections.unmodifiableList(values);
    }

    public static final class TextPair {
        public final String support;
        public final String target;

        public TextPair(String support, String target) {
            this.support = support;
            this.target = target;
        }

        public String forRole(String role) {
            if ("support".equals(role)) return support;
            if ("target".equals(role)) return target;
            throw new IllegalArgumentException("role must be support or target: " + role);
        }
    }

    public static final class Language {
        public final String language;
        public final String locale;

        public Language(String language, String locale) {
            this.language = language;
            this.locale = locale;
        }
    }

    public static final class SpeechDefaults {
        public final String primaryRole;
        public final String targetLocale;
        public final String supportLocale;

        public SpeechDefaults(String primaryRole, String targetLocale, String supportLocale) {
            this.primaryRole = primaryRole;
            this.targetLocale = targetLocale;
            this.supportLocale = supportLocale;
        }
    }

    public static final class Course {
        public final int schemaVersion;
        public final String contentVersion;
        public final String id;
        public final String courseLocale;
        public final Language supportLanguage;
        public final Language targetLanguage;
        public final SpeechDefaults speech;
        public final TextPair title;
        public final List<Module> modules;

        public Course(int schemaVersion, String contentVersion, String id, String courseLocale,
                      Language supportLanguage, Language targetLanguage, SpeechDefaults speech,
                      TextPair title, List<Module> modules) {
            this.schemaVersion = schemaVersion;
            this.contentVersion = contentVersion;
            this.id = id;
            this.courseLocale = courseLocale;
            this.supportLanguage = supportLanguage;
            this.targetLanguage = targetLanguage;
            this.speech = speech;
            this.title = title;
            this.modules = immutable(modules);
        }
    }

    public static final class Module {
        public final String id;
        public final String type;
        public final Integer level;
        public final String audience;
        public final TextPair title;
        public final List<String> tags;
        public final List<Lesson> lessons;

        public Module(String id, String type, Integer level, String audience, TextPair title,
                      List<String> tags, List<Lesson> lessons) {
            this.id = id;
            this.type = type;
            this.level = level;
            this.audience = audience;
            this.title = title;
            this.tags = immutable(tags);
            this.lessons = immutable(lessons);
        }

        public boolean isChildren() {
            return "children".equals(type) && "children".equals(audience);
        }
    }

    public static final class Lesson {
        public final String id;
        public final String moduleId;
        public final int order;
        public final TextPair title;
        public final TextPair situation;
        public final List<String> tags;
        public final List<Item> items;
        public final Quiz quiz;

        public Lesson(String id, String moduleId, int order, TextPair title, TextPair situation,
                      List<String> tags, List<Item> items, Quiz quiz) {
            this.id = id;
            this.moduleId = moduleId;
            this.order = order;
            this.title = title;
            this.situation = situation;
            this.tags = immutable(tags);
            this.items = immutable(items);
            this.quiz = quiz;
        }
    }

    public static final class SpeechSpec {
        public final String role;
        public final String locale;
        public final boolean enabled;

        public SpeechSpec(String role, String locale, boolean enabled) {
            this.role = role;
            this.locale = locale;
            this.enabled = enabled;
        }
    }

    public static final class Note {
        public final String support;
        public final String targetDetail;

        public Note(String support, String targetDetail) {
            this.support = support;
            this.targetDetail = targetDetail;
        }
    }

    public static final class Item {
        public final String id;
        public final int order;
        public final String type;
        public final String speaker;
        public final TextPair text;
        public final SpeechSpec speech;
        public final Map<String, Note> notes;
        public final List<String> tags;

        public Item(String id, int order, String type, String speaker, TextPair text,
                    SpeechSpec speech, Map<String, Note> notes, List<String> tags) {
            this.id = id;
            this.order = order;
            this.type = type;
            this.speaker = speaker;
            this.text = text;
            this.speech = speech;
            this.notes = Collections.unmodifiableMap(notes);
            this.tags = immutable(tags);
        }
    }

    public static final class Quiz {
        public final String id;
        public final TextPair title;
        public final List<Question> questions;

        public Quiz(String id, TextPair title, List<Question> questions) {
            this.id = id;
            this.title = title;
            this.questions = immutable(questions);
        }
    }

    public static final class Question {
        public final String id;
        public final int order;
        public final String type;
        public final String answerDisplayRole;
        public final TextPair prompt;
        public final List<Answer> answers;
        public final TextPair explanation;
        public final List<String> tags;

        public Question(String id, int order, String type, String answerDisplayRole, TextPair prompt,
                        List<Answer> answers, TextPair explanation, List<String> tags) {
            this.id = id;
            this.order = order;
            this.type = type;
            this.answerDisplayRole = answerDisplayRole;
            this.prompt = prompt;
            this.answers = immutable(answers);
            this.explanation = explanation;
            this.tags = immutable(tags);
        }

        public String displayedAnswerText(Answer answer) {
            return answer.text.forRole(answerDisplayRole);
        }
    }

    public static final class Answer {
        public final String id;
        public final TextPair text;
        public final boolean correct;

        public Answer(String id, TextPair text, boolean correct) {
            this.id = id;
            this.text = text;
            this.correct = correct;
        }
    }
}
