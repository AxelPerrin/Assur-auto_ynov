package fr.td.domaine;

import java.time.LocalDate;

/**
 * Coefficient de bonus-malus du contrat : toujours entre 0,50 et 3,50 (A13).
 * Le stockage officiel reste dans ContratPlus — voir SECTION8.md.
 */
public record CoefficientBonusMalus(double valeur) {

    public CoefficientBonusMalus {
        if (valeur < 0.50 || valeur > 3.50) {
            throw new IllegalArgumentException(
                    "le coefficient de bonus-malus reste entre 0,50 et 3,50");
        }
    }

    /**
     * A13 — facteurs de malus. Ne persiste rien : ContratPlus détient le coefficient réel.
     */
    public CoefficientBonusMalus apresSinistreResponsable(Responsabilite responsabilite) {
        double facteur = switch (responsabilite.pourcentage()) {
            case 100 -> 1.25;
            case 50 -> 1.125;
            default -> 1.0;
        };
        double brut = valeur * facteur;
        double borne = Math.min(3.50, Math.max(0.50, brut));
        return new CoefficientBonusMalus(borne);
    }

    /**
     * A14 — recalcul uniquement à l'échéance annuelle, jamais le jour du sinistre.
     * Tentative de règle « orpheline » : sans entité Contrat (ContratPlus), on ne peut
     * que refuser le mauvais moment ; on ne peut pas appliquer ni stocker le nouveau coefficient.
     */
    public CoefficientBonusMalus recalculerAEcheance(LocalDate jourRecalcul,
                                                     LocalDate jourSinistre,
                                                     LocalDate echeanceAnnuelle,
                                                     Responsabilite responsabilite) {
        if (jourRecalcul.equals(jourSinistre)) {
            throw new RecalculRefuse(
                    "le coefficient est recalculé à l'échéance annuelle, jamais le jour du sinistre");
        }
        if (!jourRecalcul.equals(echeanceAnnuelle)) {
            throw new RecalculRefuse(
                    "le coefficient n'est recalculé qu'à l'échéance annuelle du contrat");
        }
        return apresSinistreResponsable(responsabilite);
    }
}
