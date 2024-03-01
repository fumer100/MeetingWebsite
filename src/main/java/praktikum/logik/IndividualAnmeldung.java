package praktikum.logik;

import lombok.Data;
import praktikum.uebung.Termin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Klasse, um die Individualanmeldung zu managen

@SuppressWarnings({"PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
    "PMD.AvoidLiteralsInIfCondition", "LineLength"}) //Line Length für die Kommentare
@Data
public class IndividualAnmeldung {

    private Random random = new Random();
    private Long individualId = random.nextLong();
    //Terminliste für die Sammlung an Terminen (hier werden später "termineZurGleichenZeit" (!) hinzugefügt)
    private List<Termin> termine;
    //Sammlung an Studenten für diesen Zeitslot
    private List<String> studenten = new ArrayList<>();
    //minimale Anzahl an Studenten in einer Gruppe
    private int minAnzahl;
    //maximale Anzahl an Studenten für die Verteilung
    private int maxAnzahl;
    private int maxProTermin;
    private String datum;
    private String uhrzeit;

    public IndividualAnmeldung(final List<Termin> termine, final int minAnzahl, final int maxProTermin,
                               final String datum, final String uhrzeit) {
        this.termine = termine;
        this.minAnzahl = minAnzahl;
        this.maxProTermin = maxProTermin;
        this.maxAnzahl = maxProTermin * termine.size();
        this.datum = datum;
        this.uhrzeit = uhrzeit;
    }

    public void addStudierende(final String student) {
        if (studenten.size() < maxAnzahl) {
            studenten.add(student);
        }
    }

    //Algorithmus für die Zuweisung von Studenten auf die Termine (Stichwort: no greedy algorithm)
                                                    //für die Schleifenvariablen
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ShortVariable",
                        "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter"})
    public void studiZuweisung() {
        final int minAnzahlVoll = termine.size() * minAnzahl;   //minimale Anzahl an Studenten für die Verteilung mit vollen Gruppen (minAnzahl erreicht für jeden (!) Termin)
        final int anzahlTermine = termine.size();
        final int anzahlStudi = studenten.size();
        int student = 0;
        int i = 0;
        //wenn kleiner als minAnzahlVoll, dann müssen wir einen anderen Algorithmus finden und erst Gruppen auffüllen, "leere Termine" verfallen dann
        if (studenten.size() < minAnzahlVoll) {
            final int anzahlGruppen = studenten.size() / minAnzahlVoll;
            final int rest = studenten.size() % minAnzahl;
            for (int j = 0; j < anzahlGruppen; j++) {
                for (int k = 0; k < maxAnzahl; k++) {
                    final Termin ter = termine.get(j);
                    ter.getGruppe().add(studenten.get(k * j + k));
                }
            }
            //Rest
            for (int l = 0; l < anzahlGruppen; l++) {
                for (int m = studenten.size() - rest; m < studenten.size(); m++) {
                    final Termin ter = termine.get(l);
                    ter.getGruppe().add(studenten.get(m));
                }
            }
        //wir haben genug Studenten, um pro Termin in der Liste erst einen Studenten hinzuzufügen, dann die Terminvariable zu reseten und wieder bei Termin1 zu starten, usw
        } else if (studenten.size() >= minAnzahlVoll) {
            while (student < anzahlStudi) {
                if (anzahlTermine == 1) {
                    termine.get(0).setGruppe(studenten);
                } else {
                termine.get(i).getGruppe().add(studenten.get(student));
                student++;
                i++;

                    //Reset vom Counter um wieder beim ersten Termin zu starten, bis der student Counter die anzahlStudi erreicht hat (alle hinzugefügt)
                    if (i == anzahlTermine - 1 && student < anzahlStudi) {
                        i = 0;
                    }
                }
            }
        }
    }

}
