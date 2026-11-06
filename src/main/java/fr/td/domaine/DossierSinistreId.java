package fr.td.domaine;

/** Identifiant d'un dossier sinistre. */
public record DossierSinistreId(String valeur) {

    public DossierSinistreId {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("l'identifiant du dossier sinistre est obligatoire");
        }
    }
}
