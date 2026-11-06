package fr.td.domaine;

/** Levée quand l'indemnisation est suspendue (A12 — vol et fraude). */
public class IndemnisationSuspendue extends RuntimeException {

    public IndemnisationSuspendue(String message) {
        super(message);
    }
}
