# Nenoling app template

This document records the reusable architecture and pedagogical rules learned from Learn-FR-DA. It is intended as the baseline for future Nenoling language apps with other language pairs.

## 1. Course architecture

A full course can use the following module pattern:

1. Level 1: visitor/travel situations
2. Level 2: everyday life and work
3. Level 3: professional/business communication
4. Children: age-appropriate everyday situations
5. Grammar: practical reference and exercises
6. Practical resources: external reference links kept separate from linguistic lesson items

A standard teaching module contains 10 lessons. A standard lesson contains 10 communication items and 3 quiz questions. Lesson counts and module titles must come from course data, not hard-coded UI values.

The source language is the support language and the language being learned is the target language. TTS locale and labels must be configurable by language pair rather than hard-coded for French/Danish.

## 2. Level 3: professional/business language

Level 3 is practical professional language, not an accounting, legal, finance or management course. It should serve employees, freelancers, small businesses, customers and suppliers.

Recommended 10-lesson structure:

1. Contacting a company
2. Requesting and making an offer/quotation
3. Negotiating price
4. Orders and delivery
5. Invoice and payment
6. Accounting vocabulary and costs
7. Investment and financing vocabulary
8. Buying, renting and leasing
9. Contracts and terms
10. Freelancer/small-business workflow from enquiry to payment

The module should introduce approximately 50 core business terms naturally through professional situations. Do not turn the module into 50 isolated vocabulary cards.

Core semantic fields include company, customer, supplier, product, service, order, quotation, estimate, price, discount, agreement, contract, terms, negotiation, invoice, payment, payment deadline, due date, bank transfer, receipt, credit note, accounting, bookkeeping, cost, expense, income, turnover, profit, loss, VAT, tax, budget, investment, financing, buying, selling, renting, leasing, salary, hourly rate, fixed price and delivery.

Dialogue should model realistic workflows and contemporary professional spoken language. Avoid repetitive generic prompts such as “Can you help me?” unless the situation genuinely calls for them.

## 3. Quiz architecture

Every normal lesson has exactly 3 quiz questions unless a course specification explicitly says otherwise.

Quiz integrity is checked per lesson, not only by global question count. For every lesson the runtime mapping must resolve to exactly three non-empty questions with non-empty answer choices and exactly one correct answer.

### 3.1 No answer leakage

Translation questions test one direction at a time.

If the question gives a target-language word and asks for its meaning, answer buttons display support-language choices only.

If the question gives a support-language word and asks for the target-language equivalent, answer buttons display target-language choices only.

Bilingual answer objects may remain in the data model for reuse and feedback, but the renderer displays only the role required by the question. Use a generic field such as `answerDisplayRole: support|target` rather than hard-coding particular languages.

For grammar questions, display only the forms needed to solve the question. A translation or explanation that reveals the correct choice must not appear in the answer button. Bilingual explanation may be shown after the learner answers.

### 3.2 Random answer order

The correct answer must not occupy a predictable position.

The renderer shuffles answer choices once when each question is presented. Scoring uses the stable answer ID or correct flag, never the visual position. The order remains stable while the learner answers and while feedback is shown. A new attempt may generate a new order.

Tests must prove that:

- the correct identity survives shuffling;
- all answer choices remain present exactly once;
- scoring is independent of visual position;
- the correct answer can appear in positions 1, 2 and 3;
- `answerDisplayRole` still controls the visible language.

Do not manually randomize answer order in JSON.

### 3.3 Required quiz tests

For every production course, test:

- every lesson resolves to its quiz;
- exactly 3 questions per standard lesson;
- question text is non-empty;
- answer choices are non-empty;
- exactly one correct answer;
- IDs and references resolve;
- no bilingual answer leakage;
- display role resolves correctly;
- shuffled scoring remains correct;
- final lesson navigation opens the quiz;
- quiz score and progress persist after restart.

Physical-device QA must include the final lesson in every module because navigation/state defects can appear at module boundaries.

## 4. Reusable UI rules

Keep the lesson screen compact. Support and target panels should have limited padding and vertical whitespace.

Provide two compact TTS buttons side by side, labelled for the two language roles/locales. Do not require scrolling merely to switch TTS language.

Previous/next navigation uses a reusable round arrow component directly adjacent to the lesson content. The final next action routes to the lesson quiz.

Technical persistence messages such as “last position saved” should not clutter the learner UI. Persistence remains active silently.

Grammar is placed after the teaching/audience modules on the home screen.

Version and release date come from one build/configuration source. Do not duplicate hard-coded version strings throughout the UI.

## 5. Content/data separation

Linguistic production JSON is the source of truth. Generated/canonical Android assets are regenerated from production sources and validated for equality.

Practical links/resources should be stored separately from dialogue items. External links open in the device browser. Offline lessons must not depend on network access, and an external browser link does not require adding Android INTERNET permission to the app itself.

Stable IDs are required for progress persistence and cross-version updates.

## 6. Linguistic QA

Dialogue, grammar/reference language and written information may use different registers. Everyday dialogue should sound natural and contemporary without becoming slang. Grammar/reference material can be more explicit and standard.

QA must check semantic equivalence between support and target text, natural target-language usage, duplicated/generated dialogue patterns, incorrect terminology, malformed accents/characters and remnants from another language pair.

Do not mass-rewrite correct linguistic content merely for style.

## 7. Build and test delivery

Each test build should have an explicit version and release date. Build artifacts are copied to a predictable local distribution directory using a name such as:

`dist/Learn-<SUPPORT>-<TARGET>-<version>-test.apk`

The `dist` directory may remain gitignored for normal development. If builds are performed in a remote agent/container, that agent cannot write directly to a developer's local Windows/Dropbox filesystem; the APK must be downloaded, transferred, built locally, or published through a deliberate artifact/release mechanism.

Before declaring a version ready for physical-device testing, require content validation, per-lesson quiz integrity, build success and an actual verified APK artifact.

## 8. Template principle

Future language-pair apps should reuse the engine and replace configuration/content rather than copying language-specific logic. Language names, locales, TTS roles, module titles, answer display roles, content IDs and practical resources should be data/configuration-driven wherever practical.

When a defect is discovered in one app and represents a general engine rule, document it here and add an automated regression test before creating the next language app.
