# Learn FR-DA --- Project Specification

## 1. Purpose

Learn FR-DA is an Android language-learning and practical communication
app for French-speaking users who need Danish in Denmark. It is
primarily intended for tourists, but also supports longer stays,
work-related relocation, families and children.

It is not intended to be a complete Danish course. Its main purpose is
to help users understand and produce useful Danish in real situations.

## 2. Development principle

Learn FR-DA should reuse the technical architecture of Learn Portuguese
2 wherever practical. The project must distinguish between reusable app
functionality, language-pair-specific functionality, Portuguese-specific
functionality to remove, and FR-DA linguistic content stored in JSON.

The project should also move the Nenoling architecture toward easier
reuse for future language pairs.

## 3. Language architecture

-   User/support language: French
-   Target language: Danish
-   Menus, instructions, grammar and practical notes: primarily French
-   Dialogues: French and Danish
-   Core TTS: Danish
-   French TTS: technically supported where practical
-   Linguistic content: separated from code

## 4. Main learning areas

### Level 1 --- Visiting Denmark

Ten practical lessons for tourists and short visits. Themes include
arrival, airport/station, public transport, accommodation, café,
restaurant, shopping/payment, directions/sightseeing, digital
tickets/apps, and problems/help.

### Level 2 --- Living and working in Denmark

Ten lessons for longer stays. Themes include home/neighbourhood,
shopping, workplace, appointments, health, public services,
MitID/Digital Post, banking/payment, commuting, and social life.

### Children / Enfants

A separate module designed for children rather than a simplified copy of
adult lessons. Situations may include meeting children, play, school,
sports, swimming pool, library, shops, computer/games, being lost,
transport, finding parents and simple emergencies.

## 5. Digital Denmark

Digital interaction is part of normal Danish life and must appear
naturally in relevant lessons, not only in a separate technology lesson.

Examples: - Transport: digital tickets, Rejsekort/relevant travel-card
solutions, Rejsebillet/DOT/DSB and journey-planning apps - Payment:
cards, contactless payment, MobilePay - Public administration: MitID,
Digital Post, borger.dk, CPR number, NemKonto - Health: health card,
digital appointments and relevant health services/apps - Everyday life:
QR codes, booking, parking apps, electronic receipts and digital queue
systems

The app teaches language and context; it is not an authoritative
public-service manual.

## 6. Linguistic approach

Lessons are situation-based. A typical lesson contains approximately ten
dialogue/communication units forming a small coherent story or
situation.

Avoid repetitive textbook patterns and generic phrases repeated across
lessons. Prefer natural contemporary Danish that users are likely to
hear and need.

Important FR→DA areas include en/et, definiteness, plural, adjective
agreement, pronouns, forms of address, present tense, infinitive, modal
verbs, perfect tense, V2, inversion, placement of ikke, questions,
der/det/man, prepositions, pronunciation and numbers.

## 7. Pronunciation

Danish pronunciation requires special attention for French speakers.
Danish TTS supports target-language examples. The architecture should
allow later addition of pronunciation notes, listening exercises and
repeat-after-me activities.

## 8. Data

Initial content files are expected to include: - level1.json -
level2.json - children.json - grammar.json - quiz.json

Possible later files: - vocabulary.json - culture.json

## 9. UI

Use a calm Nenoling visual identity with pastel/off-white background,
soft blue primary elements, muted red accents and dark neutral text. The
association with French/Danish should be subtle rather than repeated
flag graphics. The Children section may be more playful while remaining
part of the same design system.

## 10. Offline-first

Core lessons and JSON content are bundled with the app. No account is
required for normal learning. TTS dependencies should fail gracefully if
a required Android voice is unavailable.

## 11. Definition of first release

Version 1 should contain: - French UI - Danish target content - Level 1
and Level 2 - Children module - Grammar - Quizzes - Danish TTS -
Progress tracking - Documented JSON architecture - Nenoling visual
identity - Offline lesson content - Successful installation/test on a
physical Android device - Release APK
