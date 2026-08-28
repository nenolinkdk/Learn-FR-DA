# Build and Test

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
| Level 1 | 1 | 10 | 100 | 10 | 30 | 90 | 69 | 12 | 13 | 5 |
| Level 2 | 1 | 10 | 100 | 10 | 30 | 90 | 58 | 11 | 11 | 0 |
| Children | 1 | 10 | 100 | 10 | 30 | 90 | 9 | 7 | 4 | 0 |
| Grammar | 1 | 10 | 100 | 10 | 30 | 90 | 100 | 0 | 0 | 0 |
| Total | 4 | 40 | 400 | 40 | 120 | 360 | 236 | 30 | 28 | 5 |

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

The normal command validates all nine files and requires the checked-in canonical asset to equal the merged source records.
The writer validates the entire result before replacing the asset.

Checks include strict UTF-8 decoding, JSON parsing, version 1, required/unknown/null fields, value types,
course identity, French support and Danish target configuration, module type/audience, bilingual text,
ID/tag regexes, global ID uniqueness, parent references, unique positive integer orders, note shapes,
TTS roles/locales and exactly one correct answer per single-choice question.
Production additionally requires the four module IDs, lesson orders 1–10, three questions per lesson
and enabled target/da-DK speech for every item, including Grammar.

There are **965 globally unique canonical IDs and zero duplicate entity IDs**.
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
