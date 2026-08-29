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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import dk.nenolink.learnfrda.content.ContentModels.TextPair;
import dk.nenolink.learnfrda.content.ResourceModels.ExternalResource;
import dk.nenolink.learnfrda.content.ResourceModels.ResourceCollection;

public final class ResourceRepository {
    public static final String TRANSPORT_ASSET = "content/fr-da/resources.json";

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9]+(?:[.-][a-z0-9]+)*$");
    private static final Pattern TAG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Set<String> ALLOWED_URLS = setOf(
            "https://rejseplanen.dk",
            "https://www.dsb.dk",
            "https://dinoffentligetransport.dk",
            "https://m.dk",
            "https://publictransport.dk/app",
            "https://www.visitcopenhagen.com"
    );

    private final Context context;
    private final Set<String> ids = new HashSet<>();
    private String assetName = TRANSPORT_ASSET;

    public ResourceRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public ResourceCollection loadTransport() throws ContentContractException {
        return load(TRANSPORT_ASSET);
    }

    public ResourceCollection load(String name) throws ContentContractException {
        assetName = name;
        ids.clear();
        try {
            JSONObject root = new JSONObject(readAsset(assetName));
            requireOnly(root, "$", "schemaVersion", "contentVersion", "collection");
            int schemaVersion = requiredPositiveInt(root, "schemaVersion", "$");
            if (schemaVersion != 1) fail("$.schemaVersion", "only version 1 is supported");
            requiredString(root, "contentVersion", "$");
            JSONObject collection = requiredObject(root, "collection", "$");
            requireOnly(collection, "$.collection", "id", "category", "title", "intro", "items");
            String id = requiredId(collection, "id", "$.collection");
            String category = requiredString(collection, "category", "$.collection");
            TextPair title = parsePair(requiredObject(collection, "title", "$.collection"), "$.collection.title");
            TextPair intro = parsePair(requiredObject(collection, "intro", "$.collection"), "$.collection.intro");
            List<ExternalResource> items = parseItems(requiredArray(collection, "items", "$.collection"));
            if (items.isEmpty()) fail("$.collection.items", "must contain at least one resource");
            Collections.sort(items, (a, b) -> Integer.compare(a.order, b.order));
            return new ResourceCollection(id, category, title, intro, items);
        } catch (IOException | JSONException exception) {
            throw new ContentContractException(assetName + ": invalid JSON: " + exception.getMessage(), exception);
        }
    }

    private List<ExternalResource> parseItems(JSONArray array) throws JSONException, ContentContractException {
        List<ExternalResource> items = new ArrayList<>();
        Set<Integer> orders = new HashSet<>();
        for (int index = 0; index < array.length(); index++) {
            String path = "$.collection.items[" + index + "]";
            JSONObject json = array.getJSONObject(index);
            requireOnly(json, path, "id", "order", "name", "title", "url", "tags");
            String id = requiredId(json, "id", path);
            int order = requiredPositiveInt(json, "order", path);
            if (!orders.add(order)) fail(path + ".order", "duplicate sibling order");
            String name = requiredString(json, "name", path);
            TextPair title = parsePair(requiredObject(json, "title", path), path + ".title");
            String url = requiredString(json, "url", path);
            if (!url.startsWith("https://") || !ALLOWED_URLS.contains(url)) {
                fail(path + ".url", "unapproved URL");
            }
            List<String> tags = parseTags(requiredArray(json, "tags", path), path + ".tags");
            items.add(new ExternalResource(id, order, name, title, url, tags));
        }
        return items;
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

    private TextPair parsePair(JSONObject json, String path) throws JSONException, ContentContractException {
        requireOnly(json, path, "support", "target");
        return new TextPair(requiredString(json, "support", path), requiredString(json, "target", path));
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

    private int requiredPositiveInt(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof Number)) fail(path + "." + key, "required integer");
        int value = json.getInt(key);
        if (value <= 0) fail(path + "." + key, "must be positive");
        return value;
    }

    private JSONObject requiredObject(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof JSONObject)) fail(path + "." + key, "required object");
        return json.getJSONObject(key);
    }

    private JSONArray requiredArray(JSONObject json, String key, String path) throws JSONException, ContentContractException {
        if (!json.has(key) || json.isNull(key) || !(json.get(key) instanceof JSONArray)) fail(path + "." + key, "required array");
        return json.getJSONArray(key);
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
