# Section 8 — Évolution du modèle / ce qui coince

## Règle orpheline tentée : A13 (+ A14)

**Règle.** Bonus-malus : × 1,25 (100 %), × 1,125 (50 %), × 0,95 sans sinistre responsable ; bornes 0,50–3,50. Recalcul **à l’échéance annuelle**, jamais le jour du sinistre.

**Tentative.** `CoefficientBonusMalus.apresSinistreResponsable` et `recalculerAEcheance` codent les facteurs et refusent le mauvais jour (`RecalculRefuse`).

**Ce qui coince.**

| Problème | Détail |
|----------|--------|
| Objet manquant | Pas d’entité **Contrat** dans Assur'Auto : les contrats restent dans **ContratPlus** (hors périmètre). |
| Info au mauvais endroit | Le coefficient « officiel » vit dans ContratPlus (`police`, `avenant`). Notre record n’est qu’une projection locale. |
| Dépendance externe | Sans API ContratPlus, on ne peut ni lire l’échéance réelle ni **persister** le nouveau coefficient. |
| Croisement A14 / A18 | Savoir si un dossier classé sans suite doit être exclu du malus exige un historique contrat que nous n’avons pas. |

**Conclusion.** A13 est partiellement jouable (calcul + garde A14) mais **orpheline pour l’application métier réelle** tant que ContratPlus n’expose pas un port anti-corruption.

## Autres points durs

- **A3 / A17** : état « actif / suspendu » et date de relance → ContratPlus uniquement ; on simule des booléens dans les vérifs.
- **A12 « 3e sinistre »** : le compteur 12 mois devrait agréger tous les dossiers de l’assuré ; sans agrégat Assuré/historique, on passe le compte en paramètre (et **H1** tranche le périmètre de comptage).
- **A10 / PaySûr** : on vérifie le délai sur une date de versement simulée ; on ne code pas les codes `SENT` / `REJECTED_IBAN`.

## Hypothèses retenues (pas de dossier étudiant section 9)

Issues de la **section 12** du sujet Assur'Auto :

- **H1** — Les bris de glace (et sinistres non responsables) **comptent** pour le « 3e sinistre en 12 mois » de A12.
- **H2** — Silence de l’assuré **15 jours** après proposition = **acceptation tacite** ; le délai A10 part de cette date.
