package praktikum;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SuppressWarnings({"all", "PMD"})
@ComponentScan
@SpringBootApplication
public class WebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args); }

    //Github API
    @Bean
    CommandLineRunner init() {
        return args -> {

            System.out.println("Running");

            System.out.println("Done.");
        };
    }

}

