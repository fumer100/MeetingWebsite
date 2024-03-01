package praktikum;


import com.tngtech.archunit.junit.ArchTest;
import java.util.Map;
import org.springframework.boot.test.context.SpringBootTest;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;


import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import praktikum.ddd.AggregateRoot;

	@SuppressWarnings({"all", "PMD"})
	@SpringBootTest
	@AnalyzeClasses(packagesOf = WebappApplication.class)
	class WebappApplicationTests {


        /*               läuft ohne Datenbankanbindung nicht durch!

	     @Test
	        void contextLoads() {
	     }

        */


		// 	*****************************	Arch Unit Tests	*****************************


        //AggregateRoot Test
        @ArchTest
        final ArchRule uebungRoot = classes()
            .that().resideInAPackage("uebung")
            .and().areNotAnnotatedWith(AggregateRoot.class)
            .should().onlyHaveDependentClassesThat().resideInAnyPackage("uebung")
            .because("Nur über den Aggregate Root sollten Transaktionen nach außen geschehen");


		//Accessibility Checks

		@ArchTest
		final ArchRule logikAccess = classes()
				.that().resideInAnyPackage("logik")
				.should().onlyBeAccessed()
				.byAnyPackage("uebung", "controller")
				.because("Logik Klassen sollten nur im Package controller/uebung benutzt werden");

		@ArchTest
		final ArchRule githubApiAccess = classes()
				.that().resideInAnyPackage("githubapi")
				.should().onlyBeAccessed()
				.byAnyPackage("uebung", "controller")
				.because("Github API Klassen sollten nur im Package controller/uebung benutzt werden");

		@ArchTest
		final ArchRule authentifizierungAccess = classes()
				.that().resideInAnyPackage("authentifizierung")
				.should().onlyBeAccessed()
				.byAnyPackage("controller")
				.because("Authentifizierung Klassen sollten nur im Package controller benutzt werden");


		// Name Checks...
		@ArchTest
		final ArchRule serviceClassesShouldHaveService = classes()
				.that().areAnnotatedWith(Service.class).or().areMetaAnnotatedWith(Service.class)
				.should().haveSimpleNameContaining("Service")
				.because("Service Klassen nicht richtig annotiert.");

		//...
		@ArchTest
		final ArchRule repositoriesShouldEndWithRepository = classes()
			.that().areAnnotatedWith(Repository.class).or().areMetaAnnotatedWith(Repository.class)
			.should().haveSimpleNameContaining("Repository")
			.because("Repositories nicht richtig annotiert.");



		//Package Tests

		/*

		@ArchTest
		final ArchRule uebungPackage = classes()
				.that().haveSimpleNameStartingWith("Termin")
				.or()
				.haveSimpleNameStartingWith("Uebung")
				.should().resideInAPackage("uebung")
				.because("Gehört zum Aggregat Uebung");

		*/


		//Mapping Annotationen...
		@ArchTest
		final ArchRule getMapping = methods()
			.that().areAnnotatedWith(GetMapping.class)
			.should().haveRawReturnType(String.class)
			.orShould().haveRawReturnType(ModelAndView.class)
			.orShould().haveRawReturnType(Map.class)	          //Oauth Controller
			.because("GetMapping nicht richtig annotiert.");

		//...
		@ArchTest
		final ArchRule postMapping = methods()
			.that().areAnnotatedWith(PostMapping.class)
			.should().haveRawReturnType(String.class)
			.orShould().haveRawReturnType(ModelAndView.class)
			.orShould().haveRawReturnType(Map.class)	          //Oauth Controller
			.because("PostMapping nicht richtig annotiert.");


		// 	*****************************	Web MVC Tests	*****************************

		// TODO Web MVC Tests


		// 	*****************************	Integration Tests  *****************************

		// TODO Integration Tests


        // 	*****************************	Security Tests  *****************************

        // TODO Security Tests


}
