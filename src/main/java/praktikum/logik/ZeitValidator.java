package praktikum.logik;

//zur Überprüfung von Uhrzeit-/Datumsformaten

@SuppressWarnings({"MagicNumber", "HideUtilityClassConstructor",
    "PMD.UseUtilityClass", "PMD.TooManyMethods", "PMD.OnlyOneReturn", "PMD.LiteralsFirstInComparisons",
    "PMD.PositionLiteralsFirstInComparisons", "PMD.DataflowAnomalyAnalysis", "PMD.SimplifyBooleanReturns",
    "PMD.UseLocaleWithCaseConversions"})
public class ZeitValidator {

    public static boolean ueberpruefeUhrzeit(final String uhrzeit) {
        final boolean format = ueberpruefeUhrzeitFormat(uhrzeit);
        final boolean zeit = ueberpruefeUhrzeitZeit(uhrzeit);

        return format && zeit;
    }

    private static boolean ueberpruefeUhrzeitFormat(final String zeit) {
        final char doppelpunkt = zeit.charAt(2);

        return doppelpunkt == ':' && zeit.length() == 5;
    }

    //getestet
    public static boolean ueberpruefeUhrzeitZeit(final String zeit) {

        final String stunde = zeit.substring(0, 2);
        final int stundeInInt = Integer.parseInt(stunde);

        if (stundeInInt < 0 || stundeInInt > 23) {
            return false;
        }

        final String minute = zeit.substring(3, 5);
        final int minuteInInt = Integer.parseInt(minute);

        if (minuteInInt < 0 || minuteInInt > 59) {
            return false;
        }
        return true;
    }

    //toLowerCase damit alle Moeglichkeiten funktionieren
    //z.B. MO, mo, Mo, etc
    //getestet

    @SuppressWarnings("PMD.LawOfDemeter")   //Lösung mit equals sollte man soweit kennen und ist in Ordnung
    public static boolean ueberpruefeTag(final String tag) {
        final String wochentag = tag.toLowerCase();

        return wochentag.equals("mo") || wochentag.equals("di") || wochentag.equals("mi")
            || wochentag.equals("do") || wochentag.equals("fr");
    }

    public static boolean ueberpruefeDatum(final String datum) {

        if (!ueberpruefeDatumFormat(datum)) {
            return false;
        }


        final int monatAlsZahl = monatAlsInteger(datum);

        if (monatAlsZahl < 01 || monatAlsZahl > 12) {
            return false;
        }


        final int tagAlsZahl = tagAlsInteger(datum);

        //Monate mit 31 Tagen
        if (!monateMitEinUndDreissigTagen(tagAlsZahl, monatAlsZahl)) {
            return false;
        }

        //Februar
        if (monatAlsZahl == 02 && !februarTage(tagAlsZahl)) {
            return false;
        }

        //Monate mit 30 Tagen
        if (!monateMitDreissigTagen(tagAlsZahl, monatAlsZahl)) {
            return false;
        }
        return true;
    }

    private static boolean februarTage(final int tag) {
        return tag >= 01 && tag <= 29;
    }

    private static boolean monateMitEinUndDreissigTagen(final int tag, final int monat) {
        //Monate zwischen Januar und Juli
        if (monat % 2 != 0 && monat < 8) {
            return tag <= 31 && tag >= 01;
        //Monate zwischen August und Dezember
        } else if (monat != 2 && monat % 2 == 0 && monat >= 8) {
            return tag <= 31 && tag >= 01;
        }
        return true;
    }

    private static boolean monateMitDreissigTagen(final int tag, final int monat) {
        //Monate zwischen September und November
        if (monat >= 9 && monat % 2 != 0) {
            return tag <= 30 && tag >= 01;
        //Monate zwischen April und Juni
        } else if (monat < 7 && monat % 2 == 0) {
            return tag <= 30 && tag >= 01;
        }
        return true;
    }

    //getestet
    public static boolean ueberpruefeDatumFormat(final String datum) {
        final char ersterPunkt = datum.charAt(2);
        final char zweiterPunkt = datum.charAt(5);

        return ersterPunkt == '.' && zweiterPunkt == '.';
    }

    /*private static boolean weihnachtsferien(final String startdatum, final String enddatum) {
        if()
    }*/

    /*
    private static int jahrAlsInteger(final String datum) {
        final String jahr = datum.substring(6, 10);
        return Integer.parseInt(jahr);
    }
    */

    //TODO Weihnachtsferien, Uhrzeitformat verbessern (8:8 Uhr moeglich), evtl mehr Tests?,
    // verfuegbare Uhrzeiten z.B. nicht zu spaet/frueh, Jahr fehlt(Februar Schaltjahr),
    // Abfangen von Errors fehlt

    private static int monatAlsInteger(final String datum) {
        final String monat = datum.substring(3, 5);
        return Integer.parseInt(monat);
    }

    private static int tagAlsInteger(final String datum) {
        final String tag = datum.substring(0, 2);
        return Integer.parseInt(tag);
    }

}
