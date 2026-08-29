# JSON Format Specification

## Status and scope

Proposed generic Nenoling content contract, version 1. It is concrete enough for parser and progress tests, but it does not authorize production of the final 20 lessons. Files in `linguistic/testdata/` are synthetic fixtures only.

The contract keeps LP2's useful hierarchy—module, lesson, ordered learning items, notes, and quiz—while removing Portuguese-specific names and title-based module detection.

## Language direction

- support/source and interface language: French (`fr`, `fr-FR`)
- target language: Danish (`da`, `da-DK`)
- course locale: `fr-FR`, the default locale for learner-facing metadata and instructions
- primary TTS: Danish (`da-DK`)
- optional support TTS: French (`fr-FR`)

“Source” and “support” describe the same role. The canonical key is `support`; `source`, `textPt`, `textDa`, `fr`, and `da` are not parser aliases.

## Core decisions

1. A course document contains metadata and modules.
2. Modules explicitly declare type and audience. Children is not inferred from a title or number.
3. Modules contain lessons; lessons contain items and an optional quiz.
4. Language-bearing records use `support` and `target` roles. Course metadata maps roles to languages/locales.
5. TTS role and locale are explicit.
6. Stable string IDs—not indexes or displayed text—are progress keys.
7. Optional notes are grouped under `notes` and use French support text.
8. Missing optional fields are omitted; canonical content does not use `null`.
9. Version 1 rejects unknown fields so spelling mistakes do not disappear silently.

## File layout

The first implementation may bundle one document at `content/fr-da/course.json`. A later manifest may split modules without changing the in-memory contract. Test fixtures stay outside production content:

```text
linguistic/testdata/course.synthetic.json
linguistic/testdata/README.md
linguistic/resources/transport.json
```

Practical reference links are **not** linguistic lesson items. They live in a separate schema-1 document (`collection` of official HTTPS URLs) so version-1 course JSON is unchanged. The bundled copy is `app/src/main/assets/content/fr-da/resources.json`. External URLs open in the device browser (`ACTION_VIEW`); the app requests no network permission and remains offline-first for lessons.

## Course object

```json
{
  "schemaVersion": 1,
  "contentVersion": "0.0.1-test",
  "course": {
    "id": "course.fr-da",
    "courseLocale": "fr-FR",
    "languages": {
      "support": { "language": "fr", "locale": "fr-FR" },
      "target": { "language": "da", "locale": "da-DK" }
    },
    "tts": {
      "primaryRole": "target",
      "targetLocale": "da-DK",
      "supportLocale": "fr-FR"
    },
    "title": { "support": "Danois pratique", "target": "Praktisk dansk" },
    "modules": []
  }
}
```

All shown top-level fields are required. `schemaVersion` is a positive integer; `contentVersion` is an editorial/package version independent of the app version. `course.id` is the progress namespace. Language codes are lower-case ISO 639-1 and locales are BCP 47 tags. Version 1 requires `courseLocale` to equal the support locale, and each TTS locale to match its role's language.

## Bilingual text

```json
{ "support": "Où est la gare ?", "target": "Hvor er stationen?" }
```

Both fields are required, non-blank UTF-8 strings. `support` is French and `target` is Danish. Intentionally monolingual data uses a specifically documented note field rather than an empty translation.

## Practical resources (separate document)

Not part of the course `modules` array. Version-1 course files must not contain URLs in dialogue `text` fields.

```json
{
  "schemaVersion": 1,
  "contentVersion": "1.0.0-transport-resources",
  "collection": {
    "id": "resources.transport",
    "category": "transport",
    "title": { "support": "Liens pratiques", "target": "Praktiske links" },
    "intro": { "support": "…", "target": "…" },
    "items": [
      {
        "id": "resource.transport.rejseplanen",
        "order": 1,
        "name": "Rejseplanen",
        "title": { "support": "Planifier un trajet", "target": "Planlæg en rejse" },
        "url": "https://rejseplanen.dk",
        "tags": ["journey-planning"]
      }
    ]
  }
}
```

URLs must be `https://` official destinations. They are opened with Android `ACTION_VIEW`. Prices and timetables are not stored.

## Module object

```json
{
  "id": "module.level-1",
  "type": "level",
  "level": 1,
  "audience": "general",
  "title": {
    "support": "Niveau 1 — Visiter le Danemark",
    "target": "Niveau 1 — Besøg i Danmark"
  },
  "tags": ["travel", "beginner"],
  "lessons": []
}
```

Required: `id`, `type`, `audience`, `title`, `tags`, and `lessons`.

Allowed types:

- `level`: requires `level` equal to `1` or `2`
- `children`: requires `audience: "children"` and omits `level`
- `grammar`: standalone grammar reference/practice
- `quiz`: standalone cross-lesson quiz

Other modules use `audience: "general"`. Reserved production IDs are `module.level-1`, `module.level-2`, `module.children`, `module.grammar`, and `module.quiz`. A small fixture need not contain every module.

## Lesson object

```json
{
  "id": "lesson.level-1.arrival-test",
  "moduleId": "module.level-1",
  "order": 1,
  "title": { "support": "Test à l’arrivée", "target": "Test ved ankomsten" },
  "situation": {
    "support": "Une scène fictive pour tester l’affichage.",
    "target": "En opdigtet scene til test af visningen."
  },
  "tags": ["synthetic", "arrival"],
  "items": [],
  "quiz": {}
}
```

Required: `id`, `moduleId`, positive `order`, `title`, `situation`, `tags`, and `items`. `quiz` is optional. `moduleId` must equal the containing module ID. Order is unique among sibling lessons and affects display only.

## Item object

```json
{
  "id": "item.level-1.arrival-test.greeting",
  "order": 1,
  "type": "dialogue-turn",
  "speaker": "A",
  "text": { "support": "Bonjour.", "target": "Goddag." },
  "tts": { "role": "target", "locale": "da-DK", "enabled": true },
  "notes": {
    "grammar": {
      "support": "Note grammaticale synthétique.",
      "targetExample": "Goddag."
    },
    "cultural": { "support": "Note culturelle synthétique." },
    "digital": { "support": "Note numérique synthétique." },
    "pronunciation": {
      "support": "Conseil de prononciation synthétique.",
      "targetText": "Goddag"
    }
  },
  "tags": ["greeting"]
}
```

Required: `id`, positive `order`, `type`, `text`, `tts`, and `tags`. Allowed types are `phrase`, `dialogue-turn`, `grammar-example`, and `digital-scenario`. Optional `speaker` is a neutral short label such as `A`, `B`, or `Narrator`.

The whole `notes` object is optional. Its only allowed children are:

- `grammar`: required French `support`, optional Danish `targetExample`
- `cultural`: required French `support`
- `digital`: required French `support`; safe explanatory text only
- `pronunciation`: required French `support`, optional Danish `targetText`

Notes are plain text, not HTML. Digital notes must never contain real MitID credentials, CPR numbers, payment details, or health data.

TTS `role` is `target` or `support`; its locale must match the declared role. Ordinary FR-DA items use target/`da-DK`. Support/`fr-FR` remains possible without changing the schema. `enabled: false` suppresses speech. The parser must not infer locale from a property name or the device locale.

## Quiz object

```json
{
  "id": "quiz.level-1.arrival-test",
  "title": { "support": "Mini-quiz synthétique", "target": "Syntetisk mini-quiz" },
  "questions": [
    {
      "id": "question.level-1.arrival-test.hello",
      "order": 1,
      "type": "single-choice",
      "prompt": {
        "support": "Choisissez la salutation danoise.",
        "target": "Vælg den danske hilsen."
      },
      "answers": [
        {
          "id": "answer.level-1.arrival-test.hello.correct",
          "text": { "support": "Bonjour", "target": "Goddag" },
          "correct": true
        }
      ],
      "explanation": {
        "support": "Explication synthétique.",
        "target": "Syntetisk forklaring."
      },
      "tags": ["greeting"]
    }
  ]
}
```

Required quiz fields: `id`, `title`, and non-empty `questions`. Required question fields: `id`, positive `order`, `type`, `prompt`, `answers`, `explanation`, and `tags`. Version 1 supports `single-choice`; each question has at least two answers and exactly one correct answer. Each answer requires stable `id`, bilingual `text`, and boolean `correct`. Apps may shuffle a copy without changing IDs.

## Stable IDs and progress

Every ID matches:

```text
^[a-z0-9]+(?:[.-][a-z0-9]+)*$
```

IDs are globally unique within the course, never translated, never reused for another entity, and not derived from mutable display text. The minimum progress identity is:

```text
course.id + module.id + lesson.id + entity.id
```

For item completion, `entity.id` is the item ID. For quiz progress it is the quiz or question ID, depending on the stored record. `contentVersion` may accompany progress for migration diagnostics but is not identity. Reordering arrays must not lose progress.

## Tags

Tags are unique within their array and match `^[a-z0-9]+(?:-[a-z0-9]+)*$`. They support filtering and QA but never determine module identity, language direction, locale, or progress.

## Validation rules

A version-1 validator rejects content when:

- required fields are absent, blank, `null`, unknown, or of the wrong type
- an ID is invalid/duplicated or a reference does not resolve
- `moduleId` differs from its containing module
- sibling `order` values are non-positive or duplicated
- level/type/audience rules are violated
- bilingual text lacks either role
- language or TTS locale conflicts with the course declaration
- a quiz has no question, a question has fewer than two answers, or correct-answer count is not one
- tags are invalid or duplicated

Errors identify the file and JSON path. A complete document is validated before activation; partial content never replaces known-good bundled content.

## Differences from LP2 and parser work required

These changes are documented before implementation. This task does not change Android code.

| LP2 behavior | Version-1 contract | Required future parser action |
|---|---|---|
| Scans `levels/levelN/lessonNN.json` | Explicit course/module identity | Read course/manifest; do not infer semantics from paths. |
| `textPt`, `textDa` | `text.support`, `text.target` | Map roles through course language config. |
| Grammar keys `pt`, `da` | Structured role-based text/notes | Preserve structure; format in presentation. |
| `titleDa`, `questionDa`, `explanationDa` | Bilingual objects | Return both roles, not Danish-only strings. |
| Numeric contiguous IDs | Stable string IDs plus `order` | Iterate actual IDs and remove `1..count` assumptions. |
| Children when title equals `Børn` | Explicit type/audience | Route using module metadata. |
| Hard-coded `pt-PT` | Course/item TTS config | Add locale-driven speech boundary; use `da-DK` primarily. |
| Permissive `opt*` parsing | Strict required/optional validation | Report file and JSON path. |
| Repository formats Danish grammar text | Structured content | Move labels/formatting to localized UI. |
| Numeric level/lesson/dialog progress keys | Stable course/module/lesson/entity tuple | Namespace storage; do not reuse LP2 preference name. |
| Legacy duplicate level-1 directory | One canonical source | Do not add a legacy FR-DA fallback. |

The smallest safe implementation is a new version-1 reader and generic models. An LP2 compatibility adapter is optional only if one runtime must read old LP2 assets; LP2 aliases must not enter the canonical reader.

## Synthetic fixture coverage

`linguistic/testdata/course.synthetic.json` contains one Level 1 lesson and one Children lesson, four items and two quiz questions per lesson. Across the fixture it exercises French support, Danish target text, explicit `da-DK` TTS, all four optional note types, stable IDs/references/orders/tags, quiz scoring inputs, progress identity, and Children routing.

This verifies contract sufficiency for display, French support, Danish target, Danish TTS, quiz, progress, and Children. It is not final linguistic content and must not be promoted to production assets.
