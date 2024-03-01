package praktikum.authentifizierung;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//zum Setzen und Überprüfen der Berechtigungen

@Service
@Data
public class RollenService {

    private final List<String> organisatoren = new ArrayList<>();
    private final List<String> tutoren = new ArrayList<>();

    public void addOrganisator(final String name) {
        organisatoren.add(name);
    }

    public void addTutor(final String name) {
        tutoren.add(name);
    }

    public boolean isTutor(final String name) {
        return tutoren.contains(name);
    }

    public boolean isOrganisator(final String name) {
        return organisatoren.contains(name);
    }

}
