TD — Coder votre modèle du domaine
==================================

Ouvrir le projet
----------------
Dans IntelliJ : File > Open, puis choisissez le dossier td-depart (celui qui
contient pom.xml). Le projet n'a aucune dépendance : il compile sans Internet.
Java 17 ou plus.

Ce qu'il contient
-----------------
src/main/java/fr/td/
    Verifications.java   VOS vérifications : c'est le programme à lancer.
    Verificateur.java    L'outil (verifier, attendu, refuse, differents).
                         Vous n'avez pas besoin de le modifier.
    domaine/             VOTRE modèle : vos value objects, vos entités, vos règles.

Où mettre vos classes
---------------------
Toutes vos classes vont dans le package fr.td.domaine, une classe par fichier,
comme dans l'exercice 3. Dans IntelliJ : clic droit sur domaine > New >
Java Class, puis choisissez Class (entité) ou Record (value object).

Exemple sur MédiaTech (l'exercice 3 avait exactement cette forme) :

    fr/td/domaine/
        Isbn.java                 record : value object
        PeriodeEmprunt.java       record : value object
        EmpruntId.java            record : l'identifiant de l'entité
        Emprunt.java              class  : entité (equals sur son identifiant)
        ProlongationRefusee.java  class  : l'exception levée quand une règle refuse

Un fichier par concept du glossaire, avec le même nom. Pas de sous-dossiers
pour l'instant.

Lancer les vérifications
------------------------
Clic droit sur Verifications.java > Run 'Verifications.main()'.
Chaque ligne affiche :
    [ OK      ]  la règle est respectée
    [ ÉCHEC   ]  la règle n'est pas respectée (le message dit pourquoi)
    [ À FAIRE ]  le code lève UnsupportedOperationException : pas encore écrit

Au départ, seuls les trois exemples s'affichent, en OK. Supprimez-les quand
vous avez compris.

Ce qui est attendu (au minimum)
-------------------------------
- 3 value objects : des record qui refusent les valeurs invalides
  (validation dans le constructeur compact).
- 1 entité : une classe dont equals() et hashCode() reposent sur son identifiant.
- 3 règles du sujet, chacune codée dans l'objet auquel vous l'avez rattachée,
  dont au moins une qui limite un nombre (« au plus… », « jamais plus de… »).
- Une vérification par règle, avec le code de la règle (« G4 », « V3 »…).

Trois conseils
--------------
- Les noms viennent du glossaire. Pas de Manager, de Service ou de set...().
- Une règle refusée lève une exception : refuse(...) le vérifie.
- Les systèmes externes ne se codent pas : si votre modèle a besoin d'une
  réponse de l'un d'eux, simulez-la par une simple valeur.
