# Build and Test

## Current implementation status

The first Android skeleton is implemented as a single native Java `app` module. It deliberately validates architecture rather than final linguistic scope.

Implemented:

- Generic version-1 Nenoling content models and strict JSON parser.
- The existing `linguistic/testdata/course.synthetic.json` is included directly as a bundled Android asset; it is not duplicated into production content.
- French support/interface text and Danish target text.
- Explicit language configuration from the course document: `fr-FR` support and `da-DK` target.
- Level 1 synthetic lesson flow.
- Children / Enfants synthetic lesson flow selected through module `type` and `audience`, not a translated title.
- Lesson overview, ordered items, previous/next navigation, optional notes, and quiz.
- Lazy, locale-driven Android TTS for Danish target text, plus optional French support playback.
- Graceful TTS unavailable/failure messages.
- `SharedPreferences` progress using stable course/module/lesson/entity IDs.
- Local item completion, latest-position restore information, and saved quiz result.
- French Android string resources and a minimal off-white/soft-blue/muted-red identity.
- Offline bundled content with no Android network permission or runtime service.
- Placeholder navigation for Level 2 and Grammar; no final content is implied.

Not implemented in this phase:

- Final Level 1/2, Children, grammar, or quiz content.
- Full UI redesign, production accessibility QA, accounts, sync, analytics, or cloud services.
- LP2 legacy JSON compatibility; canonical FR-DA code accepts only the generic contract.

## Project structure

```text
app/
  build.gradle
  src/main/
    AndroidManifest.xml
    java/dk/nenolink/learnfrda/
      MainActivity.java
      content/
        ContentModels.java
        ContentContractException.java
        ContentRepository.java
      progress/ProgressStore.java
      speech/SpeechController.java
    res/
linguistic/testdata/course.synthetic.json
```

The Gradle source set maps `linguistic/testdata` into debug/application assets. The parser opens `course.synthetic.json` from Android `AssetManager` at runtime.

## Build command

From the repository root:

```powershell
.\gradlew.bat assembleDebug
```

When the build succeeds, Gradle produces:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/manual-debug/Learn-FR-DA-debug.apk
```

The second path is a stable manual-testing copy generated after `assembleDebug`.

## Build result in this task

Gradle wrapper generation succeeded with Gradle 9.6.1, and the Android project/plugin configuration was reached. The full debug build could not proceed to resource or Java compilation in the Codex sandbox because Windows denied the Gradle process byte access to the already installed SDK files under:

```text
C:\Users\henri\AppData\Local\Android\Sdk
```

The concrete failure occurred while reading `platforms/android-35/package.xml`; the same sandbox ACL also denied reads of `android.jar`, `aapt2.exe`, and the SDK license file. Gradle consequently reported platform/build-tools licenses as unavailable. This is an execution-environment access failure, not a diagnosed project compile error. No debug APK was generated in this task.

Run the build command in a normal local PowerShell or Android Studio session with access to the installed SDK. Do not accept or reinstall SDK licenses merely to work around the Codex sandbox unless the local SDK Manager independently reports them missing.

## Tests and validations performed

### Synthetic contract validation — passed

- JSON parses as UTF-8.
- Exactly two modules: Level 1 and Children.
- Exactly two lessons, eight items, and four quiz questions.
- 27 course/module/lesson/item/quiz/question/answer IDs are valid and globally unique.
- Lesson `moduleId` references resolve.
- Every item contains French `support`, Danish `target`, and target/`da-DK` TTS.
- Every single-choice question has exactly one correct answer.
- Grammar, cultural, digital, and pronunciation note shapes are represented.

### Source and resource validation — passed

- All Android XML files parse as well-formed XML.
- Java front-end parsing reported no syntax errors. Full Android type resolution still requires the SDK build.
- Static flow checks found module, lesson, item, quiz, progress, and speech wiring.
- The manifest has one exported launcher activity and requests no permissions.
- The Gradle asset source set points to the existing synthetic fixture.

### Portuguese-remnant audit — passed for runtime scope

The Android source, resources, Gradle files, and synthetic fixture were searched for:

- `textPt`
- `pt-PT`
- Portuguese/portugis naming
- LearnPortuguese package/product names
- hard-coded `Locale("pt", ...)`
- `titleDa`, `questionDa`, and `explanationDa`
- the LP2 progress preference name

No runtime matches remain. Portuguese terms intentionally remain in historical architecture documentation explaining what was removed; they are not compiled or bundled as lesson content.

### Device/runtime tests — pending

The SDK ACL prevented APK generation, installation, and emulator/device launch in this task. These checks remain mandatory:

1. App starts without a network connection.
2. Level 1 and its synthetic lesson open.
3. French and Danish strings render without clipping at large font scale.
4. Previous/back/next navigation behaves correctly.
5. `da-DK` TTS speaks Danish or shows the unavailable message.
6. Optional `fr-FR` TTS does not change the Danish default.
7. Item completion and latest position survive process restart.
8. Quiz score is saved and restored.
9. Children opens as a distinct module.

## Known limitations and technical debt

- `MainActivity` still owns the small screen state and programmatic View rendering. New parser, progress, and TTS responsibilities are isolated, but a later phase should introduce testable presentation state before the screen set grows.
- Parser validation is strict and runtime-based; an equivalent JVM test suite and/or generated JSON Schema should be added after the first successful Android build.
- `SharedPreferences` is sufficient for the skeleton but will need migration/version tests before content IDs ship publicly.
- Quiz supports only the documented `single-choice` type.
- TTS does not yet expose voice selection, rate, audio focus, or utterance highlighting.
- Level 2 and Grammar are placeholders because final content is explicitly out of scope.
- The UI uses one responsive vertical layout but has not received physical-device, TalkBack, landscape, or full accessibility testing.
- The Gradle flag `android.useAndroidX=false` follows the no-dependency LP2 baseline and is deprecated in Android Gradle Plugin 9.2.1; reassess it before broader UI work.

## Remaining LP2 reuse and debt

Reused concepts:

- Single-module native Android baseline.
- Offline JSON assets.
- Module/lesson/item progression and previous/next flow.
- Simple local completion/quiz persistence.
- Lazy Android TTS and graceful missing-voice behavior.
- Programmatic View approach for an incremental skeleton.

Generalised or replaced:

- Portuguese/Danish field names became `support`/`target` roles.
- Numeric level/dialog IDs became stable course/module/lesson/entity IDs.
- Title-based Children detection became explicit module metadata.
- Hard-coded Portuguese TTS became per-course/per-item locale configuration.
- Permissive LP2 parsing became required-field, unknown-field, ID, reference, tag, locale, and quiz validation.
- TTS and progress moved out of `MainActivity`.
- Danish LP2 interface copy became French Android resources.

## Next recommended implementation step

First run `assembleDebug` and the runtime checklist in a normal local Android environment. Once that baseline passes, add automated tests for the version-1 parser and stable progress keys before creating or importing any final linguistic content. The next feature work should then add presentation-state tests and accessibility fixes—not the 20 production lessons yet.
