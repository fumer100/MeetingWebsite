package praktikum.uebung;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

//Klasse, um sämtliche Speicher-/Veränderungszugriffe auf die Datenbank zu managen

@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UnusedPrivateField",
                "PMD.BeanMembersShouldSerialize", "PMD.SingularField"})
@Repository
public class UebungsRepository {

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    public UebungsRepository(final DataSource dataSource, final JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    //Speichern der Student <-> Termin Zugehörigkeit
    public void insertStudent(final String benutzername, final Termin termin) {

        jdbcTemplate.update(
                "INSERT INTO `Student_Gehoert_Zu_Termin` (benutzername,termin) VALUES (?,?)",
                benutzername, termin.getTerminId()
        );
    }

    //Speichern der Übung
    public void insertUebung(final Uebung uebung) {
        jdbcTemplate.update(
                "DELETE FROM Uebungen WHERE uebungId=" + uebung.getUebungId()
        );
        jdbcTemplate.update(
                "INSERT INTO `Uebungen` (uebungId, maxAnzahl, minAnzahl,"
                        + "uebungname, startDatum, endDatum, gruppenanmeldung,"
                        + " individualanmeldung) VALUES(?,?,?,?,?,?,?,?)",
                uebung.getUebungId(), uebung.getMaxAnzahl(), uebung.getMinAnzahl(),
                uebung.getUebungname(), uebung.getStartDatum(),
                uebung.getEndDatum(), uebung.isGruppenanmeldung(), uebung.isIndividualanmeldung()

        );

    }

    //Termin <-> Student Zugehörigkeit wird gelöscht (Student nicht mehr im Termin)
    public void deleteTerminStudent(final String benutzername, final Termin termin) {
        jdbcTemplate.update(
                "DELETE FROM Student_Gehoert_Zu_Termin WHERE benutzername = '"
                        + benutzername + "' AND termin = " + termin.getTerminId()
        );
    }

    //wird gebraucht beim Löschen der Übung, um Termin zu finden
    @SuppressWarnings("PMD.AvoidFinalLocalVariable")
    public long findTerminId(final long uebungId) {
        final String sql = "SELECT terminId FROM Termin_Gehoert_Zu_Uebung WHERE uebungId = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{uebungId}, Long.class);
    }

    //lösche Übung (und die Zugehörigkeiten)
    public void deleteUebung(final long uebungId) {
        jdbcTemplate.update("DELETE FROM Student_Gehoert_Zu_Termin WHERE termin IN"
                + "(SELECT terminId FROM Termin_Gehoert_Zu_Uebung WHERE uebungId= " + uebungId + ")");
        final long terminId = findTerminId(uebungId);
        jdbcTemplate.update("DELETE FROM Termin_Gehoert_Zu_Uebung WHERE uebungId= " + uebungId);
        jdbcTemplate.update("DELETE FROM Termine WHERE terminId =" + terminId);
        jdbcTemplate.update("DELETE FROM Uebungen WHERE uebungId = " + uebungId);

    }

    //Gruppe (Sammlung an Studenten in einem Termin) wird gelöscht
    public void deleteGruppe(final Termin termin) {
        jdbcTemplate.update(
                "DELETE FROM Student_Gehoert_Zu_Termin WHERE termin = "
                        + termin.getTerminId()
        );
    }

    //Termin wird abgespeichert + Zugehörigkeit zur Übung
    public void insertTermin(final Uebung uebung, final Termin termin) {
        jdbcTemplate.update(
                "DELETE FROM Termine WHERE terminId=" + termin.getTerminId()
        );
        jdbcTemplate.update(
                "INSERT INTO `Termine` (terminId, tag, datum, uhrzeit,maxMember,minMember,"
                        + "tutor,repoErstellt,terminBelegt,gruppenname)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?)",
                termin.getTerminId(), termin.getTag(), termin.getDatum(), termin.getUhrzeit(),
                termin.getMaxMember(), termin.getMinMember(),
                termin.getTutor(), termin.isRepoErstellt(),
                termin.isTerminBelegt(), termin.getGruppenname()

        );
        jdbcTemplate.update(
                "DELETE FROM Termin_Gehoert_Zu_Uebung WHERE terminId=" + termin.getTerminId()
        );
        jdbcTemplate.update(
                "INSERT INTO `Termin_Gehoert_Zu_Uebung`(terminId,uebungId) Values (?,?)",
                termin.getTerminId(), uebung.getUebungId()
        );
    }

    /* public void editTermin(Termin termin,String gruppenname){
       db.update(
               "UPDATE Termine SET gruppenname='"+gruppenname+"' "+"WHERE terminId="+termin.getTerminId()
       );
        //TODO Updates fehlen noch in der Datenbank
   }*/

}
