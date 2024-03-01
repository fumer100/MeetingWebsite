package praktikum.uebung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Data;
import praktikum.ddd.AggregateRoot;
import praktikum.logik.IndividualAnmeldung;
import praktikum.logik.ZeitValidator;

//bezeichnet die praktische Übung (z.B. Praktische Übung 8)

@SuppressWarnings({"PMD.OnlyOneReturn", "PMD.LongVariable"})
@AggregateRoot
@Data
public class Uebung {

    private Random random = new Random();
    private Long uebungId = random.nextLong();
    private int maxAnzahl;
    private int minAnzahl;
    private List<Termin> termine = new ArrayList<>();
    //wichtig für die IndividualAnmeldungen (siehe auch UebungService)
    private List<IndividualAnmeldung> individualAnmeldungen = new ArrayList<>();
    private String uebungname;
    private String startDatum;
    private String endDatum;

    //booleans für die unterschiedlichen Modi
    private boolean gruppenanmeldung;
    private boolean individualanmeldung;

    public Uebung() {
    }

    public Uebung(final String uebungname, final String startDatum, final String endDatum,
                  final int maxAnzahl, final int minAnzahl) {
        this.uebungname = uebungname;
        this.startDatum = startDatum;
        this.endDatum = endDatum;
        this.maxAnzahl = maxAnzahl;
        this.minAnzahl = minAnzahl;
    }

    //static factory Methode, da Überprüfung der Eingaben vom Benutzer
    public static Uebung create(final String uebungname, final String startDatum,
                                final String endDatum, final int maxAnzahl, final int minAnzahl) {
        final boolean start = ZeitValidator.ueberpruefeDatum(startDatum);
        final boolean ende = ZeitValidator.ueberpruefeDatum(endDatum);
        if (start && ende) {
            return new Uebung(uebungname, startDatum, endDatum, maxAnzahl, minAnzahl);
        }
        throw new IllegalArgumentException("Ein Eingabeparameter ist nicht legitim.");
    }

    //Anmeldungsmodi
    public void checkUebungsArt(final String anmeldeart) {
        if ("gruppenanmeldung".equals(anmeldeart)) {
            this.gruppenanmeldung = true;
            individualanmeldung = false;
        }
        if ("individualanmeldung".equals(anmeldeart)) {
            this.individualanmeldung = true;
            gruppenanmeldung = false;

        }
    }

    //erstellt Individualanmeldung
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.DataflowAnomalyAnalysis",
                        "PMD.LawOfDemeter"})
    public void erstelleIndiAnmeldung() {
        boolean gleich = false;
        for (final Termin ter : termine) {
            for (final IndividualAnmeldung individualAnmeldung : individualAnmeldungen) {
       //existiert Individualanmeldung zu diesem Zeitslot bereits, dann wird er nicht hinzugefügt
                gleich = ter.getDatum().equals(individualAnmeldung.getDatum())
                    && ter.getUhrzeit().equals(individualAnmeldung.getUhrzeit());
                if (gleich) {
                    break;
                }
            }
            if (!gleich) {
                final TerminService terminService = new TerminService();
                final List<Termin> termineZurGleichenZeit
                    = terminService.findeFreieTermineZurGleichenZeit(ter, termine);
                final IndividualAnmeldung individualAnmeldung
                    = new IndividualAnmeldung(termineZurGleichenZeit, minAnzahl,
                    maxAnzahl, ter.getDatum(), ter.getUhrzeit());
                individualAnmeldungen.add(individualAnmeldung);
            }
        }
    }

}
