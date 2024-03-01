package praktikum.logik;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.uebung.Termin;
import praktikum.uebung.TerminLogik;

//TODO entfernen bei Testklassen
@SuppressWarnings({"all", "PMD"})
public class TerminTest {

    @Test
    void erstelleTermin() {
        Termin termin = Termin.create("Mo", "22.02.", "08:30", "xy");
        assertThat(termin.getTag()).isEqualTo("Mo");
    }

    @Test
    @DisplayName("soll keinen leeren Termin zurueckgeben")
    void erstelleGueltigenTermin() {
        Termin termin = Termin.create("Mo", "22.02.", "08:30", "xy");
        assertThat(termin).isNotEqualTo(new Termin());
    }

    @Test
    @DisplayName("gibt Exception zurueck, da Wochentag ungueltig")
    public void erstelleTerminMitTagUngueltig() {
        boolean thrown = false;

        try {
            Termin termin = Termin.create("Ko", "22.02.", "08:30", "xy");
            String student = "abc";
            TerminLogik logik = new TerminLogik();
            logik.addStudierendeGruppe(student, termin);
        } catch (IllegalArgumentException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(thrown).isTrue();
    }

}
