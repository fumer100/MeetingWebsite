package praktikum.logik;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"all", "PMD"})
public class ZeitValidatorTest {


    //TESTS FUER UHRZEITEN

    @Test
    @DisplayName("Minute und Stunde sind richtig")
    void uhrzeitMinuteUndStundeRichtig(){
        boolean uhrzeit = ZeitValidator.ueberpruefeUhrzeit("21:22");
        assertThat(uhrzeit).isTrue();
    }

    @Test
    @DisplayName("Minute ist ungueltig")
    void uhrzeitMinuteFalsch(){
        boolean uhrzeit = ZeitValidator.ueberpruefeUhrzeit("21:60");
        assertThat(uhrzeit).isFalse();
    }

    @Test
    @DisplayName("Stunde ist ungueltig")
    void uhrzeitStundeFalsch(){
        boolean uhrzeit = ZeitValidator.ueberpruefeUhrzeit("61:20");
        assertThat(uhrzeit).isFalse();
    }

    @Test
    @DisplayName("Uhrzeit falsch")
    void uhrzeitFalsch(){
        boolean zeit = ZeitValidator.ueberpruefeUhrzeit("21.23.");
        assertThat(zeit).isFalse();
    }

    @Test
    @DisplayName("Uhrzeit richtig")
    void uhrzeitRichtig(){
        boolean zeit = ZeitValidator.ueberpruefeUhrzeit("21:23");
        assertThat(zeit).isTrue();
    }



    //TESTS FUER WOCHENTAGE

    @Test
    @DisplayName("Wochentag gueltig und kleingeschrieben")
    void wochentagRichtigKlein(){
        boolean tag = ZeitValidator.ueberpruefeTag("mo");
        assertThat(tag).isTrue();
    }

    @Test
    @DisplayName("Wochentag gueltig und grossgeschrieben")
    void wochentagRichtigGross(){
        boolean tag = ZeitValidator.ueberpruefeTag("MO");
        assertThat(tag).isTrue();
    }

    @Test
    @DisplayName("Wochentag ungueltig")
    void wochentagFalsch(){
        boolean tag = ZeitValidator.ueberpruefeTag("So");
        assertThat(tag).isFalse();
    }

    @Test
    @DisplayName("Wochentag exisitiert nicht")
    void keinWochentag(){
        boolean tag = ZeitValidator.ueberpruefeTag("Ko");
        assertThat(tag).isFalse();
    }


    //Test fuer Datum

    @Test
    @DisplayName("richtiges Datumformat")
    void datumFormatRichtig(){
        boolean tag = ZeitValidator.ueberpruefeDatumFormat("21.23.");
        assertThat(tag).isTrue();
    }

    @Test
    @DisplayName("falsches Datumformat")
    void datumFormatFalsch(){
        boolean tag = ZeitValidator.ueberpruefeDatumFormat("21:23.");
        assertThat(tag).isFalse();
    }

    @Test
    @DisplayName("falsches Datum Monat")
    void datumMonatFalsch(){
        boolean tag = ZeitValidator.ueberpruefeDatum("21.23.");
        assertThat(tag).isFalse();
    }

    @Test
    @DisplayName("falsches Datum Tag")
    void datumTagFalsch(){
        boolean tag = ZeitValidator.ueberpruefeDatum("41.12.");
        assertThat(tag).isFalse();
    }

    @Test
    @DisplayName("richtiges Datum")
    void datumRichtig(){
        boolean tag = ZeitValidator.ueberpruefeDatum("21.12.");
        assertThat(tag).isTrue();
    }
}
