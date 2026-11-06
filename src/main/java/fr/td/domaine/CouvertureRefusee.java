package fr.td.domaine;

/** Levée quand le sinistre n'est pas couvert (A3 — contrat / garantie). */
public class CouvertureRefusee extends RuntimeException {

    public CouvertureRefusee(String message) {
        super(message);
    }
}
