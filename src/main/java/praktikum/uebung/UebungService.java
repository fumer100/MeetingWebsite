package praktikum.uebung;

import org.springframework.stereotype.Service;
import praktikum.logik.IndividualAnmeldung;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//Service Klasse zu den Uebungen

@SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.BeanMembersShouldSerialize",
                    "PMD.AvoidDuplicateLiterals", "PMD.MissingStaticMethodInNonInstantiatableClass"})
@Service
public final class UebungService {

    private final Map<Long, Uebung> uebungen = new HashMap<>();

    public List<Uebung> alleUebungen() {
        return new ArrayList<>(uebungen.values());
    }

    private UebungService() {
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    //hier kein Method chain, sondern wir benutzen ein Stream(daher keine Verletzung Law of Demeter)
    public void saveAlleUebungen(final List<Uebung> uebungList) {
        final Map<Long, Uebung> terminMap = uebungList.stream()
                .collect(Collectors.toMap(Uebung::getUebungId, Function.identity()));

        uebungen.putAll(terminMap);
    }

    public void addUebung(final Uebung uebung) {
        uebungen.put(uebung.getUebungId(), uebung);
    }

    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.LocalVariableCouldBeFinal", "PMD.SystemPrintln"})
    public Uebung findByID(final Long uebungId) {
        for (Uebung uebung : uebungen.values()) {
            try {
                if (uebungId.equals(uebung.getUebungId())) {
                    return uebung;
                }
            } catch (IllegalArgumentException exception) {
                System.err.println(exception.toString());
                //TODO logger call
            }
        }
        return null;
    }

    //Hashmaps mit IDs bearbeiten
    @SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.LawOfDemeter", "PMD.OnlyOneReturn"})
    public void deleteUebung(final long uebungId) {
        uebungen.remove(uebungId);
    }

    @SuppressWarnings({"PMD.SystemPrintln", "PMD.LawOfDemeter", "PMD.OnlyOneReturn"})
    public IndividualAnmeldung findIAByID(final Uebung uebung, final long individualId) {
        for (final IndividualAnmeldung indiAnmeldung : uebung.getIndividualAnmeldungen()) {
            try {
                if (indiAnmeldung.getIndividualId().equals(individualId)) {
                    return indiAnmeldung;
                }
            } catch (IllegalArgumentException exception) {
                //TODO logger call
                System.err.println(exception.toString());
            }
        }
        return null;
    }

    //Starting point für die Zuteilung der Organisatoren für die Individualanmeldung (per Knopfdruck)
    public void shuffleIndiAnmeldungen(final Uebung uebung) {
        for (final IndividualAnmeldung indiAnmeldung : uebung.getIndividualAnmeldungen()) {
            indiAnmeldung.studiZuweisung();
        }
    }

    //zufällige Zuweisung der Tutoren
    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter", "PMD.OnlyOneReturn"})
    public void tutorZuweisung(final List<Termin> termineGleich) {
        final List<String> tutoren = new ArrayList<>();
        for (final Termin ter : termineGleich) {
            tutoren.add(ter.getTutor());
            ter.setTutor(null);
        }
        final Random random = new Random();
        for (final Termin ter : termineGleich) {
            final String tutor = tutoren.get(random.nextInt(tutoren.size()));
            ter.setTutor(tutor);
            tutoren.remove(tutor);
        }
    }

}
