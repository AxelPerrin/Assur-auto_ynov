package fr.td.domaine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Dossier ouvert pour un sinistre : instruction, indemnisation, contestation.
 */
public class DossierSinistre {

    /** H2 — silence de l'assuré au-delà de ce délai = acceptation tacite. */
    public static final int JOURS_SILENCE_ACCEPTATION_TACITE = 15;

    private final DossierSinistreId id;
    private Responsabilite responsabilite;
    private Montant dommagesEstimes;
    /** Valeur du véhicule renvoyée par Expertis (simulée). */
    private Montant valeurVehiculeFixeeParExpert;
    private Montant montantPropose;
    private int contestations;
    private boolean indemnisationSuspendue;
    private boolean classeSansSuite;
    private boolean declarationEnRetard;
    private LocalDate dateAccordAssuré;
    private boolean couvertureConfirmee;

    public DossierSinistre(DossierSinistreId id) {
        this.id = Objects.requireNonNull(id);
    }

    public DossierSinistreId id() {
        return id;
    }

    public Responsabilite responsabilite() {
        return responsabilite;
    }

    public Montant montantPropose() {
        return montantPropose;
    }

    public int contestations() {
        return contestations;
    }

    public boolean indemnisationSuspendue() {
        return indemnisationSuspendue;
    }

    public boolean declarationEnRetard() {
        return declarationEnRetard;
    }

    public boolean classeSansSuite() {
        return classeSansSuite;
    }

    public LocalDate dateAccordAssuré() {
        return dateAccordAssuré;
    }

    /** A4 — responsabilité fixée à 0 %, 50 % ou 100 %. */
    public void fixerResponsabilite(Responsabilite responsabilite) {
        this.responsabilite = Objects.requireNonNull(responsabilite);
    }

    /**
     * A3 — couverture selon l'état contrat / garanties lus dans ContratPlus (simulés).
     */
    public void verifierCouverture(boolean contratActifALaDateDuSinistre, boolean garantieSouscrite) {
        if (!contratActifALaDateDuSinistre || !garantieSouscrite) {
            throw new CouvertureRefusee(
                    "un sinistre n'est couvert que si le contrat était actif et la garantie souscrite");
        }
        this.couvertureConfirmee = true;
    }

    /**
     * A2 — déclaration hors délai : le retard est noté, pas de refus d'office.
     *
     * @param delaiMaxJoursOuvres 5 jours (cas général) ou 2 jours (vol)
     */
    public void enregistrerDeclaration(LocalDate dateSinistre, LocalDate dateDeclaration,
                                       int delaiMaxJoursOuvres) {
        int ecoule = JoursOuvres.entre(dateSinistre, dateDeclaration);
        this.declarationEnRetard = ecoule > delaiMaxJoursOuvres;
    }

    /**
     * A12 — suspicion de fraude : &lt; 15 jours après souscription, ou 3e sinistre en 12 mois.
     * H1 — les bris de glace et sinistres non responsables comptent dans le total
     *      (hypothèse faute de dossier section 9 étudiant ; point 4 section 12).
     *
     * @param sinistresComptabilisesSur12Mois nombre déjà comptés avant ce dossier (H1 / A18)
     */
    public void evaluerSuspicionFraude(LocalDate dateSinistre, LocalDate dateSouscription,
                                       int sinistresComptabilisesSur12Mois) {
        if (classeSansSuite) {
            return;
        }
        long joursDepuisSouscription = ChronoUnit.DAYS.between(dateSouscription, dateSinistre);
        boolean tropTot = joursDepuisSouscription < 15;
        boolean troisieme = sinistresComptabilisesSur12Mois >= 2;
        if (tropTot || troisieme) {
            this.indemnisationSuspendue = true;
        }
    }

    /** A18 — classé sans suite : ne compte ni pour le bonus-malus ni pour A12. */
    public void classerSansSuite() {
        this.classeSansSuite = true;
        this.indemnisationSuspendue = false;
    }

    /**
     * A7 — une expertise est obligatoire si les dommages sont estimés à plus de 1 500 €.
     */
    public void estimerDommages(Montant estimation) {
        this.dommagesEstimes = Objects.requireNonNull(estimation);
    }

    public boolean expertiseObligatoire() {
        return dommagesEstimes != null && dommagesEstimes.superieurA(new Montant(1500));
    }

    /** Enregistre la réponse d'Expertis (valeur du véhicule), sans coder Expertis. */
    public void enregistrerExpertise(Montant valeurVehicule) {
        this.valeurVehiculeFixeeParExpert = Objects.requireNonNull(valeurVehicule);
    }

    /**
     * A8 — l'indemnité ne dépasse jamais la valeur du véhicule fixée par l'expert.
     */
    public Montant proposerIndemnite(Montant coutReparation, Montant franchise) {
        Objects.requireNonNull(coutReparation);
        Objects.requireNonNull(franchise);
        garantirIndemnisable();
        if (expertiseObligatoire() && valeurVehiculeFixeeParExpert == null) {
            throw new IndemniteRefusee(
                    "une expertise est obligatoire si les dommages sont estimés à plus de 1 500 €");
        }
        if (valeurVehiculeFixeeParExpert == null) {
            throw new IndemniteRefusee(
                    "impossible de proposer une indemnité sans valeur du véhicule fixée par l'expert");
        }

        Montant base = coutReparation.superieurA(valeurVehiculeFixeeParExpert)
                ? valeurVehiculeFixeeParExpert
                : coutReparation;
        Montant indemnite = base.moins(franchise);

        if (indemnite.superieurA(valeurVehiculeFixeeParExpert)) {
            throw new IndemniteRefusee(
                    "l'indemnité ne dépasse jamais la valeur du véhicule fixée par l'expert");
        }

        this.montantPropose = indemnite;
        return indemnite;
    }

    /**
     * Tente de verser un montant libre — refuse si supérieur à la valeur expert (A8).
     */
    public void verser(Montant indemnite) {
        Objects.requireNonNull(indemnite);
        garantirIndemnisable();
        if (valeurVehiculeFixeeParExpert == null) {
            throw new IndemniteRefusee("pas de valeur du véhicule fixée par l'expert");
        }
        if (indemnite.superieurA(valeurVehiculeFixeeParExpert)) {
            throw new IndemniteRefusee(
                    "l'indemnité ne dépasse jamais la valeur du véhicule fixée par l'expert");
        }
        this.montantPropose = indemnite;
    }

    /** A11 — l'assuré peut contester le montant une seule fois. */
    public void contesterMontant() {
        if (montantPropose == null) {
            throw new ContestationRefusee("aucun montant proposé à contester");
        }
        if (contestations >= 1) {
            throw new ContestationRefusee(
                    "l'assuré peut contester le montant une seule fois");
        }
        contestations++;
    }

    /** Acceptation explicite du montant par l'assuré → démarre le délai A10. */
    public void accepterMontant(LocalDate dateAccord) {
        if (montantPropose == null) {
            throw new IllegalStateException("aucun montant proposé à accepter");
        }
        this.dateAccordAssuré = Objects.requireNonNull(dateAccord);
    }

    /**
     * H2 — si l'assuré ne répond pas sous 15 jours, acceptation tacite
     * (hypothèse section 12 point 2, faute de dossier étudiant section 9).
     */
    public void constaterSilenceAssuré(LocalDate dateProposition, LocalDate aujourdhui) {
        if (montantPropose == null) {
            throw new IllegalStateException("aucun montant proposé");
        }
        if (dateAccordAssuré != null) {
            return;
        }
        long jours = ChronoUnit.DAYS.between(dateProposition, aujourdhui);
        if (jours >= JOURS_SILENCE_ACCEPTATION_TACITE) {
            this.dateAccordAssuré = dateProposition.plusDays(JOURS_SILENCE_ACCEPTATION_TACITE);
        }
    }

    /**
     * A10 — l'indemnité est versée sous 30 jours après l'accord de l'assuré.
     * PaySûr n'est pas codé : on enregistre seulement la date de versement simulée.
     */
    public void enregistrerVersement(LocalDate dateVersement) {
        if (dateAccordAssuré == null) {
            throw new IllegalStateException("pas d'accord de l'assuré (ni tacite) sur le montant");
        }
        long jours = ChronoUnit.DAYS.between(dateAccordAssuré, dateVersement);
        if (jours > 30) {
            throw new DelaiVersementDepasse(
                    "l'indemnité est versée sous 30 jours après l'accord de l'assuré");
        }
    }

    private void garantirIndemnisable() {
        if (indemnisationSuspendue) {
            throw new IndemnisationSuspendue(
                    "suspicion de fraude : indemnisation suspendue (équipe vol et fraude)");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierSinistre that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
