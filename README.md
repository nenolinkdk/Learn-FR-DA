# Learn FR-DA

Android language-learning app for French-speaking users learning
practical Danish for use in Denmark.

## Status

Phase 1: documentation and specification.

## Target groups

-   Primarily French-speaking tourists in Denmark
-   New arrivals and people moving to Denmark for work
-   Freelancers and people doing business in Denmark
-   Families
-   Children needing simple practical Danish

## Core principles

-   French is the interface and explanation language.
-   Danish is the target language.
-   Danish TTS is a core feature; French TTS should remain technically
    possible.
-   Linguistic content is stored in JSON and kept separate from Android
    code.
-   The technical basis is Learn Portuguese 2, but Portuguese-specific
    assumptions must not be copied blindly.
-   Digital Denmark is integrated into realistic situations: MitID,
    Digital Post, MobilePay, health card, travel cards and travel apps.
-   Core learning content should work offline.
-   Dialogue French uses a spoken register; grammar and reference notes
    may keep a more written register.
-   Practical transport links open in the device browser. The app does
    not store ticket prices or timetables.

## Planned structure

-   Level 1: Visiting Denmark --- 10 lessons
-   Level 2: Living and working in Denmark --- 10 lessons
-   Level 3: Working and doing business in Denmark --- 10 lessons
-   Children / Enfants module
-   Grammar
-   Quiz

## Development phases

1.  Documentation and specification
2.  Reuse/adaptation of Learn Portuguese 2
3.  Linguistic JSON production
4.  Integration and linguistic QA
5.  Android QA and release APK

Read `/docs` before implementing application code.

A successful `assembleDebug` also writes
`dist/Learn-FR-DA-<version>-test.apk` (gitignored; local/Dropbox only).
