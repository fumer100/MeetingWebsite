package praktikum.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import praktikum.authentifizierung.RollenService;
import praktikum.githubapi.RepoerstellungService;
import praktikum.logik.IndividualAnmeldung;
import praktikum.uebung.*;

@SuppressWarnings({"FinalParameters", "PMD.MethodArgumentCouldBeFinal", "PMD.AtLeastOneConstructor",
        "PMD.BeanMembersShouldSerialize", "PMD.LongVariable", "PMD.ConfusingTernary",
        "PMD.UseObjectForClearerAPI", "PMD.TooManyMethods", "PMD.SingularField", "PMD.LawOfDemeter",
        "PMD.DataflowAnomalyAnalysis", "PMD.AvoidCatchingGenericException",
        "PMD.AvoidDuplicateLiterals", "PMD.SignatureDeclareThrowsException"})
@Controller
public class Webpage {


    @Autowired
    private final UebungsRepository uebungsRepository;



    @Autowired
    private final TerminService terminService;

    @Autowired
    private final UebungService uebungService;



    @Autowired
    private RollenService rollenService;

    @Autowired
    private RepoerstellungService repoerstellungService;


    public Webpage(TerminService terminService, UebungService uebungService,
                   UebungsRepository uebungsRepository) {
        this.uebungService = uebungService;
        this.terminService = terminService;
        this.uebungsRepository = uebungsRepository;
    }



    //*************************** CSV Controller ***************************


    //Einlesen der CSV mit Terminen und Tutoren (siehe test.csv, diesen können Sie direkt benutzen ;) )
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "REC_CATCH_EXCEPTION"})
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/upload-csv-file/{uebungId}")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model, @PathVariable long uebungId) {

        final Uebung uebung = uebungService.findByID(uebungId);

        //Validierung
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            //Parsing der csv file
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                //csv bean reader
                final CsvToBean<Termin> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Termin.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                //Liste an Terminen mit csv
                final List<Termin> termine = csvToBean.parse();


                int count = 1;
                uebung.setTermine(new ArrayList<Termin>());                // TODO: Termine ueberpruefen???

                //für jeden Termin, den wir mit der csv eingelesen haben
                for (final Termin ter : termine) {
                    //Tutoren werden gesetzt
                    rollenService.addTutor(ter.getTutor());
                    //TODO geschieht auch bei Gruppenanmeldung
                    //Standard Gruppenname
                    if (uebung.isIndividualanmeldung()) {
                        ter.setGruppenname(uebung.getUebungname() +  " Gruppe" + count + " " + ter.getDatum());
                        count++;
                    }
                    //Termin zur Übung hinzufügen
                    terminService.addTermin(ter, uebung);

                    //Zuweisung der Tutoren auf den Termin
                    //(nur zu den Zeitslot, an den sie auch tatsächlich anwesend sein können)
                    uebungService.tutorZuweisung(
                        terminService.findeFreieTermineZurGleichenZeit(ter, uebung.getTermine()));
                   ter.setMaxMember(uebung.getMaxAnzahl());
                   ter.setMinMember(uebung.getMinAnzahl());
                }

                //TODO geschieht auch bei Gruppenanmeldung
                uebung.erstelleIndiAnmeldung();

                model.addAttribute("termine", termine);
                model.addAttribute("status", true);

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }

        return "redirect:/tutor/termine/" + uebungId;
    }






    //*************************** Standard Controller ***************************


    ////////////////////////////// Uebersicht ////////////////////////////////////////////

    //Uebersicht Seite mit unterschiedlichen Sichten pro Benutzerrolle (siehe uebersicht.html)
    @Secured({"ROLE_STUDENT", "ROLE_ORGA", "ROLE_TUTOR"})
    @GetMapping("/uebersicht")
    public String getUebersicht(Model model, @AuthenticationPrincipal OAuth2User principal) {
        final List<Uebung> uebungen = uebungService.alleUebungen();
        model.addAttribute("uebungen", uebungen);
        final boolean orga = rollenService.isOrganisator(principal.getAttribute("login"));
        model.addAttribute("orga", orga);
        final boolean tutor = rollenService.isTutor(principal.getAttribute("login"));
        model.addAttribute("tutor", tutor);
        return "uebersicht";
    }



    ////////////////////////////// Alles zu den Uebungen ////////////////////////////////////////////


    //Organisator View, erstellte Übungen
    @Secured("ROLE_ORGA")
    @GetMapping("/orga/createuebung")
    public String createUebung1(Model model) {
        final Uebung uebung = new Uebung();
        model.addAttribute("uebung", uebung);
        model.addAttribute("uebungen", uebungService.alleUebungen());
        return "createUebungsForm";
    }


    //Organisator erstellt die Übung (Post)
    @Secured("ROLE_ORGA")
    @PostMapping(value = "/orga/createuebung",
        params = {"uebungname", "startdatum", "enddatum", "anmeldeart", "max", "min"})
    public String createUebung2(String uebungname, String startdatum, String enddatum,
                                String anmeldeart, Model model, int max, int min) {
        final Uebung uebung = Uebung.create(uebungname, startdatum, enddatum, max, min);
        uebung.checkUebungsArt(anmeldeart);
        uebungService.addUebung(uebung);
        uebungsRepository.insertUebung(uebung);
        model.addAttribute("uebung", uebung);
        model.addAttribute("termine", uebung.getTermine());
        model.addAttribute("uebungen", uebungService.alleUebungen());
        return "createUebungsForm";
    }


    //Editieren der Übung durch den Organisator, GET
    @Secured("ROLE_ORGA")
    @GetMapping("/orga/edituebung/{uebungId}")
    public String editUebung(Model model, @PathVariable long uebungId) {
        final Uebung uebung = uebungService.findByID(uebungId);
        model.addAttribute("uebungen", uebungService.alleUebungen());
        model.addAttribute("uebung", uebung);
        return "edituebung";
    }


    //Editieren der Übung durch den Organisator, tatsächliches Editieren (von Name, Datum)
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/edituebung/{uebungId}")
    public String edit(@PathVariable long uebungId, String uebungname,
                       String startdatum, String enddatum) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Uebung neu = uebung;
        neu.setUebungname(uebungname);
        neu.setStartDatum(startdatum);
        neu.setEndDatum(enddatum);
        uebungService.deleteUebung(uebungId);
        uebungService.addUebung(neu);
        return "redirect:/orga/createuebung";
    }


    //Löschen einer Uebung durch den Organisator
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/deleteuebung/{uebungId}")
    public String deleteUebung(@PathVariable long uebungId) {
        uebungService.deleteUebung(uebungId);
        uebungsRepository.deleteUebung(uebungId);
        return "redirect:/orga/createuebung";
    }



    ////////////////////////////// Anmeldungscontroller ////////////////////////////////////////////


    //Individualanmeldung für die Studierende, GET
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @GetMapping("/anmeldungindi/{uebungId}/{individualId}")
    public String indiAnmeldungget(Model model, @PathVariable long individualId, @PathVariable long uebungId) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final IndividualAnmeldung individualAnmeldung = uebungService.findIAByID(uebung, individualId);
        model.addAttribute("uebung", uebung);
        model.addAttribute("individualanmeldung", individualAnmeldung);
        return "anmeldung";
    }


    //Gruppenanmeldung für die Studierende, GET
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @GetMapping("/anmeldunggruppe/{uebungId}/{terminId}")
    public String gruppeAnmeldungget(Model model, @PathVariable long terminId, @PathVariable long uebungId) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        model.addAttribute("uebung", uebung);
        model.addAttribute("termin", termin);
        model.addAttribute("gruppe", termin.getGruppenname());
        return "anmeldung";
    }


    //Gruppenanmeldung für die Studierende, POST, wo erstmal nur der Gruppenname gesetzt wird
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @PostMapping(value = "/anmeldunggruppe/{uebungId}/{terminId}", params = {"gruppenname"})
    public String anmeldungpost(@PathVariable long terminId, @PathVariable long uebungId,
                                Model model, String gruppenname) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        termin.setGruppenname(gruppenname);
        uebungsRepository.insertTermin(uebung, termin); //setzt
        model.addAttribute("uebung", uebung);
        model.addAttribute("termin", termin);
        return "redirect:/anmeldunggruppe/" + uebungId + "/" + terminId;
    }


    //Gruppenanmeldung für die Studierende, POST, hier werden die Studenten hinzugefügt
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @PostMapping(value = "/anmeldunggruppe/{uebungId}/{terminId}", params = {"studierende"})
    public String studierendeAdd(@PathVariable long terminId, @PathVariable long uebungId,
                                 Model model, String studierende)
        throws Exception {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        if (!TerminLogik.istAngemeldet(studierende, uebung)) {
            final TerminLogik logik = new TerminLogik();
            logik.addStudierendeGruppe(studierende, termin);
            uebungsRepository.insertStudent(studierende, termin);
        }
        model.addAttribute("uebung", uebung);
        model.addAttribute("termin", termin);
        model.addAttribute("studierende", termin.getGruppe());

        return "redirect:/anmeldunggruppe/" + uebungId + "/" + terminId;
    }



    ////////////////////////////// Termincontroller ////////////////////////////////////////////


    //zeigt die verfuegbaren Termine an (fuer Studenten), GET, auf der HTML mit Schaltfläche zur Anmeldung
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @GetMapping("/terminwahl/{uebungId}")
    public String test1(@PathVariable long uebungId, Model model) {
        final Uebung uebung = uebungService.findByID(uebungId);
        model.addAttribute("uebung", uebung);
        model.addAttribute("individualanmeldungen", uebung.getIndividualAnmeldungen());
        return "terminwahl";
    }


    //zeigt alle Termine an (für die mehr privilegierten Benutzergruppen)
    @Secured({"ROLE_TUTOR", "ROLE_ORGA"})
    @GetMapping("/tutor/termine/{uebungId}")
    public String termine(@PathVariable long uebungId, Model model) {
        final Uebung uebung = uebungService.findByID(uebungId);
        model.addAttribute("termine", uebung.getTermine());
        model.addAttribute("uebung", uebung);
        return "termine";
    }
    //TODO Tutorzuteilung auch bei Gruppen zufaellig



    /////////////////// Controller für die Gruppenansicht/Editieren ///////////////////////////////


    //Anzeigen der Gruppen (für die mehr privilegierten Benutzergruppen)
    @Secured({"ROLE_TUTOR", "ROLE_ORGA"})
    @GetMapping("/tutor/gruppe/{uebungId}/{terminId}")
    public String gruppeAnzeigen(Model model, @PathVariable long uebungId, @PathVariable long terminId,
                                 @AuthenticationPrincipal OAuth2User principal) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        model.addAttribute("termin", termin);
        model.addAttribute("uebung", uebung);
        model.addAttribute("gruppen", termin.getGruppe());
        final boolean orga = rollenService.isOrganisator(principal.getAttribute("login"));
        model.addAttribute("orga", orga);
        return "gruppe";
    }


    //Editieren der Gruppen, GET (Orga)
    @Secured("ROLE_ORGA")
    @GetMapping("/orga/editgruppe/{uebungId}/{terminId}")
    public String editGruppeGet(Model model, @PathVariable long terminId, @PathVariable long uebungId) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        model.addAttribute("uebung", uebung);
        model.addAttribute("termin", termin);
        model.addAttribute("studierende", termin.getGruppe());
        return "editgruppe";
    }


    //Editieren der Gruppen, tatsächliches Editieren (Orga)
    @Secured("ROLE_ORGA")
    @PostMapping(value = "/orga/editgruppe/{uebungId}/{terminId}", params = {"gruppenname"})
    public String renameGruppe(@PathVariable long uebungId, @PathVariable long terminId, String gruppenname) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        termin.setGruppenname(gruppenname);
        //TODO editgruppe DB
        return "redirect:/tutor/gruppe/" + uebungId + "/" + terminId;
    }


    //Löschen einer Gruppe (Orga)
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/deletegruppe/{uebungId}/{terminId}")
    public String deleteGruppe(@PathVariable long uebungId, @PathVariable long terminId) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        uebungsRepository.deleteGruppe(termin);
        termin.setGruppenname(null);
        termin.setGruppe(new ArrayList<>());
        return "redirect:/terminwahl/" + uebungId;
    }


    //manuelles Hinzufügen von Studierende durch den Organisator
    @Secured("ROLE_ORGA")
    @PostMapping(value = "/orga/addstudierende/{uebungId}/{terminId}", params = {"studierende"})
    public String addStudierende(@PathVariable long uebungId, @PathVariable long terminId,
                                 String studierende) throws Exception {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        if (!TerminLogik.istAngemeldet(studierende, uebung)) {
            final TerminLogik logik = new TerminLogik();
            logik.addStudierendeGruppe(studierende, termin);
        }
        return "redirect:/tutor/gruppe/" + uebungId + "/" + terminId;
    }


    //gleiche wie oben, aber für Individualanmeldung
    @Secured({"ROLE_ORGA", "ROLE_STUDENT"})
    @PostMapping(value = "/addstudierendeindividual/{uebungId}/{individualId}", params = {"studierende"})
    public String addStudierendeIndividual(@PathVariable long uebungId,
                                           @PathVariable long individualId, String studierende) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final IndividualAnmeldung individualAnmeldung = uebungService.findIAByID(uebung, individualId);
        individualAnmeldung.addStudierende(studierende);
        return "redirect:/uebersicht";
    }


    //Studierende aus Termin löschen (Orga)
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/deletestudierende/{uebungId}/{terminId}/{studierende}")
    public String deleteStudierende(@PathVariable long uebungId, @PathVariable long terminId,
                                    @PathVariable String studierende) {
        final Uebung uebung = uebungService.findByID(uebungId);
        final Termin termin = terminService.findByID(uebung, terminId);
        final List<String> neu = termin.getGruppe();
        neu.remove(studierende);
        uebungsRepository.deleteTerminStudent(studierende, termin);
        termin.setGruppe(neu);
        return "redirect:/tutor/gruppe/" + uebungId + "/" + terminId;
    }


    ////////////////////////////// Zuteilung bei Individualanmeldung ////////////////////////////////////////////


    //Zuteilung der Studenten in die Termine bei der Individualanmeldung
    @Secured("ROLE_ORGA")
    @PostMapping("/orga/studizuteilung/{uebungId}")
    public String zuteilungStudi(@PathVariable long uebungId) throws Exception {
        final Uebung uebung = uebungService.findByID(uebungId);
        uebungService.shuffleIndiAnmeldungen(uebung);
        for (final Termin ter : uebung.getTermine()) {
            uebungsRepository.insertTermin(uebung, ter);
            repoerstellungService.repoErstellen(ter);
        }
        for (final Termin termin : uebung.getTermine()) {
            for (final String student : termin.getGruppe()) {
                uebungsRepository.insertStudent(student, termin);

            }

        }

        return "redirect:/uebersicht";
    }






    //*************************** OAuth CONTROLLER ***************************


    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("user",
                principal != null
                    ? principal.getAttribute("login") : null
        );

        return "index";
    }

    @RequestMapping("/user")
    public @ResponseBody
    Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/tokeninfo")
    public @ResponseBody
    Map<String, Object> tokeninfo(@RegisteredOAuth2AuthorizedClient
                                      OAuth2AuthorizedClient authorizedClient) {
        final OAuth2AccessToken gitHubAccessToken = authorizedClient.getAccessToken();
        return Map.of("token", gitHubAccessToken);
    }

}

