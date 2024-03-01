package praktikum.authentifizierung;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)

//IST EINE VORGABE UND SOLLTE SO BLEIBEN

@SuppressWarnings("PMD.AtLeastOneConstructor") //meckert auch mit Konstruktor
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

}
