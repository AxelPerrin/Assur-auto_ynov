package fr.td.domaine;

/** Identifiant d'un gestionnaire sinistres. */
public record GestionnaireId(String valeur) {

    public GestionnaireId {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("l'identifiant du gestionnaire est obligatoire");
        }
    }
}
