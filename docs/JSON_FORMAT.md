# JSON Format Specification

## Status

Draft. The final schema must be reconciled with the existing Learn
Portuguese 2 parser before full linguistic production begins.

## Principle

Keep content independent of Android code and keep the schema generic
enough for future Nenoling language pairs.

## Proposed lesson structure

``` json
{
  "level": "level1",
  "titleFr": "Arrivée au Danemark",
  "titleDa": "Ankomst til Danmark",
  "lessons": [
    {
      "id": "L1-01",
      "titleFr": "À l'arrivée",
      "titleDa": "Ved ankomsten",
      "situationFr": "Vous venez d'arriver au Danemark.",
      "items": [
        {
          "id": "L1-01-01",
          "fr": "Où puis-je acheter un billet ?",
          "da": "Hvor kan jeg købe en billet?",
          "grammarNoteFr": null,
          "cultureNoteFr": null,
          "digitalNoteFr": null,
          "pronunciationNoteFr": null,
          "tags": ["transport", "ticket"]
        }
      ]
    }
  ]
}
```

## Rules

-   Stable IDs must not depend on displayed text.
-   Optional note fields may be null or omitted, depending on the final
    parser.
-   Do not duplicate UI labels inside every linguistic record.
-   Use UTF-8.
-   Keep French and Danish in separate fields.
-   Do not embed HTML unless the UI explicitly requires and safely
    supports it.
-   Tags should be machine-friendly lowercase identifiers where
    possible.

## Planned datasets

-   `level1.json`
-   `level2.json`
-   `children.json`
-   `grammar.json`
-   `quiz.json`

## Next step

Codex should compare this draft with the actual LP2 JSON schema and
parser. Prefer backward-compatible adaptation if the existing schema is
clean; otherwise document the migration before changing it.
