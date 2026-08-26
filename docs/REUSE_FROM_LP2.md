# Reuse from Learn Portuguese 2

## Executive summary

The inspected LP2 source (`LearnPortuguese2-clean`, branch `agent/curate-lp3-story-scenes`) is a small offline native Android app: one Java `app` module, a 2,136-line `MainActivity`, programmatic Android Views, `org.json` assets, plain Java models, Android `TextToSpeech`, and `SharedPreferences` progress. It has no fragments, composables, XML layouts, navigation graph, database, networking layer, dependency injection, or Android test suite.

Reuse the proven behaviors—local content, level discovery, lesson/dialog flow, quiz rules, simple progress, and lazy speech—but not the Portuguese names or monolithic implementation. LP2 hard-codes `textPt`, grammar key `pt`, `pt-PT`, Danish UI strings, package/product names, numeric identity assumptions, and Children detection by the title `Børn`.

The smallest safe strategy is an incremental generic extraction using the version-1 contract in `JSON_FORMAT.md`. Do not copy LP2 wholesale and do not rewrite everything before tests exist.

Classifications:

- **A:** reuse unchanged
- **B:** reuse with small modifications
- **C:** refactor into generic Nenoling functionality
- **D:** replace for FR-DA
- **E:** do not reuse

## Existing LP2 architecture

- Gradle Groovy, one `:app` module, Java 17, min SDK 23, compile/target SDK 35.
- Namespace/application ID: `dk.nenolink.learnportuguese2`.
- One launcher activity; manifest requests no permissions.
- All screens, navigation state, styling, quiz behavior, TTS, settings, fallbacks, and much formatting live in `MainActivity.java`.
- `LessonRepository.java` scans `assets/levels/levelN`, parses `level.json` and `lessonNN.json`, and supports a legacy level-1 fallback under `assets/lessons`.
- `ProgressRepository.java` stores latest numeric level/lesson/dialogue, completed dialogue keys, and lesson quiz results in `SharedPreferences`.
- Assets contain four Portuguese levels, a separate number dataset, and a Danish user guide. Level 3 is Children; folder level 4 represents product level 3.
- Resources contain a minimal theme and launcher vectors. Most strings, colors, padding, and type sizes are hard-coded in Java.

## Directly reusable components

- Gradle wrapper mechanics and a single-module starting point (**A/B**).
- Permission-free, offline launcher baseline (**B**).
- UTF-8 asset reading, immutable collection returns, and deterministic discovery/sorting concepts (**B/C**).
- Quiz answer/result concepts and validation that a question has choices and a correct answer (**A/B**).
- Level-aware progress namespacing concept and defensive copy of preference string sets (**B**).
- Version/build/release-date presentation from Gradle/package metadata (**B**).
- Screen flow concepts: module/lesson/dialogue/phrase, story, quiz, progress, guide, settings (**B/C**).

No complete production feature file should be copied unchanged: even generic-looking classes use the Portuguese package, numeric IDs, or language-specific fields.

## Components requiring modification

- Rename root project, namespace, app ID, manifest label, preferences, release artifact, and package tree.
- Retain the broad model shapes while introducing stable string IDs, course/module identity, language roles, audience, and locale metadata.
- Keep local progress initially, but key it by course/module/lesson/entity IDs and iterate real IDs rather than `1..count`.
- Keep quiz scoring/navigation behavior, while separating it from `MainActivity` and localizing labels in French resources.
- Keep the familiar vertical flow, but move palette/styles/strings to resources and test long French strings and accessibility.
- Replace settings prose with course-driven Danish/French voice status.

## Components to generalise

- **Content:** strict versioned parser with `support`/`target` roles instead of Portuguese/Danish property names.
- **Language configuration:** course ID, French support locale, Danish target locale, course locale, and TTS defaults.
- **Presentation:** state/navigation and small renderers separated from the monolithic activity; Compose is not required for this extraction.
- **Modules:** explicit `level`, `children`, `grammar`, and `quiz` types rather than localized-title tests.
- **TTS:** a speech service receiving text, role, and BCP 47 locale.
- **Progress:** neutral repository interface and stable identity tuple.
- **Validation:** required/optional field rules and JSON-path errors instead of permissive `opt*` defaults.

## Portuguese-specific elements to remove

- `LearnPortuguese2` project/product/package/application/APK/log/preference names.
- `new Locale("pt", "PT")` and Portuguese speech messages/settings.
- `textPt`, model fields named `portuguese`, grammar example `pt`, and the hard-coded Portuguese half of vocabulary/numbers.
- Fallback labels such as `Learn Portuguese`, `Dansk til europæisk portugisisk`, and Portuguese AI disclosure text.
- Danish hard-coded screens, buttons, quiz prompts, errors, guide copy, and footer wording.
- Portuguese levels, grammar (`ser`, `estar`, `ter`, conjugations and tense notes), scenarios, number data, guide, and Children safety content.
- Children detection using `titleDa == "Børn"`.
- The validator's LP2 paths, product counts, Portuguese content rules, and Learn Portuguese 3 assumptions.
- Green/yellow branding unless separately approved as generic Nenoling-owned visual material.

## JSON architecture analysis

LP2 reads `assets/levels/levelN/level.json` and numeric `lessonNN.json` files. Level metadata uses `id`, `titleDa`, `subtitleDa`, `introDa`, and `aiDisclosureDa`; `contentVersion` and `productLevel` occur but are ignored. Lessons use `id`, `titleDa`, `descriptionDa`, `dialogues`, `quiz`, and optional story fields. Dialogues contain `id`, Danish title/objective, `phrases`, `vocabulary`, and `grammar`.

Phrases and vocabulary require `textPt`/`textDa`; phrases add `speaker`/`grammarDa`. Grammar can contain Danish title/explanation, `verb`, `verbDa`, conjugation rows, examples with `pt`/`da`, `notesDa`, and common mistakes. Quiz uses numeric ID, `questionDa`, answers with `text`/`correct`, and `explanationDa`. Numbers use `textPt`, `textDa`, and `noteDa`.

Important parser assumptions:

- most fields use `opt*`, so malformed required values silently become empty/zero
- level/lesson/dialogue IDs are numeric; completion assumes contiguous dialogue IDs from 1
- directory and filename conventions determine identity
- level 1 has a duplicate legacy fallback
- one prototype dialogue may be split into ten synthetic dialogues with Danish generated labels
- unknown/unconsumed fields are ignored
- grammar parsing also performs Danish presentation formatting

Decision: migrate to the minimally revised generic schema in `JSON_FORMAT.md`. Preserve the hierarchy but replace language names with roles, add course/module/audience/TTS metadata and strict validation, and use stable IDs plus independent order. An LP2 compatibility adapter is optional; aliases such as `textPt` do not belong in the canonical FR-DA reader.

## TTS analysis

LP2 initializes TTS lazily in `MainActivity`, calls `setLanguage(new Locale("pt", "PT"))`, and speaks the current Portuguese phrase using `QUEUE_FLUSH`. It shuts down on activity destruction. There is no separate speech layer, voice selection, offline check, rate/pitch UI, audio focus, utterance state, or Danish speech path.

FR-DA must configure `da-DK` as primary target speech and retain optional `fr-FR` support speech. Each request gets text plus explicit role/locale from the content/course contract. Missing voice data is visible but non-blocking. Android code changes are intentionally deferred until the documented contract is approved.

## Navigation and progress analysis

LP2's common flow already handles discovered adult levels, lesson/dialogue/phrase navigation, lesson and aggregated quizzes, progress, stories, number practice, guide, and settings. Level 1/2 can therefore share the behavioral model. Grammar currently appears inside dialogue content rather than as a top-level destination.

Children currently uses the adult renderer and is separated only by an exact Danish title. FR-DA requires explicit module metadata and a child-safe entry/exit shell, but can share lesson and quiz rendering.

Progress is sufficient only for one simple local profile. It does not track item position, story completion, content version, generated quiz results, profiles, or richer statistics. Namespace it by course/module and stable IDs, decide what counts as completion, and test migrations before release.

## UI/theme analysis

LP2 has no reusable layout XML or Compose components. Its programmatic UI uses warm off-white, dark green, mint, pale blue, lavender, peach, and pink constants. FR-DA should extract tokens/resources for pastel off-white, soft blue, muted red, and accessible dark text. Muted red must not be the only state cue.

All interface strings move to French Android resources; linguistic text stays in JSON. Preserve scrollable vertical layout initially, but allow buttons to wrap/stack and test small screens, font scaling, TalkBack, French length, and Danish/French special characters. Children may have a playful variant of the same system, not a title-driven color hack.

## Children module implications

Reuse the lesson/item/quiz engine, local-only baseline, and progress concept. Replace level-3/title coupling with `type: "children"` and `audience: "children"`. Give Children an explicit route, progress namespace, safe navigation boundaries, and guarded settings/external links. Do not copy Portuguese travel/emergency content or expose adult Digital Denmark identity/payment/health topics.

## Recommended migration strategy

1. Freeze LP2 as behavioral reference and define smoke tests.
2. Approve the generic contract and synthetic fixtures.
3. Add tests describing current LP2 parsing/progress behavior.
4. Implement generic models and a strict version-1 reader using the synthetic fixture.
5. Add an LP2 adapter only if old LP2 assets must run in the same engine.
6. Generalise progress storage and migration.
7. Generalise TTS and `da-DK`/`fr-FR` fallback state.
8. Extract navigation state/renderers from `MainActivity` while retaining Views initially.
9. Apply French UI resources, FR-DA theme, and explicit Children shell.
10. Produce reviewed real content only after parser and navigation tests pass.

## Risks

1. **Largest risk:** UI, navigation, quiz, TTS, styling, formatting, and state are coupled in one untested 2,136-line activity.
2. Permissive JSON parsing can make incomplete content look valid.
3. Numeric/contiguous IDs and localized title detection can break completion and routing.
4. No Android tests protect extraction behavior.
5. Duplicate legacy level-1 content can diverge.
6. Hard-coded Danish UI and horizontal programmatic layouts create localization/accessibility risk.
7. Android plugin/SDK/disabled-AndroidX settings must be verified rather than copied blindly.
8. Reuse must come from an approved LP2 revision with ownership/license history verified, not a generated APK.

## Proposed implementation sequence

The first implementation task should be a generic content-contract spike: create generic models and a strict version-1 parser, load `linguistic/testdata/course.synthetic.json` in unit tests, and prove role/locale/module/progress mappings. This must happen before final linguistic JSON, UI redesign, or bulk LP2 migration.

## File-by-file reuse table

| LP2 path | Component | Reuse classification | FR-DA action | Notes |
|---|---|---:|---|---|
| `settings.gradle` | Repository/root setup | B | Rename root; verify tooling | Single module is enough initially. |
| `build.gradle` | Android plugin declaration | B | Retain pattern; verify version | No shared conventions exist. |
| `gradle.properties` | Android/Gradle flags | B/E | Review each; do not copy blindly | AndroidX/Kotlin disabled; unusual compatibility flags. |
| `gradlew*`, `gradle/wrapper/*` | Gradle wrapper | A/B | Reuse approved wrapper or regenerate | Verify provenance/version. |
| `app/build.gradle` | App/release configuration | B | Rename namespace, app ID, artifact, metadata | Product identifiers are LP2-specific. |
| `app/src/main/AndroidManifest.xml` | Launcher/no-permission baseline | B | Rename app/activity; retain minimal permissions | Strong offline baseline. |
| `.../MainActivity.java` | UI/navigation/quiz/TTS/settings | C | Behavioral reference; split before reuse | 2,136-line monolith. |
| `.../LessonRepository.java` | Asset discovery/parser/formatting | C | Strict generic parser; remove formatting | Portuguese keys and permissive defaults. |
| `.../ProgressRepository.java` | Local progress | B/C | Stable course/module/entity keys | SharedPreferences can serve a simple first version. |
| `.../model/Level.java` | Level metadata | C | Generic module/audience/version model | Numeric/Danish assumptions. |
| `.../model/Lesson.java` | Lesson aggregate | B/C | Retain shape with generic text/IDs | Some root JSON fields are ignored. |
| `.../model/Dialogue.java` | Dialogue aggregate | B | Map to generic ordered items | Useful structure. |
| `.../model/Phrase.java` | PT/DA phrase | C | `text.support`/`text.target` | Direct language coupling. |
| `.../model/VocabularyItem.java` | PT/DA vocabulary pair | C | Generic role pair | Direct language coupling. |
| `.../model/GrammarNote.java` | Flattened grammar | C | Preserve structured notes | Formatting belongs in UI. |
| `.../model/QuizQuestion.java` | Question/answers/explanation | B/C | Stable ID and bilingual fields | Current question/explanation are Danish-only. |
| `.../model/QuizAnswer.java` | Answer/correct flag | A/B | Retain concept; add stable ID | Contract requires exactly one correct answer. |
| `.../model/QuizResult.java` | Result value | A/B | Reuse/adapt | Not central to current save path. |
| `.../model/UserProgress.java` | Progress value | C | Align with stable identities | Repository mostly exposes primitives. |
| `.../model/NumberEntry.java` | PT/DA number pair | C/D | Generic activity or replace later | Hard-coded asset path and language fields. |
| `.../tts/.gitkeep`, `.../ui/*/.gitkeep` | Empty scaffolding | E | Do not copy | No implementation exists there. |
| `assets/levels/level1..4/*` | Portuguese course content | D | Schema reference only; replace later | Not FR-DA linguistic content. |
| `assets/lessons/*` | Legacy duplicate level 1 | E | Do not copy | Ambiguous duplicate source. |
| `assets/content/numbers_1_100.json` | Portuguese numbers | D | Replace later | Not part of this test dataset. |
| `assets/docs/user_guide.json` | Danish LP2 guide | D | Replace with French FR-DA guide | Portuguese product/TTS wording. |
| `res/values/styles.xml` | Minimal theme | B | Retain baseline if Views remain | Apply FR-DA tokens. |
| `res/values/colors.xml` | Green launcher color | D | Replace/review | Runtime palette mostly in Java. |
| `res/drawable`, `res/mipmap-*` | Launcher vector/wrappers | B/D | Reuse structure; rebrand | Confirm ownership. |
| `tools/validate_navigation_content.ps1` | Static validator | C | Replace with contract-aware validation/tests | Encodes LP2 paths/content/counts. |
| `docs/*`, `README.md`, `CHANGELOG.md` | LP2 documentation | D | Historical input only | Product direction differs. |
| `app/*.apk`, `app/build/**`, `build/**`, `.gradle/**` | Generated artifacts | E | Never copy | Rebuild from target source. |
