package dk.nenolink.learnfrda.content;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import dk.nenolink.learnfrda.content.ContentModels.Answer;
import dk.nenolink.learnfrda.content.ContentModels.Course;
import dk.nenolink.learnfrda.content.ContentModels.Item;
import dk.nenolink.learnfrda.content.ContentModels.Language;
import dk.nenolink.learnfrda.content.ContentModels.Lesson;
import dk.nenolink.learnfrda.content.ContentModels.Module;
import dk.nenolink.learnfrda.content.ContentModels.Note;
import dk.nenolink.learnfrda.content.ContentModels.Question;
import dk.nenolink.learnfrda.content.ContentModels.Quiz;
import dk.nenolink.learnfrda.content.ContentModels.SpeechDefaults;
import dk.nenolink.learnfrda.content.ContentModels.SpeechSpec;
import dk.nenolink.learnfrda.content.ContentModels.TextPair;

public final class ContentRepository {
    public static final String PRODUCTION_ASSET = "content/fr-da/course.json";
    private String assetName = PRODUCTION_ASSET;

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:[.-][a-z0-9]+)*$");
    private static final Pattern TAG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Set<String> MODULE_TYPES = setOf("level", "children", "grammar", "quiz");
    private static final Set<String> ITEM_TYPES = setOf("phrase", "dialogue-turn", "grammar-example", "digital-scenario");
    private static final Set<String> NOTE_TYPES = setOf("grammar", "cultural", "digital", "pronunciation");

    private final Context context;
    private final Set<String> ids = new HashSet<>();

    public ContentRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public Course loadProductionCourse() throws ContentContractException {
        return loadCourse(PRODUCTION_ASSET);
    }

    public Course loadCourse(String name) throws ContentContractException {
        assetName = name;
        ids.clear();
        try {
            JSONObject root = new JSONObject(readAsset(assetName));
            requireOnly(root, "$", "schemaVersion", "contentVersion", "course");
            int schemaVersion = requiredPositiveInt(root, "schemaVersion", "$");
            if (schemaVersion != 1) {
                fail("$.schemaVersion", "only version 1 is supported");
            }
            String contentVersion = requiredString(root, "contentVersion", "$");
            JSONObject courseJson = requiredObject(root, "course", "$");
            requireOnly(courseJson, "$.course", "id", "courseLocale", "languages", "tts", "title", "modules");

            String courseId = requiredId(courseJson, "id", "$.course");
            String courseLocale = requiredString(courseJson, "courseLocale", "$.course");
            JSONObject languages = requiredObject(courseJson, "languages", "$.course");
            requireOnly(languages, "$.course.languages", "support", "target");
            Language support = parseLanguage(requiredObject(languages, "support", "$.course.languages"), "$.course.languages.support");
            Language target = parseLanguage(requiredObject(languages, "target", "$.course.languages"), "$.course.languages.target");
            if (!courseLocale.equals(support.locale)) {
                fail("$.course.courseLocale", "must equal the support locale");
            }
            SpeechDefaults defaults = parseSpeechDefaults(requiredObject(courseJson, "tts", "$.course"), support, target);
            TextPair title = parseTextPair(requiredObject(courseJson, "title", "$.course"), "$.course.title");
            List<Module> modules = parseModules(requiredArray(courseJson, "modules", "$.course"), support, target);
            if (modules.isEmpty()) {
                fail("$.course.modules", "must contain at least one module");
            }
            return new Course(schemaVersion, contentVersion, courseId, courseLocale, support, target, defaults, title, modules);
        } catch (IOException | JSONException exception) {
            throw new ContentContractException(assetName + ": invalid JSON: " + exception.getMessage(), exception);
        }
    }

    private Language parseLanguage(JSONObject json, String path) throws JSONException, ContentContractException {
        requireOnly(json, path, "language", "locale");
        return new Language(requiredString(json, "language", path), requiredString(json, "locale", path));
    }

    private SpeechDefaults parseSpeechDefaults(JSONObject json, Language support, Language target)
            throws JSONException, ContentContractException {
        String path = "$.course.tts";
        requireOnly(json, path, "primaryRole", "targetLocale", "supportLocale");
        String primaryRole = requiredString(json, "primaryRole", path);
        String targetLocale = requiredString(json, "targetLocale", path);
        String supportLocale = requiredString(json, "supportLocale", path);
        if (!"target".equals(primaryRole)) fail(path + ".primaryRole", "must be target");
        if (!target.locale.equals(targetLocale)) fail(path + ".targetLocale", "must match target language");
        if (!support.locale.equals(supportLocale)) fail(path + ".supportLocale", "must match support language");
        return new SpeechDefaults(primaryRole, targetLocale, supportLocale);
    }

    private List<Module> parseModules(JSONArray array, Language support, Language target)
            throws JSONException, ContentContractException {
        List<Module> modules = new ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            String path = "$.course.modules[" + index + "]";
            JSONObject json = requiredArrayObject(array, index, path);
            requireOnly(json, path, "id", "type", "level", "audience", "title", "tags", "lessons");
            String id = requiredId(json, "id", path);
            String type = requiredString(json, "type", path);
            if (!MODULE_TYPES.contains(type)) fail(path + ".type", "unsupported module type");
            String audience = requiredString(json, "audience", path);
            Integer level = json.has("level") ? requiredPositiveInt(json, "level", path) : null;
            if ("level".equals(type) && (level == null || (level != 1 && level != 2 && level != 3))) {
                fail(path + ".level", "level modules require 1, 2 or 3");
            }
            if ("children".equals(type) && (!"children".equals(audience) || level != null)) {
                fail(path, "children requires children audience and no level");
            }
            if (!"children".equals(type) && !"general".equals(audience)) {
                fail(path + ".audience", "non-children modules require general audience");
            }
            TextPair title = parseTextPair(requiredObject(json, "title", path), path + ".title");
            List<String> tags = parseTags(requiredArray(json, "tags", path), path + ".tags");
            List<Lesson> lessons = parseLessons(requiredArray(json, "lessons", path), id, support, target);
            modules.add(new Module(id, type, level, audience, title, tags, lessons));
        }
        return modules;
    }

    private List<Lesson> parseLessons(JSONArray array, String moduleId, Language support, Language target)
            throws JSONException, ContentContractException {
        List<Lesson> lessons = new ArrayList<>();
        Set<Integer> orders = new HashSet<>();
        for (int index = 0; index < array.length(); index++) {
            String path = "$.course.modules[" + moduleId + "].lessons[" + index + "]";
            JSONObject json = requiredArrayObject(array, index, path);
            requireOnly(json, path, "id", "moduleId", "order", "title", "situation", "tags", "items", "quiz");
            String id = requiredId(json, "id", path);
            String reference = requiredString(json, "moduleId", path);
            if (!moduleId.equals(reference)) fail(path + ".moduleId", "must match containing module");
            int order = requiredPositiveInt(json, "order", path);
            if (!orders.add(order)) fail(path + ".order", "duplicate sibling order");
            TextPair title = parseTextPair(requiredObject(json, "title", path), path + ".title");
            TextPair situation = parseTextPair(requiredObject(json, "situation", path), path + ".situation");
            List<String> tags = parseTags(requiredArray(json, "tags", path), path + ".tags");
            List<Item> items = parseItems(requiredArray(json, "items", path), path + ".items", support, target);
            if (items.isEmpty()) fail(path + ".items", "must contain at least one item");
            Quiz quiz = json.has("quiz") ? parseQuiz(requiredObject(json, "quiz", path), path + ".quiz") : null;
            lessons.add(new Lesson(id, reference, order, title, situation, tags, items, quiz));
        }
        java.util.Collections.sort(lessons, (a, b) -> Integer.compare(a.order, b.order));
        return lessons;
    }

    private List<Item> parseItems(JSONArray array, String path, Language support, Language target)
            throws JSONException, ContentContractException {
        List<Item> items = new ArrayList<>();
        Set<Integer> orders = new HashSet<>();
        for (int index = 0; index < array.length(); index++) {
            String itemPath = path + "[" + index + "]";
            JSONObject json = requiredArrayObject(array, index, itemPath);
            requireOnly(json, itemPath, "id", "order", "type", "speaker", "text", "tts", "notes", "tags");
            String id = requiredId(json, "id", itemPath);
            int order = requiredPositiveInt(json, "order", itemPath);
            if (!orders.add(order)) fail(itemPath + ".order", "duplicate sibling order");
            String type = requiredString(json, "type", itemPath);
            if (!ITEM_TYPES.contains(type)) fail(itemPath + ".type", "unsupported item type");
            String speaker = optionalString(json, "speaker", itemPath);
            TextPair text = parseTextPair(requiredObject(json, "text", itemPath), itemPath + ".text");
            SpeechSpec speech = parseSpeech(requiredObject(json, "tts", itemPath), itemPath + ".tts", support, target);
            Map<String, Note> notes = json.has("notes")
                    ? parseNotes(requiredObject(json, "notes", itemPath), itemPath + ".notes")
                    : new HashMap<>();
            List<String> tags = parseTags(requiredArray(json, "tags", itemPath), itemPath + ".tags");
            items.add(new Item(id, order, type, speaker, text, speech, notes, tags));
        }
        java.util.Collections.sort(items, (a, b) -> Integer.compare(a.order, b.order));
        return items;
    }

    private SpeechSpec parseSpeech(JSONObject json, String path, Language support, Language target)
            throws JSONException, ContentContractException {
        requireOnly(json, path, "role", "locale", "enabled");
        String role = requiredString(json, "role", path);
        String locale = requiredString(json, "locale", path);
        boolean enabled = requiredBoolean(json, "enabled", path);
        if (!"target".equals(role) && !"support".equals(role)) fail(path + ".role", "must be target or support");
        String expected = "target".equals(role) ? target.locale : support.locale;
        if (!expected.equals(locale)) fail(path + ".locale", "does not match role language");
        return new SpeechSpec(role, locale, enabled);
    }

    private Map<String, Note> parseNotes(JSONObject json, String path) throws JSONException, ContentContractException {
        requireOnly(json, path, NOTE_TYPES.toArray(new String[0]));
        Map<String, Note> notes = new HashMap<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String type = keys.next();
            JSONObject noteJson = requiredObject(json, type, path);
            String notePath = path + "." + type;
            if ("grammar".equals(type)) {
                requireOnly(noteJson, notePath, "support", "targetExample");
                notes.put(type, new Note(requiredString(noteJson, "support", notePath), optionalString(noteJson, "targetExample", notePath)));
            } else if ("pronunciation".equals(type)) {
                requireOnly(noteJson, notePath, "support", "targetText");
                notes.put(type, new Note(requiredString(noteJson, "support", notePath), optionalString(noteJson, "targetText", notePath)));
            } else {
                requireOnly(noteJson, notePath, "support");
                notes.put(type, new Note(requiredString(noteJson, "support", notePath), ""));
            }
        }
        return notes;
    }

    private Quiz parseQuiz(JSONObject json, String path) throws JSONException, ContentContractException {
        requireOnly(json, path, "id", "title", "questions");
        String id = requiredId(json, "id", path);
        TextPair title = parseTextPair(requiredObject(json, "title", path), path + ".title");
        JSONArray questionArray = requiredArray(json, "questions", path);
        if (questionArray.length() == 0) fail(path + ".questions", "must not be empty");
        List<Question> questions = new ArrayList<>();
        Set<Integer> orders = new HashSet<>();
        for (int index = 0; index < questionArray.length(); index++) {
            String questionPath = path + ".questions[" + index + "]";
            JSONObject question = requiredArrayObject(questionArray, index, questionPath);
            requireOnly(question, questionPath, "id", "order", "type", "prompt", "answers", "explanation", "tags");
            String questionId = requiredId(question, "id", questionPath);
            int order = requiredPositiveInt(question, "order", questionPath);
            if (!orders.add(order)) fail(questionPath + ".order", "duplicate sibling order");
            String type = requiredString(question, "type", questionPath);
            if (!"single-choice".equals(type)) fail(questionPath + ".type", "only single-choice is supported");
            TextPair prompt = parseTextPair(requiredObject(question, "prompt", questionPath), questionPath + ".prompt");
            List<Answer> answers = parseAnswers(requiredArray(question, "answers", questionPath), questionPath + ".answers");
            TextPair explanation = parseTextPair(requiredObject(question, "explanation", questionPath), questionPath + ".explanation");
            List<String> tags = parseTags(requiredArray(question, "tags", questionPath), questionPath + ".tags");
            questions.add(new Question(questionId, order, type, prompt, answers, explanation, tags));
        }
        java.util.Collections.sort(questions, (a, b) -> Integer.compare(a.order, b.order));
        return new Quiz(id, title, questions);
    }

    private List<Answer> parseAnswers(JSONArray array, String path) throws JSONException, ContentContractException {
        if (array.length() < 2) fail(path, "single-choice requires at least two answers");
        List<Answer> answers = new ArrayList<>();
        int correctCount = 0;
        for (int index = 0; index < array.length(); index++) {
            String answerPath = path + "[" + index + "]";
            JSONObject json = requiredArrayObject(array, index, answerPath);
            requireOnly(json, answerPath, "id", "text", "correct");
            String id = requiredId(json, "id", answerPath);
            TextPair text = parseTextPair(requiredObject(json, "text", answerPath), answerPath + ".text");
            boolean correct = requiredBoolean(json, "correct", answerPath);
            if (correct) correctCount++;
            answers.add(new Answer(id, text, correct));
        }
        if (correctCount != 1) fail(path, "single-choice requires exactly one correct answer");
        return answers;
    }

    private TextPair parseTextPair(JSONObject json, String path) throws JSONException, ContentContractException {
        requireOnly(json, path, "support", "target");
        return new TextPair(requiredString(json, "support", path), requiredString(json, "target", path));
    }

    private List<String> parseTags(JSONArray array, String path) throws JSONException, ContentContractException {
        List<String> tags = new ArrayList<>();
        Set<String> unique = new HashSet<>();
        for (int index = 0; index < array.length(); index++) {
            Object value = array.get(index);
            if (!(value instanceof String) || !TAG_PATTERN.matcher((String) value).matches()) {
                fail(path + "[" + index + "]", "invalid tag");
            }
            String tag = (String) value;
            if (!unique.add(tag)) fail(path + "[" + index + "]", "duplicate tag");
            tags.add(tag);
        }
        return tags;
    }

    private String requiredId(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        String value = requiredString(json, key, path);
        if (!ID_PATTERN.matcher(value).matches()) fail(path + "." + key, "invalid stable ID");
        if (!ids.add(value)) fail(path + "." + key, "duplicate stable ID");
        return value;
    }

    private String requiredString(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof String)) fail(path + "." + key, "required string");
        String value = json.getString(key);
        if (value.trim().isEmpty()) fail(path + "." + key, "must not be blank");
        return value;
    }

    private String optionalString(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key)) return "";
        return requiredString(json, key, path);
    }

    private int requiredPositiveInt(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof Number)) fail(path + "." + key, "required integer");
        double number = json.getDouble(key);
        if (number != Math.rint(number) || number > Integer.MAX_VALUE) fail(path + "." + key, "required integer");
        int value = json.getInt(key);
        if (value <= 0) fail(path + "." + key, "must be positive");
        return value;
    }

    private boolean requiredBoolean(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof Boolean)) fail(path + "." + key, "required boolean");
        return json.getBoolean(key);
    }

    private JSONObject requiredObject(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof JSONObject)) fail(path + "." + key, "required object");
        return json.getJSONObject(key);
    }

    private JSONArray requiredArray(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof JSONArray)) fail(path + "." + key, "required array");
        return json.getJSONArray(key);
    }

    private JSONObject requiredArrayObject(JSONArray array, int index, String path) throws JSONException, ContentContractException {
        Object value = array.get(index);
        if (!(value instanceof JSONObject)) fail(path, "required object");
        return (JSONObject) value;
    }

    private void requireOnly(JSONObject json, String path, String... allowed) throws ContentContractException {
        Set<String> allowedKeys = new HashSet<>(Arrays.asList(allowed));
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!allowedKeys.contains(key)) fail(path + "." + key, "unknown field");
        }
    }

    private String readAsset(String name) throws IOException {
        try (InputStream input = context.getAssets().open(name);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder()
                     .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                     .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT)))) {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) text.append(line).append('\n');
            return text.toString();
        }
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private void fail(String path, String reason) throws ContentContractException {
        throw new ContentContractException(assetName + " " + path + ": " + reason);
    }
}
