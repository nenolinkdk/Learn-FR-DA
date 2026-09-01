# Learn-FR-DA 0.5.2 — production test candidate

This document defines the final manual test candidate before web distribution or a later Google Play release.

## Identity

- Product: Learn-FR-DA
- Version name: 0.5.2
- Version code: 7
- Application ID: `dk.nenolink.learnfrda`
- Canonical APK filename: `Learn-FR-DA-0.5.2.apk`
- Canonical local release path after the production copy task: `releases/0.5.2/Learn-FR-DA-0.5.2.apk`
- Permanent release signing identity: see `docs/RELEASE_SIGNING.md`

## Build locally

The permanent signing key and passwords must remain outside Git and GitHub.

With the documented `NENOLING_*` signing values configured locally, run:

```powershell
.\gradlew.bat clean test copySignedReleaseApkToDist
```

The task builds the release variant and, only when release signing is configured, copies the signed APK to:

```text
releases/0.5.2/Learn-FR-DA-0.5.2.apk
```

The generated `releases/` directory is a local release staging location. Do not commit private signing material.

## Verify signature

Verify the canonical APK with Android SDK `apksigner` and confirm that its signer certificate SHA-256 is the permanent Nenoling certificate recorded in `docs/RELEASE_SIGNING.md`.

## Physical production test

Install the canonical APK on a physical Android device and test at minimum:

1. clean install and launch;
2. front page title, product intro, modules and footer;
3. one complete lesson including previous/next navigation;
4. support-language and target-language TTS;
5. final lesson item opens the quiz;
6. question order can vary between attempts while remaining stable within one attempt;
7. answer order can vary without changing correctness/scoring;
8. quiz result, repeat quiz and return to lesson;
9. practical resources and external browser links;
10. app restart/persistence behavior;
11. upgrade installation over the previously signed 0.5.2 candidate if applicable.

Record any defect before publishing the APK. A successful physical test makes this file a release candidate for web distribution. Google Play packaging/listing requirements are a later release step.

## Distribution rule

Do not distribute `app-debug.apk`, `Learn-FR-DA-debug.apk`, or the `-clean.apk` debug/test artifact as the production application. The production test candidate is the permanently signed release APK named `Learn-FR-DA-0.5.2.apk`.
