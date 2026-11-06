package fr.td.domaine;

/**
 * Somme d'argent en euros (indemnité, franchise, valeur du véhicule, estimation).
 */
public record Montant(int euros) {

    public Montant {
        if (euros < 0) {
            throw new IllegalArgumentException("un montant ne peut pas être négatif");
        }
    }

    public boolean superieurA(Montant autre) {
        return euros > autre.euros;
    }

    public Montant moins(Montant autre) {
        return new Montant(Math.max(0, euros - autre.euros));
    }
}
