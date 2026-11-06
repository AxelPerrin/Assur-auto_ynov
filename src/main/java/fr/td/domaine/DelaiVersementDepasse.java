package fr.td.domaine;

/** Levée quand le versement dépasse les 30 jours après l'accord (A10). */
public class DelaiVersementDepasse extends RuntimeException {

    public DelaiVersementDepasse(String message) {
        super(message);
    }
}
