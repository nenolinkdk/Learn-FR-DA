package dk.nenolink.learnfrda.content;

import java.util.Collections;
import java.util.List;

import dk.nenolink.learnfrda.content.ContentModels.TextPair;

public final class ResourceModels {
    private ResourceModels() {}

    public static final class ResourceCollection {
        public final String id;
        public final String category;
        public final TextPair title;
        public final TextPair intro;
        public final List<ExternalResource> items;

        public ResourceCollection(String id, String category, TextPair title, TextPair intro,
                                  List<ExternalResource> items) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.intro = intro;
            this.items = Collections.unmodifiableList(items);
        }
    }

    public static final class ExternalResource {
        public final String id;
        public final int order;
        public final String name;
        public final TextPair title;
        public final String url;
        public final List<String> tags;

        public ExternalResource(String id, int order, String name, TextPair title, String url, List<String> tags) {
            this.id = id;
            this.order = order;
            this.name = name;
            this.title = title;
            this.url = url;
            this.tags = Collections.unmodifiableList(tags);
        }
    }
}
