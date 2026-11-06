# Assur'Auto — modèle du domaine (TD)

Projet Maven Java 17 (aucune dépendance) : modèle DDD du domaine Assur'Auto
avec vérifications des règles métier.

## Ouvrir le projet

Dans IntelliJ : **File > Open**, puis choisir ce dossier (celui qui contient
`pom.xml`). Le projet compile sans Internet. Java 17 ou plus.

## Contenu

```
src/main/java/fr/td/
    Verifications.java   programme de vérifications à lancer
    Verificateur.java    outil (verifier, attendu, refuse, differents)
    Scenario.java        scénarios de test
    domaine/             modèle : value objects, entités, règles
```

Voir aussi `README.txt` (consignes du TD) et `NOTES-SECTION8.md`
(évolution du modèle / points qui coincent).

## Lancer les vérifications

Clic droit sur `Verifications.java` > Run `Verifications.main()`.

Chaque ligne affiche :

- `[ OK      ]` la règle est respectée
- `[ ÉCHEC   ]` la règle n'est pas respectée
- `[ À FAIRE ]` code pas encore écrit (`UnsupportedOperationException`)

## Build

```bash
mvn -q compile
```
