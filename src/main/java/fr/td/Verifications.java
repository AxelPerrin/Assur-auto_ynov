package fr.td;

import static fr.td.Verificateur.*;

import fr.td.domaine.CoefficientBonusMalus;
import fr.td.domaine.DossierSinistre;
import fr.td.domaine.DossierSinistreId;
import fr.td.domaine.Franchise;
import fr.td.domaine.Garantie;
import fr.td.domaine.Gestionnaire;
import fr.td.domaine.GestionnaireId;
import fr.td.domaine.Montant;
import fr.td.domaine.Responsabilite;

import java.time.LocalDate;

/**
 * Vérifications des règles Assur'Auto (section 4) et hypothèses H1 / H2.
 */
public class Verifications {

    public static void main(String[] args) {

        titre("Gestionnaire (entité) — A15");
        verifier("A15", "un gestionnaire a au plus 60 dossiers ouverts", () -> {
            Gestionnaire gestionnaire = gestionnaireAvec60DossiersOuverts();
            refuse(() -> gestionnaire.ouvrir(new DossierSinistreId("D-61")));
        });

        titre("DossierSinistre (entité) — A7, A8, A11");
        verifier("A7", "une expertise est obligatoire si les dommages sont estimés à plus de 1 500 €", () -> {
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-GRÊLE"));
            dossier.estimerDommages(new Montant(2000));
            refuse(() -> dossier.proposerIndemnite(new Montant(2000), new Montant(300)));
        });
        verifier("A8", "l'indemnité ne dépasse jamais la valeur du véhicule fixée par l'expert", () -> {
            // Expertis simulé : valeur du véhicule = 3 200 € (situation Clio de M. Lambert)
            DossierSinistre dossier = dossierAvecExpertise(new Montant(3200));
            refuse(() -> dossier.verser(new Montant(5000)));
        });
        verifier("A11", "l'assuré peut contester le montant une seule fois", () -> {
            DossierSinistre dossier = dossierAvecMontantPropose(new Montant(2900));
            dossier.contesterMontant();
            refuse(dossier::contesterMontant);
        });

        titre("Value objects (contraintes A4 / A13)");
        verifier("A4", "la responsabilité n'accepte que 0 %, 50 % ou 100 %", () -> {
            refuse(() -> new Responsabilite(25));
            attendu(50, new Responsabilite(50).pourcentage());
        });
        verifier("A13", "le coefficient de bonus-malus reste entre 0,50 et 3,50", () -> {
            refuse(() -> new CoefficientBonusMalus(3.51));
            refuse(() -> new CoefficientBonusMalus(0.49));
        });

        titre("Bonus — A12, A10, A2, A5/A6, A3, A14, A18");
        verifier("A12", "suspicion de fraude si sinistre moins de 15 jours après souscription", () -> {
            // ContratPlus simulé : souscription le 2, sinistre le 14 (M. Caron)
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-CARON"));
            dossier.evaluerSuspicionFraude(
                    LocalDate.of(2026, 3, 14),
                    LocalDate.of(2026, 3, 2),
                    0);
            refuse(() -> dossier.verser(new Montant(1000)));
        });
        verifier("A12", "suspicion de fraude au 3e sinistre en 12 mois (indemnisation suspendue)", () -> {
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-3"));
            dossier.evaluerSuspicionFraude(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2020, 1, 1),
                    2); // déjà 2 → celui-ci est le 3e
            refuse(() -> dossier.proposerIndemnite(new Montant(500), new Montant(0)));
        });
        verifier("A10", "l'indemnité est versée sous 30 jours après l'accord de l'assuré", () -> {
            DossierSinistre dossier = dossierAvecMontantPropose(new Montant(900));
            dossier.accepterMontant(LocalDate.of(2026, 4, 1));
            refuse(() -> dossier.enregistrerVersement(LocalDate.of(2026, 5, 10)));
        });
        verifier("A2", "une déclaration en retard n'est pas refusée d'office : le retard est noté", () -> {
            // Vol le vendredi 6, déclaration le mercredi 11 (Mme Perrin) → > 2 jours ouvrés
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-PERRIN"));
            dossier.enregistrerDeclaration(
                    LocalDate.of(2026, 3, 6),
                    LocalDate.of(2026, 3, 11),
                    2);
            attendu(true, dossier.declarationEnRetard());
        });
        verifier("A6", "pas de franchise si non responsable et tiers identifié", () -> {
            Franchise f = Franchise.pour(
                    Garantie.TOUS_RISQUES, false,
                    new Responsabilite(0), true);
            attendu(0, f.euros());
        });
        verifier("A5", "franchise 300 € en tous risques si responsable", () -> {
            Franchise f = Franchise.pour(
                    Garantie.TOUS_RISQUES, false,
                    new Responsabilite(100), false);
            attendu(300, f.euros());
        });
        verifier("A3", "sinistre non couvert si contrat inactif (ContratPlus simulé)", () -> {
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-SUSPENDU"));
            refuse(() -> dossier.verifierCouverture(false, true));
        });
        verifier("A14", "le coefficient n'est jamais recalculé le jour du sinistre", () -> {
            CoefficientBonusMalus coef = new CoefficientBonusMalus(1.0);
            LocalDate sinistre = LocalDate.of(2026, 2, 10);
            refuse(() -> coef.recalculerAEcheance(
                    sinistre, sinistre, LocalDate.of(2026, 3, 1), new Responsabilite(100)));
        });
        verifier("A18", "un dossier classé sans suite ne déclenche pas A12", () -> {
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-DOUBLON"));
            dossier.classerSansSuite();
            dossier.evaluerSuspicionFraude(
                    LocalDate.of(2026, 3, 14),
                    LocalDate.of(2026, 3, 2),
                    0);
            attendu(false, dossier.indemnisationSuspendue());
        });

        titre("Hypothèses (section 12 — pas de dossier étudiant section 9)");
        verifier("H1", "les bris de glace comptent pour le 3e sinistre en 12 mois (A12)", () -> {
            // 2 bris de glace déjà comptés → le 3e déclenche la suspension
            DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-VITRE-3"));
            dossier.evaluerSuspicionFraude(
                    LocalDate.of(2026, 9, 1),
                    LocalDate.of(2019, 1, 1),
                    2);
            attendu(true, dossier.indemnisationSuspendue());
            refuse(() -> dossier.verser(new Montant(200)));
        });
        verifier("H2", "silence de l'assuré 15 jours = acceptation tacite (démarre A10)", () -> {
            DossierSinistre dossier = dossierAvecMontantPropose(new Montant(900));
            LocalDate proposition = LocalDate.of(2026, 5, 1);
            dossier.constaterSilenceAssuré(proposition, LocalDate.of(2026, 5, 16));
            attendu(LocalDate.of(2026, 5, 16), dossier.dateAccordAssuré());
            dossier.enregistrerVersement(LocalDate.of(2026, 5, 20)); // OK < 30 j
            refuse(() -> dossier.enregistrerVersement(LocalDate.of(2026, 6, 20))); // > 30 j
        });

        bilan();
    }

    // ---------------------------------------------------------------------
    // Utilitaires
    // ---------------------------------------------------------------------

    static Gestionnaire gestionnaireAvec60DossiersOuverts() {
        Gestionnaire gestionnaire = new Gestionnaire(new GestionnaireId("G-REY"));
        for (int i = 1; i <= Gestionnaire.MAX_DOSSIERS_OUVERTS; i++) {
            gestionnaire.ouvrir(new DossierSinistreId("D-" + i));
        }
        return gestionnaire;
    }

    /** Simule la réponse d'Expertis (valeur du véhicule). */
    static DossierSinistre dossierAvecExpertise(Montant valeurVehicule) {
        DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-LAMBERT"));
        dossier.enregistrerExpertise(valeurVehicule);
        return dossier;
    }

    static DossierSinistre dossierAvecMontantPropose(Montant montant) {
        DossierSinistre dossier = dossierAvecExpertise(new Montant(3200));
        dossier.verser(montant);
        return dossier;
    }
}
