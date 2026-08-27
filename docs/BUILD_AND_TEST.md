# Build and Test

## Production integration — 2026-08-27

The canonical production course is `app/src/main/assets/content/fr-da/course.json`.
Both debug and release applications load this asset through `ContentRepository.loadProductionCourse()`.
It contains Level 1, Level 2 and Children, with ten lessons each. Module titles and counts come from JSON.
Level 2 is no longer a placeholder; Grammar remains a placeholder. No standalone Grammar or global quiz content was generated.

The seven source files in `linguistic/production/` remain unchanged.
`linguistic/testdata/course.synthetic.json` remains unchanged and is mapped only into `androidTest` assets, never application assets.
Tests can load it with `new ContentRepository(instrumentationContext).loadCourse("course.synthetic.json")`.

## Exact content counts

| Scope | Modules | Lessons | Items | Quizzes | Questions | Answers | Grammar notes | Cultural notes | Digital notes | Pronunciation notes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Level 1 | 1 | 10 | 100 | 10 | 30 | 90 | 69 | 12 | 13 | 5 |
| Level 2 | 1 | 10 | 100 | 10 | 30 | 90 | 58 | 11 | 11 | 0 |
| Children | 1 | 10 | 100 | 10 | 30 | 90 | 9 | 7 | 4 | 0 |
| Total | 3 | 30 | 300 | 30 | 90 | 270 | 136 | 30 | 28 | 5 |

Notes are counted as present note objects, not separate grammar lessons.

## Automated validation — PASS

Run with Node.js (no npm packages required):

```powershell
node tools/production-content.mjs
node --test tools/production-content.test.mjs
```

To deliberately regenerate the asset after editing production sources:

```powershell
node tools/production-content.mjs --write
```

The normal command validates all seven files and requires the checked-in canonical asset to equal the merged source records.
The writer validates the entire result before replacing the asset.

Checks include strict UTF-8 decoding, JSON parsing, version 1, required/unknown/null fields, value types,
course identity, French support and Danish target configuration, module type/audience, bilingual text,
ID/tag regexes, global ID uniqueness, parent references, unique positive integer orders, note shapes,
TTS roles/locales and exactly one correct answer per single-choice question.
Production additionally requires the three module IDs, lesson orders 1–10, three questions per lesson
and enabled target/da-DK speech for every item.

There are **724 globally unique canonical IDs and zero duplicate entity IDs**.
Course and module IDs intentionally recur as matching metadata in separate source documents;
these shared headers are consolidated, not renamed. No lesson, item, quiz, question or answer IDs recur.
Answer arrays retain their source order; the contract does not define answer or quiz-level `order` fields.

The 18 Node tests cover successful validation, exact preservation of every source lesson,
and rejection of unknown fields, null notes, duplicate/invalid IDs, duplicate/fractional orders,
invalid references, empty text, incorrect/missing TTS, invalid/duplicate tags,
zero/multiple correct answers, reversed languages and invalid Children audience.
These are static content tests, not Android runtime tests.

## Language and structural review

All 300 item text pairs were reviewed for obvious reversed FR/DA roles; none were found.
An explicit search of production JSON and runtime source found no `textPt`, `pt-PT`, Portuguese naming
or Portuguese lesson IDs. French quotations in Danish quiz prompts are intentional translation exercises.
This review is not a full linguistic, safety or factual certification.

No linguistic record, ID, tag, translation, note or quiz answer was changed.
No JSON contract change was needed. The merged package uses `contentVersion: 1.0.0-production`.
The runtime now sorts lessons, items and questions by documented order (using Android 23-compatible APIs),
rejects fractional integer fields and malformed UTF-8, and reports the actual asset name on errors.
Progress identity remains course/module/lesson/entity IDs, independent of titles and positions.

The source Level 2 and Children topic sequences differ from the planning outline in LESSON_STRUCTURE.md.
Source order and content were preserved as requested. An editorial follow-up may reconcile the outline.
One translation to review separately: `item.level-1.public-transport.city-hall` has French
“Je vais à l’Hôtel de Ville.” and Danish “Jeg skal til Rådhuspladsen.” (town hall versus town hall square).
It was not rewritten because this task only authorizes structural corrections.

## TTS — static configuration PASS; device playback NOT RUN

Primary speech is Danish (`da-DK`). The separate optional French button uses `fr-FR` and does not alter
course defaults. Item speech now selects text using its declared role.
All 300 production items use enabled target/da-DK speech.
Representative Unicode samples checked in the asset:

| Module | Samples covering æ, ø, å |
|---|---|
| Level 1 | “Skærmen viser, at toget er fem minutter forsinket.”; “Ligeud og så til højre.” |
| Level 2 | “Ja, i mange tilfælde. Du kan også få hjælp hos kommunen.”; “Du skal følge vejledningen, der passer til din situation.”; “Jeg skal arbejde her i mindst et år.” |
| Children | “Må jeg være med?”; “Ja, selvfølgelig!” |

Actual voice availability, speech dispatch and audible pronunciation remain unverified.

## Build — FAIL (execution environment)

Requested command:

```powershell
.\gradlew.bat assembleDebug
```

An initial pre-integration invocation returned success with cached skeleton outputs; that is **not**
a successful production build. The final integration build did not complete.
After resolving local Java/cache selection, the concrete blocker was:

```text
java.nio.file.AccessDeniedException:
C:\Users\henri\AppData\Local\Android\Sdk\platforms\android-35\package.xml
Failed to find target with hash string 'android-35'
```

Explicit SDK read permission was requested and granted, but Windows still denied byte access.
Do not reinstall the SDK or accept licenses to conceal this access-control failure.
No production APK is verified. Any existing APK in the output directories is stale skeleton output.

Local environment used for the final attempt:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:GRADLE_USER_HOME=Join-Path (Get-Location) '.gradle-user'
$env:ANDROID_USER_HOME=Join-Path (Get-Location) '.android-user'
.\gradlew.bat assembleDebug --offline
```

Expected APK paths after a successful build:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/manual-debug/Learn-FR-DA-debug.apk
```

The manual-testing copy is generated by the existing assembleDebug finalizer.
The deprecated `android.useAndroidX=false` warning remains unrelated to this integration.

## Device / emulator — NOT RUN

`adb devices` could not start because Windows denied execution of
`C:\Users\henri\AppData\Local\Android\Sdk\platform-tools\adb.exe`, even after permissions were granted.
No device availability, installation, screenshot or runtime result is claimed.

Static flow review confirms that all loaded modules open their JSON lessons; every lesson has a quiz,
the final item routes to that quiz, scoring increments on the selected correct answer with a repeated-answer guard,
and completed items/latest position/quiz scores use SharedPreferences stable keys.
The manifest has no network permission, and course content is bundled locally.
Long titles use wrapping, content-sized buttons inside a ScrollView; visual clipping remains unverified.

Required follow-up on an accessible emulator or device:

1. Build and install the new APK; confirm offline startup.
2. Verify the three production titles and ten lessons per module.
3. Open first and last lessons of each module; inspect French/Danish text and long titles at normal and large font sizes.
4. Exercise previous/next/back and reach the quiz from the last item.
5. Complete a quiz with correct and incorrect answers and verify scoring and saved result.
6. Mark items complete; close/restart the app and confirm completion, last position and quiz result survive.
7. Play the Danish Unicode samples above, optionally play French, then play Danish again.
8. Repeat navigation and quiz checks offline. TTS requires an installed offline Danish voice.

## Final status and next step

- CONTENT VALIDATION: PASS
- DUPLICATE IDS: 0 in the canonical course
- BUILD: FAIL — SDK access denied
- DEVICE TEST: NOT RUN
- TTS DA: NOT RUN at runtime; configuration PASS
- PROGRESS: NOT RUN at runtime; stable-key persistence code reviewed
- QUIZZES: content PASS; runtime scoring/navigation NOT RUN

Next step: run the build and device checklist from Android Studio or PowerShell with working SDK access.
Do not treat this integration as release-ready until runtime checks pass. Remaining broader limitations include
voice selection/audio focus, presentation-state and progress migration tests, and accessibility/physical-device QA.
No accounts, cloud services, analytics, new lessons, Grammar content or UI redesign were added.

## Files in this integration

Created:

- app/src/main/assets/content/fr-da/course.json
- tools/production-content.mjs
- tools/production-content.test.mjs

Modified:

- app/build.gradle
- app/src/main/java/dk/nenolink/learnfrda/MainActivity.java
- app/src/main/java/dk/nenolink/learnfrda/content/ContentRepository.java
- app/src/main/res/values/strings.xml
- docs/BUILD_AND_TEST.md

Structural content corrections: none. Linguistic entries unable to integrate unchanged: none.
All source production files and the synthetic fixture are preserved byte-for-byte in Git.

## Commit result

The requested commit `feat: integrate FR-DA production content` could not be created.
Both `git add` and `git commit` were blocked by Windows access denial on `.git/index.lock`,
including after explicit write permission for `.git` was granted. Changes remain in the actual
repository working tree, unstaged; no commit or push is claimed.
After restoring Git metadata write access, validate again and commit the eight files listed above.
