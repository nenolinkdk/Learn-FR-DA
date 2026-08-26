# Build and Test

## Goal

Produce a stable Android release APK after documentation, code
adaptation, linguistic integration and QA.

## Development sequence

1.  Read all `/docs`.
2.  Inspect Learn Portuguese 2.
3.  Document actual reuse points.
4.  Build FR-DA skeleton with test JSON only.
5.  Verify navigation, JSON loading and TTS.
6.  Integrate final linguistic datasets.
7.  Perform linguistic QA.
8.  Perform Android QA.
9.  Build release APK.

## Functional tests

-   app starts cleanly
-   module/level navigation works
-   lesson navigation works
-   next/back behaviour is consistent
-   progress is saved correctly
-   JSON errors fail visibly and safely
-   Danish TTS uses the correct locale/voice when available
-   French TTS can be supported without breaking Danish playback
-   quiz scoring/progression works
-   Children module is reachable and independent
-   offline content works without network access

## Linguistic tests

-   UTF-8 and special characters
-   natural Danish
-   natural French
-   no Portuguese remnants
-   no accidental language-direction reversal
-   consistent terminology
-   digital/cultural notes remain concise and current
-   dialogue items form coherent situations
-   repetition is controlled

## Device tests

Test on: - Android emulator - at least one physical Android device

Check small-screen layouts and long French strings.

## Release

The final deliverable is an installable release APK. Record version
number and build date. Keep source and linguistic data in Git.
