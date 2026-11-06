package fr.td.domaine;

/** Levée quand un gestionnaire ne peut pas se voir attribuer un dossier (A15). */
public class AttributionRefusee extends RuntimeException {

    public AttributionRefusee(String message) {
        super(message);
    }
}
