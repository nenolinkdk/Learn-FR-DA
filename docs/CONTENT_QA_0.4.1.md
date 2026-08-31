# Quiz QA — 0.4.1 (answer display)

Date: 2026-08-30

## Problem

Physical-device testing showed bilingual answer buttons (`target` + `support` on every choice). A translation question therefore printed the solution.

## Fix

Bilingual answer objects are kept. Each question now has generic `answerDisplayRole`: `support` or `target`. `MainActivity` paints only that role. After the answer, the existing bilingual explanation is unchanged.

Role is not hard-coded as French/Danish. Future Nenoling apps reuse the same support/target contract.

## Counts

| | Count |
|---|---:|
| Questions audited | 150 |
| Display roles support | 61 |
| Display roles target | 89 |
| Content text rewritten | 0 |
| Empty quizzes | 0 |
| Modules / lessons / questions | 5 / 50 / 150 |
| Status FIXED | 143 |
| Status NEEDS REVIEW | 7 |

NEEDS REVIEW items are pedagogically valid but worth a human glance (cognate gloss, metalanguage, or comprehension vs “what do you say”).

## Grammar (30/30)

Reviewed individually. Choose-the-correct-Danish-form questions display Danish only — including cases where all French glosses are identical. The one metalanguage item (`question.grammar.word-order.3`) displays French explanations only.

## Screenshot target

`question.level-3.contract-betingelser`

- Prompt may stay bilingual: *Que signifie « betingelser » ?* / *Hvad betyder ‘betingelser’?*
- Buttons: `conditions` / `chiffre d’affaires` / `virement`

## All 150 questions

| Question ID | Module | Lesson | Question type | Question input | Answer display | Correct answer | Status |
|---|---|---|---|---|---|---|---|
| `question.level-1.arrival.exit` | level-1 | Arrivée au Danemark | F. Situational / communication | French quote / support prompt | target (Danish) | Hvor er udgangen? | FIXED |
| `question.level-1.arrival.modal` | level-1 | Arrivée au Danemark | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Du kan tage metroen. | FIXED |
| `question.level-1.arrival.direction` | level-1 | Arrivée au Danemark | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Tout droit, puis à droite. | FIXED |
| `question.level-1.airport-station.when` | level-1 | À l’aéroport et à la gare | F. Situational / communication | French quote / support prompt | target (Danish) | Hvornår kører det? | FIXED |
| `question.level-1.airport-station.platform` | level-1 | À l’aéroport et à la gare | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | spor | FIXED |
| `question.level-1.airport-station.delay` | level-1 | À l’aéroport et à la gare | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Le train est en retard. | FIXED |
| `question.level-1.public-transport.destination` | level-1 | Métro, train et bus | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Où allez-vous ? | FIXED |
| `question.level-1.public-transport-get-off` | level-1 | Métro, train et bus | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | stå af | FIXED |
| `question.level-1.public-transport-right` | level-1 | Métro, train et bus | F. Situational / communication | French quote / support prompt | target (Danish) | Er det den rigtige bus? | FIXED |
| `question.level-1.hotel-reservation` | level-1 | À l’hôtel | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg har en reservation. | FIXED |
| `question.level-1.hotel-wifi` | level-1 | À l’hôtel | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad er koden til wi-fi? | FIXED |
| `question.level-1.hotel-problem` | level-1 | À l’hôtel | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Cela ne fonctionne pas. | FIXED |
| `question.level-1.cafe-order` | level-1 | Au café | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Jeg vil gerne have en kaffe. | FIXED |
| `question.level-1.cafe-cake` | level-1 | Au café | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | une pâtisserie à la cannelle | FIXED |
| `question.level-1.cafe-card` | level-1 | Au café | F. Situational / communication | French quote / support prompt | target (Danish) | Kan jeg betale med kort? | FIXED |
| `question.level-1.restaurant-table` | level-1 | Au restaurant | F. Situational / communication | French quote / support prompt | target (Danish) | Har I et bord til to? | FIXED |
| `question.level-1.restaurant-without` | level-1 | Au restaurant | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | uden | FIXED |
| `question.level-1.restaurant-bill` | level-1 | Au restaurant | F. Situational / communication | French quote / support prompt | target (Danish) | Må vi bede om regningen? | FIXED |
| `question.level-1.shopping-price` | level-1 | Magasins et paiement | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad koster den? | FIXED |
| `question.level-1.shopping-receipt` | level-1 | Magasins et paiement | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | kvittering | FIXED |
| `question.level-1.shopping-size` | level-1 | Magasins et paiement | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | une taille plus grande | FIXED |
| `question.level-1.directions-left` | level-1 | Se repérer et visiter | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | à gauche | FIXED |
| `question.level-1.directions-far` | level-1 | Se repérer et visiter | F. Situational / communication | French quote / support prompt | target (Danish) | Er der langt herfra? | FIXED |
| `question.level-1.directions-walk` | level-1 | Se repérer et visiter | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | à pied | FIXED |
| `question.level-1.digital-booking-perfect` | level-1 | Billets, applications et réservations | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | J’ai réservé deux billets. | FIXED |
| `question.level-1.digital-booking-qr` | level-1 | Billets, applications et réservations | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Ouvrez le code QR. | FIXED |
| `question.level-1.digital-booking-advance` | level-1 | Billets, applications et réservations | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | à l’avance | FIXED |
| `question.level-1.travel-problem-lost` | level-1 | Un problème pendant le voyage | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Jeg har mistet min taske. | FIXED |
| `question.level-1.travel-problem-look` | level-1 | Un problème pendant le voyage | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | l’apparence du sac | FIXED |
| `question.level-1.travel-problem-next` | level-1 | Un problème pendant le voyage | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad skal jeg gøre nu? | FIXED |
| `question.level-2.moving-in-moved` | level-2 | S’installer au Danemark | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg er lige flyttet til Danmark. | FIXED |
| `question.level-2.moving-in-appointment` | level-2 | S’installer au Danemark | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | prendre rendez-vous | FIXED |
| `question.level-2.moving-in-online` | level-2 | S’installer au Danemark | F. Situational / communication | French quote / support prompt | target (Danish) | Kan jeg ordne det online? | FIXED |
| `question.level-2.mitid-login` | level-2 | MitID et courrier numérique | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Je n’arrive pas à me connecter. | FIXED |
| `question.level-2.mitid-security` | level-2 | MitID et courrier numérique | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | ses codes et informations de connexion | NEEDS REVIEW |
| `question.level-2.digital-post-passive` | level-2 | MitID et courrier numérique | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Le courrier est envoyé numériquement. | FIXED |
| `question.level-2.workplace-depends` | level-2 | Premier jour au travail | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Cela dépend de l’équipe. | FIXED |
| `question.level-2.workplace-if` | level-2 | Premier jour au travail | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Hvis jeg ikke forstår noget, må jeg så spørge? | FIXED |
| `question.level-2.workplace-sigtil` | level-2 | Premier jour au travail | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | faites-le savoir | FIXED |
| `question.level-2.healthcare-appointment` | level-2 | Médecin et carte de santé | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg vil gerne bestille tid hos lægen. | FIXED |
| `question.level-2.healthcare-passer` | level-2 | Médecin et carte de santé | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Cela me convient très bien. | FIXED |
| `question.level-2.healthcare-card` | level-2 | Médecin et carte de santé | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | sundhedskort | FIXED |
| `question.level-2.bank-need` | level-2 | Banque, carte et MobilePay | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | J’ai besoin de … | FIXED |
| `question.level-2.bank-transfer` | level-2 | Banque, carte et MobilePay | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | overførsel | FIXED |
| `question.level-2.bank-recognize` | level-2 | Banque, carte et MobilePay | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad skal jeg gøre, hvis jeg ikke kan genkende en betaling? | FIXED |
| `question.level-2.commute-delay` | level-2 | Trajet quotidien et applications de transport | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | le train est en retard | FIXED |
| `question.level-2.commute-valid` | level-2 | Trajet quotidien et applications de transport | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | gælde | FIXED |
| `question.level-2.commute-nok` | level-2 | Trajet quotidien et applications de transport | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | probablement | FIXED |
| `question.level-2.municipality-case` | level-2 | À la commune | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | un dossier/une affaire administrative | FIXED |
| `question.level-2.municipality-found` | level-2 | À la commune | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Je n’ai pas trouvé la réponse. | FIXED |
| `question.level-2.municipality-remember` | level-2 | À la commune | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | N’oubliez pas de vérifier la date limite. | FIXED |
| `question.level-2.housing-duration` | level-2 | Logement et propriétaire | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Jeg har boet her i tre måneder. | FIXED |
| `question.level-2.housing-turnon` | level-2 | Logement et propriétaire | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | allumer | FIXED |
| `question.level-2.housing-causative` | level-2 | Logement et propriétaire | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Nous allons faire venir quelqu’un pour examiner le problème. | FIXED |
| `question.level-2.social-gerne` | level-2 | Collègues, déjeuner et vie sociale | F. Situational / communication | French quote / support prompt | target (Danish) | Ja, gerne. | FIXED |
| `question.level-2.social-lyst` | level-2 | Collègues, déjeuner et vie sociale | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Tu as envie de venir ? | FIXED |
| `question.level-2.social-follow` | level-2 | Collègues, déjeuner et vie sociale | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | suivre/comprendre ce qui se dit | FIXED |
| `question.level-2.everyday-digital-through` | level-2 | Une journée numérique au Danemark | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | le paiement a été traité/accepté | FIXED |
| `question.level-2.everyday-digital-battery` | level-2 | Une journée numérique au Danemark | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | ne plus avoir de batterie/énergie | FIXED |
| `question.level-2.everyday-digital-jo` | level-2 | Une journée numérique au Danemark | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | jo … jo … | FIXED |
| `question.level-3.contact-virksomhed` | level-3 | Prendre contact avec une entreprise | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | entreprise | FIXED |
| `question.level-3.contact-kunde` | level-3 | Prendre contact avec une entreprise | F. Situational / communication | French quote / support prompt | target (Danish) | Er du kunde eller leverandør? | FIXED |
| `question.level-3.contact-meeting` | level-3 | Prendre contact avec une entreprise | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Passer torsdag klokken ti? | FIXED |
| `question.level-3.offer-tilbud` | level-3 | Demander et faire une offre | F. Situational / communication | French quote / support prompt | target (Danish) | Kan du sende mig et tilbud? | FIXED |
| `question.level-3.offer-timepris` | level-3 | Demander et faire une offre | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | tarif horaire | FIXED |
| `question.level-3.offer-prisoverslag` | level-3 | Demander et faire une offre | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | prisoverslag | FIXED |
| `question.level-3.negotiate-budget` | level-3 | Négocier le prix | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Det ligger lidt over vores budget. | FIXED |
| `question.level-3.negotiate-rabat` | level-3 | Négocier le prix | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | remise | FIXED |
| `question.level-3.negotiate-aftalt` | level-3 | Négocier le prix | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Vi har aftalt en pris. | FIXED |
| `question.level-3.order-ordre` | level-3 | Commande et livraison | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | passer une commande | FIXED |
| `question.level-3.order-levere` | level-3 | Commande et livraison | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | levere | FIXED |
| `question.level-3.order-delay` | level-3 | Commande et livraison | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad hvis leveringen bliver forsinket? | FIXED |
| `question.level-3.invoice-moms` | level-3 | Facture et paiement | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Le prix comprend la TVA ? | FIXED |
| `question.level-3.invoice-forfald` | level-3 | Facture et paiement | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | date d’échéance | FIXED |
| `question.level-3.invoice-received` | level-3 | Facture et paiement | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Betalingen er modtaget. | FIXED |
| `question.level-3.accounts-turnover` | level-3 | Comptabilité et coûts | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | chiffre d’affaires | FIXED |
| `question.level-3.accounts-overskud` | level-3 | Comptabilité et coûts | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | overskud / underskud | FIXED |
| `question.level-3.accounts-udgift` | level-3 | Comptabilité et coûts | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | dépense | FIXED |
| `question.level-3.invest-investere` | level-3 | Investir et financer | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | investir | FIXED |
| `question.level-3.invest-finansiere` | level-3 | Investir et financer | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | financer l’achat | FIXED |
| `question.level-3.invest-beslutning` | level-3 | Investir et financer | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Vi træffer beslutningen i næste uge. | FIXED |
| `question.level-3.lease-udleje` | level-3 | Acheter, louer ou prendre en leasing | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | mettre en location | FIXED |
| `question.level-3.lease-leasing` | level-3 | Acheter, louer ou prendre en leasing | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | un loyer mensuel, sans être propriétaire | FIXED |
| `question.level-3.lease-compare` | level-3 | Acheter, louer ou prendre en leasing | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Vi skal sammenligne de tre løsninger. | FIXED |
| `question.level-3.contract-kontrakt` | level-3 | Contrat et conditions | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | contrat | FIXED |
| `question.level-3.contract-betingelser` | level-3 | Contrat et conditions | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | conditions | FIXED |
| `question.level-3.contract-accept` | level-3 | Contrat et conditions | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Jeg bekræfter, at jeg accepterer betingelserne. | FIXED |
| `question.level-3.freelance-flow` | level-3 | Le travail indépendant au quotidien | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | on commence le travail | NEEDS REVIEW |
| `question.level-3.freelance-invoice` | level-3 | Le travail indépendant au quotidien | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | après la livraison | NEEDS REVIEW |
| `question.level-3.freelance-salary` | level-3 | Le travail indépendant au quotidien | C. Choose the correct Danish phrase | French quote / support prompt | target (Danish) | Jeg har ikke en fast løn. Jeg fakturerer mine opgaver. | FIXED |
| `q.children.meet.1` | children | Rencontrer d’autres enfants | F. Situational / communication | French quote / support prompt | target (Danish) | Må jeg være med? | FIXED |
| `q.children.meet.2` | children | Rencontrer d’autres enfants | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | On joue au football. | FIXED |
| `q.children.meet.3` | children | Rencontrer d’autres enfants | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg taler ikke så godt dansk. | FIXED |
| `q.children.school.1` | children | À l’école et au centre de loisirs | F. Situational / communication | French quote / support prompt | target (Danish) | Kan du sige det igen? | FIXED |
| `q.children.school.2` | children | À l’école et au centre de loisirs | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | récréation/pause | NEEDS REVIEW |
| `q.children.school.3` | children | À l’école et au centre de loisirs | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Dans dix minutes. | FIXED |
| `q.children.lost.1` | children | Je me suis perdu | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg er blevet væk. | FIXED |
| `q.children.lost.2` | children | Je me suis perdu | F. Situational / communication | French quote / support prompt | target (Danish) | Kan du hjælpe mig med at ringe til hende? | FIXED |
| `q.children.lost.3` | children | Je me suis perdu | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Reste ici. | FIXED |
| `q.children.shop.1` | children | Au magasin et au café | F. Situational / communication | French quote / support prompt | target (Danish) | Hvad koster den? | FIXED |
| `q.children.shop.2` | children | Au magasin et au café | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg vil gerne have … | FIXED |
| `q.children.shop.3` | children | Au magasin et au café | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Non merci. | FIXED |
| `q.children.library.1` | children | À la bibliothèque | F. Situational / communication | French quote / support prompt | target (Danish) | Hvor er børnebøgerne? | FIXED |
| `q.children.library.2` | children | À la bibliothèque | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | låne | FIXED |
| `q.children.library.3` | children | À la bibliothèque | F. Situational / communication | French quote / support prompt | target (Danish) | Er der wi-fi her? | FIXED |
| `q.children.swimming.1` | children | À la piscine | F. Situational / communication | French quote / support prompt | target (Danish) | Må jeg svømme i det her bassin? | FIXED |
| `q.children.swimming.2` | children | À la piscine | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | profond | FIXED |
| `q.children.swimming.3` | children | À la piscine | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg har brug for hjælp. | FIXED |
| `q.children.transport.1` | children | Bus, train et trajet | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | descendre | FIXED |
| `q.children.transport.2` | children | Bus, train et trajet | F. Situational / communication | French quote / support prompt | target (Danish) | Er det den rigtige bus? | FIXED |
| `q.children.transport.3` | children | Bus, train et trajet | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | en retard | FIXED |
| `q.children.internet.1` | children | Téléphone, jeux et Internet | A. Danish → French vocabulary | Danish quote / target prompt | support (French) | ton mot de passe | NEEDS REVIEW |
| `q.children.internet.2` | children | Téléphone, jeux et Internet | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Le jeu ne fonctionne pas. | FIXED |
| `q.children.internet.3` | children | Téléphone, jeux et Internet | F. Situational / communication | French quote / support prompt | target (Danish) | Jeg giver dem ikke og taler med en voksen. | FIXED |
| `q.children.clothes.1` | children | Vêtements, plage et météo | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Il y a du vent. | FIXED |
| `q.children.clothes.2` | children | Vêtements, plage et météo | B. French → Danish vocabulary | French quote / support prompt | target (Danish) | badetøj | FIXED |
| `q.children.clothes.3` | children | Vêtements, plage et météo | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | à la place | FIXED |
| `q.children.emergency.1` | children | Quand quelque chose ne va pas | F. Situational / communication | French quote / support prompt | target (Danish) | Hjælp mig! | FIXED |
| `q.children.emergency.2` | children | Quand quelque chose ne va pas | D. Meaning of a Danish phrase | Danish quote / target prompt | support (French) | Il a mal à la jambe. | FIXED |
| `q.children.emergency.3` | children | Quand quelque chose ne va pas | F. Situational / communication | French quote / support prompt | target (Danish) | 112 | NEEDS REVIEW |
| `question.grammar.pronouns-present.1` | grammar | Pronoms et présent | E. Danish grammar | French quote / support prompt | target (Danish) | vi | FIXED |
| `question.grammar.pronouns-present.2` | grammar | Pronoms et présent | E. Danish grammar | French quote / support prompt | target (Danish) | Du arbejder her. | FIXED |
| `question.grammar.pronouns-present.3` | grammar | Pronoms et présent | E. Danish grammar | French quote / support prompt | target (Danish) | man | FIXED |
| `question.grammar.nouns.1` | grammar | Noms : en, et et forme définie | E. Danish grammar | French quote / support prompt | target (Danish) | billetten | FIXED |
| `question.grammar.nouns.2` | grammar | Noms : en, et et forme définie | E. Danish grammar | French quote / support prompt | target (Danish) | et hotel | FIXED |
| `question.grammar.nouns.3` | grammar | Noms : en, et et forme définie | E. Danish grammar | French quote / support prompt | target (Danish) | billetterne | FIXED |
| `question.grammar.word-order.1` | grammar | Ordre des mots et règle V2 | E. Danish grammar | French quote / support prompt | target (Danish) | I dag tager jeg toget. | FIXED |
| `question.grammar.word-order.2` | grammar | Ordre des mots et règle V2 | E. Danish grammar | French quote / support prompt | target (Danish) | Jeg arbejder ikke i dag. | FIXED |
| `question.grammar.word-order.3` | grammar | Ordre des mots et règle V2 | E. Danish grammar (meaning) | Danish quote / target prompt | support (French) | Le verbe vient avant le sujet. | NEEDS REVIEW |
| `question.grammar.questions.1` | grammar | Questions | E. Danish grammar | French quote / support prompt | target (Danish) | hvor | FIXED |
| `question.grammar.questions.2` | grammar | Questions | E. Danish grammar | French quote / support prompt | target (Danish) | Har du en billet? | FIXED |
| `question.grammar.questions.3` | grammar | Questions | E. Danish grammar | French quote / support prompt | target (Danish) | hvorfor | FIXED |
| `question.grammar.modals.1` | grammar | Verbes modaux | E. Danish grammar | French quote / support prompt | target (Danish) | må | FIXED |
| `question.grammar.modals.2` | grammar | Verbes modaux | E. Danish grammar | French quote / support prompt | target (Danish) | Jeg kan ikke logge ind. | FIXED |
| `question.grammar.modals.3` | grammar | Verbes modaux | E. Danish grammar | French quote / support prompt | target (Danish) | bør | FIXED |
| `question.grammar.past-perfect.1` | grammar | Passé et parfait | E. Danish grammar | French quote / support prompt | target (Danish) | var | FIXED |
| `question.grammar.past-perfect.2` | grammar | Passé et parfait | E. Danish grammar | French quote / support prompt | target (Danish) | Jeg har allerede betalt. | FIXED |
| `question.grammar.past-perfect.3` | grammar | Passé et parfait | E. Danish grammar | French quote / support prompt | target (Danish) | Jeg har ikke fået endnu. | FIXED |
| `question.grammar.subordinate.1` | grammar | Propositions subordonnées | E. Danish grammar | French quote / support prompt | target (Danish) | Jeg tror, at toget ikke kommer. | FIXED |
| `question.grammar.subordinate.2` | grammar | Propositions subordonnées | E. Danish grammar | French quote / support prompt | target (Danish) | hvis | FIXED |
| `question.grammar.subordinate.3` | grammar | Propositions subordonnées | E. Danish grammar | French quote / support prompt | target (Danish) | om | FIXED |
| `question.grammar.prepositions.1` | grammar | Prépositions et lieux | E. Danish grammar | French quote / support prompt | target (Danish) | i Danmark | FIXED |
| `question.grammar.prepositions.2` | grammar | Prépositions et lieux | E. Danish grammar | French quote / support prompt | target (Danish) | vente på toget | FIXED |
| `question.grammar.prepositions.3` | grammar | Prépositions et lieux | E. Danish grammar | French quote / support prompt | target (Danish) | med tog | FIXED |
| `question.grammar.adjectives.1` | grammar | Adjectifs et comparaison | E. Danish grammar | French quote / support prompt | target (Danish) | et godt hotel | FIXED |
| `question.grammar.adjectives.2` | grammar | Adjectifs et comparaison | E. Danish grammar | French quote / support prompt | target (Danish) | billigere | FIXED |
| `question.grammar.adjectives.3` | grammar | Adjectifs et comparaison | E. Danish grammar | French quote / support prompt | target (Danish) | end | FIXED |
| `question.grammar.everyday-patterns.1` | grammar | Structures utiles au quotidien | E. Danish grammar | French quote / support prompt | target (Danish) | jeg vil gerne | FIXED |
| `question.grammar.everyday-patterns.2` | grammar | Structures utiles au quotidien | E. Danish grammar | French quote / support prompt | target (Danish) | have brug for | FIXED |
| `question.grammar.everyday-patterns.3` | grammar | Structures utiles au quotidien | E. Danish grammar | French quote / support prompt | target (Danish) | jo … jo … | FIXED |
