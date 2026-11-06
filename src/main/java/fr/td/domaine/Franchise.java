package fr.td.domaine;

/**
 * Franchise applicable à un sinistre (A5, A6).
 */
public record Franchise(int euros) {

    public Franchise {
        if (euros < 0) {
            throw new IllegalArgumentException("une franchise ne peut pas être négative");
        }
    }

    public Montant enMontant() {
        return new Montant(euros);
    }

    /**
     * A5 — barème ; A6 — pas de franchise si non responsable et tiers identifié.
     */
    public static Franchise pour(Garantie garantie, boolean reparateurAgree,
                                 Responsabilite responsabilite, boolean tiersIdentifie) {
        if (responsabilite != null
                && responsabilite.pourcentage() == 0
                && tiersIdentifie) {
            return new Franchise(0);
        }
        return switch (garantie) {
            case TOUS_RISQUES -> new Franchise(300);
            case VOL -> new Franchise(500);
            case BRIS_DE_GLACE -> new Franchise(reparateurAgree ? 0 : 100);
            case TIERS -> new Franchise(0);
        };
    }
}
