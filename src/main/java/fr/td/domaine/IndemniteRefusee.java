package fr.td.domaine;

/** Levée quand une indemnité proposée viole une règle métier (A8). */
public class IndemniteRefusee extends RuntimeException {

    public IndemniteRefusee(String message) {
        super(message);
    }
}
