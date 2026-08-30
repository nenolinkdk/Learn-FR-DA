# Content QA — 0.4.0 (Level 3)

Date: 2026-08-30

## Scope

New production module `module.level-3` only. Levels 1, 2, Children and Grammar were not rewritten.

Purpose: practical professional Danish for French-speaking users working, freelancing or doing business in Denmark.

## Size

| | Count |
|---|---:|
| Lessons | 10 |
| Communication items | 100 |
| Quiz questions | 30 |
| Core business terms in context | 50/50 |

Sources: `linguistic/production/level3-01-05.json`, `linguistic/production/level3-06-10.json`.

## Checks

- FR/DA pairs are situational and semantically aligned.
- Danish is contemporary professional speech (`jeg sender et tilbud`, `vi har aftalt en pris`, `jeg sender fakturaen`, `betalingen er modtaget`, `inklusive moms` / `eksklusive moms`).
- French dialogue uses a spoken professional register; inversion is not forced in everyday questions (`Vous pouvez m’envoyer un devis ?`, `Le prix comprend la TVA ?`).
- No isolated 50-card vocabulary list; terms appear in contact, offer, negotiation, order, invoice, costs, financing, lease, contract and freelance workflow scenes.
- `moms` is consistently TVA; both `inklusive` and `eksklusive` appear, with `inkl. moms` / `ekskl. moms` explained in notes rather than overused in speech.
- Notes state that accounting, investment and contract language is linguistic only — not professional advice.
- No Portuguese remnants (`textPt`, `pt-PT`, Portuguese lesson IDs).
- All Level 3 items use enabled primary TTS `target` / `da-DK`.
- IDs are ASCII-only (`omsætning` and `løn` appear in text, not in IDs).

## Footer

Visible label and destination are `notaguidedtour.com` / `https://notaguidedtour.com`. Historical 0.3.x documentation may still mention the earlier `.dk` address.

## Remaining linguistic risk

Physical-device TTS quality for æ/ø/å and French accents still needs a device pass. Content tests do not replace a native-speaker review of every Level 3 line.