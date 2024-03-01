package praktikum.uebung;


import java.io.IOException;
import praktikum.githubapi.RepoerstellungService;

import java.util.List;

//Klasse für die Logik mit Terminen, die nicht in Termin passt

@SuppressWarnings({"MagicNumber", "PMD.ClassNamingConventions", "PMD.LongVariable",
                "PMD.BeanMembersShouldSerialize", "PMD.SignatureDeclareThrowsException"})
public final class TerminLogik {

    private final RepoerstellungService repoerstellungService = new RepoerstellungService();

    public TerminLogik() throws Exception {
    }

    //hinzufügen von Studierenden bei der Gruppenanmeldung
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition"})
    public void addStudierendeGruppe(final String student, final Termin termin) throws IOException {
        if (!termin.isTerminBelegt()) {
            termin.getGruppe().add(student);
        }
        if (termin.getGruppe().size() == termin.getMinMember() && !termin.isRepoErstellt()) {
            repoerstellungService.repoErstellen(termin);
            termin.setRepoErstellt(true);
        }
        if (termin.getGruppe().size() >= termin.getMinMember() && !termin.isTerminBelegt()) {
            for (final String studenten : termin.getGruppe()) {
                repoerstellungService.studentHinzufuegenGruppe(studenten, termin.getGruppenname());
            }
        }
        if (termin.getGruppe().size() == termin.getMaxMember()) {
            termin.setTerminBelegt(true);
        }

    }

    //checkt, ob ein Student bereits angemeldet ist
    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.AvoidBranchingStatementAsLastInLoop", "PMD.LawOfDemeter"})
    public static boolean istAngemeldet(final String student, final Uebung uebung) {
        final List<Termin> termine = uebung.getTermine();
        for (final Termin ter : termine) {
            return ter.getGruppe().contains(student);
        }
        return false;
    }


}
