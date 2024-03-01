package praktikum.logik;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.uebung.Termin;
import praktikum.uebung.TerminService;
import praktikum.uebung.Uebung;

@SuppressWarnings({"all", "PMD"})
public class UebungTest {

    @Test
    @DisplayName("Uebung nicht leer")
    void gueltigeUebung() {
        Uebung uebung = Uebung.create("PA-1", "10.12.", "17.12.", 5, 3);
        assertThat(uebung).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Exception, da ungueltig")
    void ungueltigeUebung() throws Exception {
        boolean thrown = false;

        try {
            Uebung uebung = Uebung.create("PA-1", "41.12.", "34.01.", 2, 1);
            Termin termin = Termin.create("MO", "12.02.", "12:45", "testtutor");
            TerminService service = new TerminService();

            service.addTermin(termin, uebung);

        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertThat(thrown).isTrue();
    }

    @Test
    void addTermin() {
        Uebung uebung = Uebung.create("abc", "12.02.", "17.02.", 2 , 1);
        Termin termin = Termin.create("MO", "12.02.", "12:45", "testtutor2");
        TerminService service = new TerminService();
        List<Termin> termine = uebung.getTermine();

        service.addTermin(termin, uebung);

        assertThat(termine.size()).isEqualTo(1);

    }
}
