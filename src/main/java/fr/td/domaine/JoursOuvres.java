package fr.td.domaine;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Jours ouvrés : du lundi au vendredi (hors jours fériés — non gérés ici).
 */
public final class JoursOuvres {

    private JoursOuvres() {
    }

    /** Nombre de jours ouvrés strictement après {@code debut}, jusqu'à {@code fin} inclus. */
    public static int entre(LocalDate debut, LocalDate fin) {
        if (fin.isBefore(debut)) {
            return -1;
        }
        int count = 0;
        LocalDate d = debut.plusDays(1);
        while (!d.isAfter(fin)) {
            DayOfWeek jour = d.getDayOfWeek();
            if (jour != DayOfWeek.SATURDAY && jour != DayOfWeek.SUNDAY) {
                count++;
            }
            d = d.plusDays(1);
        }
        return count;
    }
}
