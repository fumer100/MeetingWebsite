package praktikum.authentifizierung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//IST EINE VORGABE UND SOLLTE SO BLEIBEN

@SuppressWarnings({"all", "PMD"})
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    RollenService rollenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a
                .antMatchers("/**", "/error", "/orga/**")
                .hasRole("ORGA")
                .antMatchers( "/error", "/tutor/**")
                .hasAnyRole("ORGA, TUTOR")
                .antMatchers("/error", "/**")
                .hasAnyRole("ORGA, TUTOR, STUDENT")
                .anyRequest().authenticated())
                .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                .oauth2Login()
                .userInfoEndpoint()
                .userService(initOAuth2UserService());;
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> initOAuth2UserService(){
        OAuth2UserService<OAuth2UserRequest,OAuth2User> oAuth2UserService=new DefaultOAuth2UserService();

        return oAuth2UserRequest -> {
            OAuth2User oAuth2User=oAuth2UserService.loadUser(oAuth2UserRequest);

            Map<String,Object> attributes=oAuth2User.getAttributes(); //keep existing attributes

            String attributeNameKey=oAuth2UserRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            Set<GrantedAuthority> authorities=new HashSet<>();

            // Standard USER Role hinzufügen
            authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

            //Read lists from some config. hardcoded only for example purposes.
            rollenService.addOrganisator("bendisposto-propra");
            rollenService.addOrganisator("bendisposto");
            rollenService.addOrganisator("RutenkolkC");
            rollenService.addOrganisator("seapa100");
            rollenService.addOrganisator("plvoy");
            rollenService.addOrganisator("Mike14062");
            rollenService.addOrganisator("fumer100");

            // Prüfen auf Rollen
            if(rollenService.isOrganisator(attributes.get("login").toString())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ORGA"));
            }

            if(rollenService.isTutor(attributes.get("login").toString())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
            }

            return new DefaultOAuth2User(authorities,attributes,attributeNameKey);
        };
    }

}

