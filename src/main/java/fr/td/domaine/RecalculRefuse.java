package fr.td.domaine;

/** Levée quand on tente de recalculer le bonus-malus hors échéance (A14). */
public class RecalculRefuse extends RuntimeException {

    public RecalculRefuse(String message) {
        super(message);
    }
}
