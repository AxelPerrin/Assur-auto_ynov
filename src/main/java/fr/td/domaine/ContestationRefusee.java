package fr.td.domaine;

/** Levée quand l'assuré ne peut plus contester le montant (A11). */
public class ContestationRefusee extends RuntimeException {

    public ContestationRefusee(String message) {
        super(message);
    }
}
