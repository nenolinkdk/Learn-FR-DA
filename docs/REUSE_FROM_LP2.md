# Reuse from Learn Portuguese 2

## Purpose

This document defines the reuse strategy. It is initially a
specification; Codex should update it after inspecting the actual Learn
Portuguese 2 source.

## Reuse candidates

Investigate direct or adapted reuse of: - Android project architecture -
navigation - welcome screen - level selection - lesson selection -
dialogue/phrase display - JSON loading/parsing - TTS abstraction -
next/previous navigation - breadcrumbs - progress/completion tracking -
statistics - quiz engine - settings - documentation/about screens -
reusable UI components

## Must be audited before reuse

Search explicitly for: - Portuguese locale codes - Portuguese TTS
assumptions - hard-coded Portuguese strings - Portuguese lesson names -
Portuguese grammar categories - Portuguese-specific JSON fields -
Portuguese assets and icons - package/application names - hard-coded
source/target language direction - assumptions that explanations are in
Danish

## FR-DA changes

-   French becomes the support/interface language.
-   Danish becomes the target/TTS language.
-   Content must load from FR-DA JSON.
-   Digital/cultural notes must be supported.
-   Children is a first-class module.
-   UI palette changes to pastel off-white/blue/red.

## Refactoring objective

Where reasonable, replace language-specific constants with
configuration. Future Nenoling apps should ideally change language
configuration, content datasets, branding and selected modules rather
than rewrite the application.

## Codex task before implementation

1.  Inspect Learn Portuguese 2.
2.  List reusable components and Portuguese-specific dependencies.
3.  Update this document with actual paths/classes/resources.
4.  Propose the smallest safe refactor.
5.  Only then begin migration.
