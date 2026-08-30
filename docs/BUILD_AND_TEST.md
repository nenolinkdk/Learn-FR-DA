# Build and Test

## Named test APK in `dist/`

Every successful debug assemble copies the current debug APK to a versioned local file:

```text
dist\Learn-FR-DA-<versionName>-test.apk
```

Source: `app/build/outputs/apk/debug/app-debug.apk`.  
Gradle task: `copyNamedTestApkToDist` (runs automatically after `assembleDebug`).

`dist/` stays on the machine (Dropbox/local checkout). The folder is gitignored. Never commit `*.apk`.

Windows check after a local build:

```powershell
Test-Path ".\dist\Learn-FR-DA-0.4.1-test.apk"
Get-Item ".\dist\Learn-FR-DA-0.4.1-test.apk" | Select-Object FullName, Length, LastWriteTime
```

## Test APK — 2026-08-30 (0.4.1)

App version: `versionName "0.4.1"` (`versionCode` 1), `RELEASE_DATE` `30.08.2026`.
Home/version display: `Version 0.4.1 · 30.08.2026` from `BuildConfig`.

Quiz answer buttons no longer show both Danish and French. Each question declares `answerDisplayRole` (`support` or `target`). The renderer paints only that role. Bilingual answer objects stay in JSON for reuse and for the existing bilingual explanation after the learner answers. `QuizIntegrity` / `ProductionCourseQuizTest` now reject bilingual translation leakage on answer buttons.

Test APK: `dist/Learn-FR-DA-0.4.1-test.apk` (gitignored, 1,037,711 bytes). `./gradlew clean assembleDebug` copies `app/build/outputs/apk/debug/app-debug.apk` to that named file. Totals unchanged: 5 modules, 50 lessons, 500 items, 150 quiz questions, 1206 unique IDs. `node --test` and `./gradlew test` pass, including answer-display leakage checks.

## Test APK — 2026-08-30 (0.4.0)

App version: `versionName "0.4.0"` (`versionCode` 1), `RELEASE_DATE` `30.08.2026`.
Home/version display: `Version 0.4.0 · 30.08.2026` from `BuildConfig`, not a separate hard-coded string.

Level 3 is a full production module (`module.level-3`): practical professional Danish for French-speaking users working, freelancing or doing business in Denmark. Ten lessons, 100 items, 30 quiz questions, 50 core business terms in context. Source files: `linguistic/production/level3-01-05.json` and `level3-06-10.json`. The home screen loads five modules from `course.json` in this order: Niveau 1, Niveau 2, Niveau 3, Enfants, Grammaire danoise pratique.

Footer destination corrected to `https://notaguidedtour.com` (visible label `notaguidedtour.com`). The credit line remains `© Nenolink · Henrik Nielsen`. The link still opens with Android `ACTION_VIEW`. No `INTERNET` permission.

Test APK: `dist/Learn-FR-DA-0.4.0-test.apk` (gitignored). `assembleDebug` copies `app/build/outputs/apk/debug/app-debug.apk` to that named file via `copyNamedTestApkToDist`. The `dist/` folder stays local; APK binaries are never committed.

`./gradlew clean assembleDebug` succeeded. The APK contains five modules, `versionName 0.4.0`, footer `https://notaguidedtour.com`, and no `INTERNET` permission.

Empty quiz screens reported on the previous physical-device build were a leftover `ScrollView` offset, not missing questions. Every production lesson now fails fast unless it resolves to exactly three displayable questions (`QuizIntegrity`). `node --test tools/quiz-integrity.test.mjs` and `./gradlew test` walk every lesson ID through the same lesson → quiz → questions mapping `MainActivity` uses. An AndroidX `connectedAndroidTest` was not added because the app keeps `android.useAndroidX=false`.

Expected linguistic totals: 5 modules, 50 lessons, 500 items, 150 quiz questions, 1206 unique IDs. Practical transport resources remain a separate collection (6 official links + collection ID). Duplicate IDs: 0. Content tests include answer-display leakage checks.

## Test APK — 2026-08-29 (0.3.3)

App version: `versionName "0.3.3"` (`versionCode` 1), `RELEASE_DATE` `29.08.2026`.
One dialogue correction: *De quel billet est-ce que j’ai besoin ?* Danish unchanged.
Test APK: `dist/Learn-FR-DA-0.3.3-test.apk` (gitignored).

## Content QA and practical links — 2026-08-29 (0.3.2)

App version: `versionName "0.3.2"` (`versionCode` 1), `RELEASE_DATE` `29.08.2026`.
Linguistic source of truth remains `linguistic/production/*.json`. Practical links are a separate collection in `linguistic/resources/transport.json`, bundled as `app/src/main/assets/content/fr-da/resources.json`. External URLs open with `ACTION_VIEW`; no `INTERNET` permission. Lessons remain offline-first. Tariffs and timetables are not stored.

See `docs/CONTENT_QA_0.3.2.md` for the linguistic change log.

Runtime on emulator `Medium_Phone_API_36.1` (`emulator-5554`): four modules open; corrected items display (city-hall *Rådhuspladsen*, spoken *Combien elle coûte ?*, airport tracks 12/13 note, *De quel billet j’ai besoin ?*); Grammar item and quiz feedback work; progress 1/100 survived reinstall/restart; FR and DA TTS dispatched with no error toast; practical links section lists six official destinations; each tap launched Chrome via `ACTION_VIEW` (Chrome ANR’d once on this RAM-constrained emulator; returning to the activity was safe); airplane mode still loaded bundled lessons; no `INTERNET` permission.

## UI refinement — 2026-08-29 (0.3.1)

App version: `versionName "0.3.1"` (`versionCode` 1), with `BuildConfig.VERSION_NAME` and a single `RELEASE_DATE` field (`29.08.2026`). Linguistic production JSON is unchanged.

Compact home/lesson presentation: Grammar last among the four course modules, compact FR/DA TTS row, smaller buttons and status text, round previous/next controls (`RoundNavBar`), no user-facing last-position message, and a home-only footer linking to `https://notaguidedtour.dk`.

## Production integration — 2026-08-28 (0.3.0 test)

The canonical production course is `app/src/main/assets/content/fr-da/course.json`.
Both debug and release applications load this asset through `ContentRepository.loadProductionCourse()`.
It contains four modules — Level 1, Level 2, Children and Grammar — with ten lessons each.
Module titles and lesson counts come from JSON. The Grammar placeholder has been removed.

The nine source files in `linguistic/production/` remain unchanged, including
`grammar-01-05.json` and `grammar-06-10.json`.
`linguistic/testdata/course.synthetic.json` remains unchanged and is mapped only into `androidTest` assets, never application assets.

App version: `versionName "0.3.0"` (`versionCode` 1). Play Store signing was not prepared.

## Exact content counts

| Scope | Modules | Lessons | Items | Quizzes | Questions | Answers | Grammar notes | Cultural notes | Digital notes | Pronunciation notes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Level 1 | 1 | 10 | 100 | 10 | 30 | 90 | 69 | 13 | 13 | 5 |
| Level 2 | 1 | 10 | 100 | 10 | 30 | 90 | 58 | 11 | 11 | 0 |
| Level 3 | 1 | 10 | 100 | 10 | 30 | 90 | 22 | 11 | 0 | 0 |
| Children | 1 | 10 | 100 | 10 | 30 | 90 | 9 | 7 | 4 | 0 |
| Grammar | 1 | 10 | 100 | 10 | 30 | 90 | 100 | 0 | 0 | 0 |
| Total | 5 | 50 | 500 | 50 | 150 | 450 | 258 | 42 | 28 | 5 |

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

The normal command validates all eleven source files and requires the checked-in canonical asset to equal the merged source records.
The writer validates the entire result before replacing the asset.

Checks include strict UTF-8 decoding, JSON parsing, version 1, required/unknown/null fields, value types,
course identity, French support and Danish target configuration, module type/audience, bilingual text,
ID/tag regexes, global ID uniqueness, parent references, unique positive integer orders, note shapes,
TTS roles/locales and exactly one correct answer per single-choice question.
Production additionally requires the five module IDs, lesson orders 1–10, three questions per lesson
and enabled target/da-DK speech for every item, including Grammar.

There are **1206 globally unique canonical IDs and zero duplicate entity IDs**.
Course and module IDs intentionally recur as matching metadata in separate source documents;
these shared headers are consolidated, not renamed. No lesson, item, quiz, question or answer IDs recur.

The 18 Node tests cover successful validation, exact preservation of every source lesson,
and rejection of unknown fields, null notes, duplicate/invalid IDs, duplicate/fractional orders,
invalid references, empty text, incorrect/missing TTS, invalid/duplicate tags,
zero/multiple correct answers, reversed languages and invalid Children audience.
These are static content tests, not Android runtime tests.

No linguistic record was rewritten. No structural correction was required in the new grammar files.

## Language and structural review

An explicit search of production JSON and runtime source found no `textPt`, `pt-PT`, Portuguese naming
or Portuguese lesson IDs. All 400 production items use enabled target/da-DK speech.
The merged package uses `contentVersion: 1.0.0-production`.

## TTS — configuration PASS; emulator playback dispatched

Primary speech is Danish (`da-DK`). The separate optional French button uses `fr-FR`.
On emulator `Medium_Phone_API_36.1`, Google TTS (`com.google.android.tts`) is installed.
Tapping Écouter en danois dispatched synthesis with no unavailable/failed toast.
Tapping Écouter en français likewise showed no error toast.

## Repository synchronization and production build — PASS

- Repository: C:\Users\henri\Dropbox\Privat\Nenolink\github\Learn-FR-DA
- `git pull origin main` brought in `grammar-01-05.json` and `grammar-06-10.json`.
- All nine production linguistic source files are present.

```powershell
.\gradlew.bat assembleDebug
```

BUILD SUCCESSFUL. Verified APK paths:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/manual-debug/Learn-FR-DA-debug.apk
dist/Learn-FR-DA-0.3.0-test.apk
```

`dist/` is gitignored. APK/build binaries are not committed.

## Device / emulator — PASS

`adb devices` listed `emulator-5554` (`Medium_Phone_API_36.1`) after starting the existing AVD.
`adb install -r app\build\outputs\apk\debug\app-debug.apk` succeeded (`versionName=0.3.0`).

Runtime checklist:

1. Home shows four real modules and no Grammar placeholder / synthetic content.
2. Each module lists ten JSON lessons (counts not hard-coded).
3. Grammar lesson 1 and lesson 10 open; Danish examples and French explanations display.
4. Previous/next/back and quiz-from-overview work.
5. Grammar quiz scored 3/3; result survived process restart.
6. Marked item completion and last position survived restart (Grammar 1/100).
7. Danish `å` (`Det er åbent i dag.`) and French accents (`aujourd’hui`, `français`, `café`) render.
8. Airplane mode: lesson content still loaded from bundled JSON; TTS tap produced no error toast.
   The manifest has no network permission.

One emulator “System UI isn’t responding” dialog appeared during first launch on a RAM-constrained host;
Wait dismissed it and the app continued. This was not reproduced as an application crash.

## Final status

- CONTENT VALIDATION: PASS
- MODULES: 4 — LESSONS: 40 — ITEMS: 400 — QUIZ QUESTIONS: 120
- DUPLICATE IDS: 0 in the canonical course
- BUILD: PASS
- ADB: PASS
- INSTALL: PASS
- LEVEL 1 / LEVEL 2 / CHILDREN / GRAMMAR: PASS
- TTS DA / TTS FR: PASS (engine dispatched; no error toast)
- QUIZ / PROGRESS / OFFLINE / VISUAL: PASS

This is a completed internal test version (`0.3.0`), not a Play Store release.
Remaining broader limitations include voice-quality QA on a physical device, presentation-state tests,
and accessibility/physical-device QA.

## Files in this integration

Modified:

- tools/production-content.mjs
- tools/production-content.test.mjs
- app/src/main/assets/content/fr-da/course.json
- app/src/main/java/dk/nenolink/learnfrda/MainActivity.java
- app/src/main/res/values/strings.xml
- app/build.gradle
- .gitignore
- docs/BUILD_AND_TEST.md

Linguistic source files: unchanged. Structural content corrections: none.
