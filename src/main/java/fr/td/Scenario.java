package fr.td;

import fr.td.domaine.ContestationRefusee;
import fr.td.domaine.DossierSinistre;
import fr.td.domaine.DossierSinistreId;
import fr.td.domaine.Franchise;
import fr.td.domaine.Garantie;
import fr.td.domaine.IndemniteRefusee;
import fr.td.domaine.Montant;
import fr.td.domaine.Responsabilite;

/**
 * Section 10 — situation vécue rejouée : « La Clio de M. Lambert ».
 *
 * Lancer :
 *   mvn -q compile exec:java "-Dexec.mainClass=fr.td.Scenario"
 */
public class Scenario {

    public static void main(String[] args) {
        System.out.println("=== Situation vécue : La Clio de M. Lambert ===");
        System.out.println();

        // ContratPlus simulé : contrat tous risques actif, coefficient hors sujet ici
        System.out.println("1. M. Lambert, seul en cause, percute un poteau avec sa Clio 2011.");
        DossierSinistre dossier = new DossierSinistre(new DossierSinistreId("D-LAMBERT"));
        dossier.verifierCouverture(true, true);
        System.out.println("   -> couverture OK (contrat actif + tous risques, lu dans ContratPlus).");

        dossier.fixerResponsabilite(new Responsabilite(100));
        System.out.println("   -> responsabilite fixee a 100 % (pas de constat amiable utile).");

        System.out.println();
        System.out.println("2. Le garage estime la reparation a 4 800 EUR.");
        dossier.estimerDommages(new Montant(4800));
        if (dossier.expertiseObligatoire()) {
            System.out.println("   -> A7 : dommages > 1 500 EUR -> expertise obligatoire.");
        }

        System.out.println();
        System.out.println("3. Tentative d'indemnisation sans rapport Expertis...");
        try {
            dossier.proposerIndemnite(new Montant(4800), new Montant(300));
            System.out.println("   -> ERREUR DE MODELE : aurait du refuser.");
        } catch (IndemniteRefusee e) {
            System.out.println("   -> refuse : " + e.getMessage());
        }

        System.out.println();
        System.out.println("4. Expertis renvoie : valeur du vehicule = 3 200 EUR (declaree 5 000 a la souscription).");
        dossier.enregistrerExpertise(new Montant(3200));

        Franchise franchise = Franchise.pour(
                Garantie.TOUS_RISQUES, false, new Responsabilite(100), false);
        System.out.println("   -> A5 : franchise tous risques = " + franchise.euros() + " EUR.");

        Montant indemnite = dossier.proposerIndemnite(new Montant(4800), franchise.enMontant());
        System.out.println("   -> A8 : reparation 4 800 > valeur 3 200 -> vehicule economiquement irreparable.");
        System.out.println("   -> indemnite proposee = valeur - franchise = " + indemnite.euros() + " EUR.");

        System.out.println();
        System.out.println("5. Tentative de verser 5 000 EUR (valeur declaree a la souscription)...");
        try {
            dossier.verser(new Montant(5000));
            System.out.println("   -> ERREUR DE MODELE : aurait du refuser.");
        } catch (IndemniteRefusee e) {
            System.out.println("   -> refuse : " + e.getMessage());
        }

        System.out.println();
        System.out.println("6. M. Lambert conteste le montant propose (A11).");
        dossier.contesterMontant();
        System.out.println("   -> contestation enregistree (1/1). Contre-expertise a ses frais (non modelisee).");

        System.out.println();
        System.out.println("7. Seconde contestation...");
        try {
            dossier.contesterMontant();
            System.out.println("   -> ERREUR DE MODELE : aurait du refuser.");
        } catch (ContestationRefusee e) {
            System.out.println("   -> refuse : " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Fin du scénario ===");
        System.out.println("Étape non jouable dans ce modèle : mise à jour du bonus-malus dans ContratPlus (A13/A14).");
    }
}
