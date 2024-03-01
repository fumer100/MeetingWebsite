package praktikum.uebung;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import praktikum.logik.ZeitValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Termin Klasse, stellt den Termin in physikalischer Form dar
//mit der Gruppe an Studenten, Tutor, zu welcher Zeit er läuft, ...

@SuppressWarnings({"MagicNumber", "PMD.LongVariable",
        "PMD.AvoidLiteralsInIfCondition", "PMD.OnlyOneReturn"})
@SuppressFBWarnings("UR_UNINIT_READ")
@Data
@NoArgsConstructor
public class Termin {

    //ID, da mehrere Termine zum selben Zeitpunkt stattfinden koennen (mit verschiedenen Tutoren)
    private Random random = new Random();
    private Long terminId = random.nextLong();
    private String tag;         //mit zwei Buchstaben abgekuerzt
    private String datum;       //Format: DD.MM.
    private String uhrzeit;     //Format: HH:MM
    //um die Gruppen zu managen
    private int maxMember;
    private int minMember;
    private List<String> gruppe = new ArrayList<>(maxMember); //TODO: METHODE ZUR KAPAZITÄTÄNDERUNG
    private String tutor;
    private boolean repoErstellt;
    private boolean terminBelegt;
    private String gruppenname;



    public Termin(final String tag, final String datum, final String uhrzeit, final String tutor) {
        this.tag = tag;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.tutor = tutor;
    }

    //static factory Methode, da Überprüfung der Eingaben vom Benutzer
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public static Termin create(final String tag, final String datum, final String uhrzeit, final String tutor) {
        final boolean tagFormat = ZeitValidator.ueberpruefeTag(tag);
        final boolean datumFormat = ZeitValidator.ueberpruefeDatum(datum);
        final boolean uhrzeitFormat = ZeitValidator.ueberpruefeUhrzeit(uhrzeit);
        if (tagFormat && datumFormat && uhrzeitFormat) {
            return new Termin(tag, datum, uhrzeit, tutor);
        }
        throw new IllegalArgumentException("Ein Eingabeparameter ist nicht legitim.");
    }

}
