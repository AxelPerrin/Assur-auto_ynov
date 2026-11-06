package fr.td.domaine;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Gestionnaire sinistres : suit chaque dossier de bout en bout (A15).
 */
public class Gestionnaire {

    public static final int MAX_DOSSIERS_OUVERTS = 60;

    private final GestionnaireId id;
    private final Set<DossierSinistreId> dossiersOuverts = new HashSet<>();

    public Gestionnaire(GestionnaireId id) {
        this.id = Objects.requireNonNull(id);
    }

    public GestionnaireId id() {
        return id;
    }

    public int nombreDossiersOuverts() {
        return dossiersOuverts.size();
    }

    /**
     * A15 — un gestionnaire a au plus 60 dossiers ouverts.
     */
    public void ouvrir(DossierSinistreId dossier) {
        Objects.requireNonNull(dossier);
        if (dossiersOuverts.contains(dossier)) {
            return;
        }
        if (dossiersOuverts.size() >= MAX_DOSSIERS_OUVERTS) {
            throw new AttributionRefusee(
                    "un gestionnaire a au plus " + MAX_DOSSIERS_OUVERTS + " dossiers ouverts");
        }
        dossiersOuverts.add(dossier);
    }

    public void cloturer(DossierSinistreId dossier) {
        dossiersOuverts.remove(dossier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gestionnaire that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
