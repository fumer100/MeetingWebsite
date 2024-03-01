package praktikum.githubapi;

import lombok.Data;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;
import praktikum.uebung.Termin;

import java.io.IOException;

//dient dem Erstellen von Github Repos

@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.UseUnderscoresInNumericLiterals"})
@Data
@Service
public class RepoerstellungService {

    private GHRepository repo;

    private static final String APPID = "103817";
    private static final long INSTALLATION = 15191269;

    private final String jwt = JwtHelper.createJWT("key.der", APPID, 300000);
    private final GitHub preAuth = new GitHubBuilder().withJwtToken(jwt).build();

    private final GHAppInstallation appInstallation = preAuth.getApp().getInstallationById(INSTALLATION);
    private final GHAppInstallationToken token = appInstallation.createToken().create();

    private final GitHub gitHub = new GitHubBuilder().withAppInstallationToken(token.getToken()).build();

    public RepoerstellungService() throws Exception {
    }

    //Erstellen eines Github Repos, Schleife zum Hinzufügen der Studenten nach studiZuteilung (Individualanmeldung)
    @SuppressWarnings("PMD.LawOfDemeter")
    public void repoErstellen(final Termin termin) throws IOException {
        final GHOrganization organization = gitHub.getOrganization("meister-propra-apitest");
        repo = organization.createRepository(termin.getGruppenname()).private_(true).create();
        termin.setRepoErstellt(true);
        for (final String student : termin.getGruppe()) {
            final GHUser user = gitHub.getUser(student);
            repo.addCollaborators(user);
        }
    }

    //Einzelnes Hinzufügen eines Studenten in die Repo (für die Gruppenanmeldung)
    @SuppressWarnings("PMD.LawOfDemeter")
    public void studentHinzufuegenGruppe(final String username, final String gruppenname) throws IOException {
        final GHOrganization orga = gitHub.getOrganization("meister-propra-apitest");
        final GHRepository repository = orga.getRepository(gruppenname);
        final GHUser user = gitHub.getUser(username);
        repository.addCollaborators(user);
    }
}
