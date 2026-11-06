package fr.td.domaine;

/**
 * Part de responsabilité de l'assuré : uniquement 0 %, 50 % ou 100 % (A4).
 */
public record Responsabilite(int pourcentage) {

    public Responsabilite {
        if (pourcentage != 0 && pourcentage != 50 && pourcentage != 100) {
            throw new IllegalArgumentException(
                    "la responsabilité de l'assuré est de 0 %, 50 % ou 100 %");
        }
    }
}
