package praktikum.uebung;


import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

//Service Klasse zu Termin

//hier kein Method chain, sondern wir benutzen ein Stream(daher keine Verletzung Law of Demeter)
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.BeanMembersShouldSerialize", "PMD.LawOfDemeter"})
@Service
@Data
public class TerminService {

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private int count = 1;

    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.LocalVariableCouldBeFinal", "PMD.SystemPrintln"})
    public Termin findByID(final Uebung ubueng, final long terminId) {
        for (Termin ter : ubueng.getTermine()) {
            try {
                if (ter.getTerminId().equals(terminId)) {
                    return ter;
                }
            } catch (IllegalArgumentException exception) {
                //TODO logger call
                System.err.println(exception.toString());
            }
        }
        return null;
    }

    public void addTermin(final Termin termin, final Uebung uebung) {
        uebung.getTermine().add(termin);
    }

    //alle Termine, die in einem Zeitslot laufen (z.B. Mittwoch 24.03.2021, 12:30)
    public List<Termin> findeFreieTermineZurGleichenZeit(final Termin termin, final List<Termin> terminList) {
        final List<Termin> termineGleich = new ArrayList<>();

        for (final Termin ter : terminList) {
            if (termin.getDatum().equals(ter.getDatum())
                    && termin.getUhrzeit().equals(ter.getUhrzeit())) {
                termineGleich.add(ter);
            }
        }
        return termineGleich;
    }



}
