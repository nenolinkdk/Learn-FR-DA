# Content QA 0.3.2

Targeted linguistic corrections after the 0.3.1 UI pass. Dialogue
register was adjusted only where French was clearly more written than
spoken. Grammar examples were not colloquialised. Danish examples that
were already correct were left unchanged.

Practical transport links are a separate resource collection, not
lesson items.

## Spoken French (dialogue / quiz answers quoting the same line)

| ID | old FR | new FR | old DA | new DA | reason |
|---|---|---|---|---|---|
| item.level-1.shopping-price | Combien coûte-t-elle ? | Combien elle coûte ? | Hvad koster den? | unchanged | Everyday spoken price question; inversion with *coûte-t-elle* is stiff in a shop. |
| answer.level-1.shopping-price.correct | Combien coûte-t-elle ? | Combien elle coûte ? | Hvad koster den? | unchanged | Matches the dialogue. |
| item.level-1.arrival.transport | Comment puis-je aller au centre-ville ? | Comment je peux aller au centre-ville ? | Hvordan kommer jeg ind til centrum? | unchanged | *Puis-je* is literary; spoken tourist French uses *comment je peux*. |
| item.level-1.public-transport.ticket-needed | De quel billet ai-je besoin ? | De quel billet j’ai besoin ? | Hvilken billet skal jeg bruge? | unchanged | *Ai-je* is written; keep the same meaning. Superseded in 0.3.3 by *De quel billet est-ce que j’ai besoin ?* |
| item.level-1.public-transport-which-line | Quelle ligne dois-je prendre ensuite ? | Quelle ligne je dois prendre ensuite ? | Hvilken linje skal jeg tage bagefter? | unchanged | Spoken word order for a practical follow-up. |
| item.level-1.digital-booking-not-load | Il ne s’affiche pas. Que dois-je faire ? | Il ne s’affiche pas. Qu’est-ce que je dois faire ? | Den kommer ikke frem. Hvad skal jeg gøre? | unchanged | *Que dois-je* is formal; *qu’est-ce que je dois* is the usual spoken form. |
| item.level-1.travel-problem-next | Que dois-je faire maintenant ? | Qu’est-ce que je dois faire maintenant ? | Hvad skal jeg gøre nu? | unchanged | Same spoken-question adjustment. |
| answer.level-1.travel-problem-next.correct | Que dois-je faire maintenant ? | Qu’est-ce que je dois faire maintenant ? | Hvad skal jeg gøre nu? | unchanged | Matches the dialogue. |
| item.level-2.bank-problem | Que dois-je faire si je ne reconnais pas un paiement ? | Qu’est-ce que je dois faire si je ne reconnais pas un paiement ? | Hvad skal jeg gøre, hvis jeg ikke kan genkende en betaling? | unchanged | Spoken help question in a practical situation. |
| answer.level-2.bank-recognize.correct | Que dois-je faire si je ne reconnais pas un paiement ? | Qu’est-ce que je dois faire si je ne reconnais pas un paiement ? | Hvad skal jeg gøre, hvis jeg ikke kan genkende en betaling? | unchanged | Matches the dialogue. |

Service questions such as *Avez-vous une table…?*, *Puis-je voir une pièce d’identité ?* and *Où allez-vous ?* were kept: they are conventional, not stiff textbook inversions.

## City hall mismatch

| ID | old FR | new FR | old DA | new DA | reason |
|---|---|---|---|---|---|
| item.level-1.public-transport.city-hall | Je vais à l’Hôtel de Ville. | Je vais à Rådhuspladsen. | Jeg skal til Rådhuspladsen. | unchanged | Danish names the square; French named the town-hall building. Context is a public-transport destination in Copenhagen. |

Note-only: cultural note now states that Rådhuspladsen is the square, not the building.

## Airport transport (note only)

| ID | change | reason |
|---|---|---|
| item.level-1.airport-station.platform | Added cultural note: access to regional/long-distance train platforms at Copenhagen Airport is towards the Metro area; tracks 12 and 13 are to the right when approaching that area. | Existing lesson already asks *Hvilket spor…?*; the fact belongs in practical guidance, not in the spoken Danish line. |

## Transport digital note (note only)

| ID | change | reason |
|---|---|---|
| item.level-1.public-transport-app | Digital note now mentions Rejseplanen, bus QR information, Google Maps for stops/stations, and official sources for tickets/City Pass, with no prices or timetables. | Practical guidance without mixing URLs into dialogue text. |

## Danish *ikke* (notes / quiz wording only)

| ID | change | reason |
|---|---|---|
| item.grammar.word-order.8 | Note now says this is a **main-clause** pattern and is not universal; subordinates put *ikke* before the verb. | Avoid teaching “*ikke* always follows the finite verb”. |
| question.grammar.word-order.2 | Prompt specifies *phrase principale*; explanation contrasts subordinate placement. | Same distinction. |
| item.grammar.subordinate.2 | Note now contrasts `Jeg tror, at toget ikke kommer i dag` with `Jeg tror ikke, at toget kommer i dag`, and states the same subordinate placement for *fordi*, *hvis*, *når* and *som*. | The examples were already correct; the explanation needed the contrast. |

Danish example sentences in the grammar lessons were not rewritten.

## Unchanged on purpose

- 400 linguistic items, 40 lessons, 120 quiz questions, no new item IDs.
- No Portuguese remnants found in production JSON.
- No ticket prices or timetables stored in the app.
