package br.com.fiap.restaurante.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Garante a regra de dependência da Clean Architecture: o core não conhece a infra nem frameworks.
 */
class ArchitectureTest {

    private static final String BASE = "br.com.fiap.restaurante.";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("br.com.fiap.restaurante");

    @Test
    void coreShouldNotDependOnInfra() {
        noClasses().that().resideInAPackage(BASE + "core..")
                .should().dependOnClassesThat().resideInAPackage(BASE + "infra..")
                .check(CLASSES);
    }

    @Test
    void coreShouldDependOnlyOnJdkAndItself() {
        classes().that().resideInAPackage(BASE + "core..")
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "lombok..", BASE + "core..")
                .check(CLASSES);
    }

    @Test
    void webLayerShouldNotAccessDatabaseLayer() {
        noClasses().that().resideInAPackage(BASE + "infra.web..")
                .should().dependOnClassesThat().resideInAPackage(BASE + "infra.database..")
                .check(CLASSES);
    }

    @Test
    void domainShouldNotDependOnUseCasesOrControllers() {
        noClasses().that().resideInAPackage(BASE + "core.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + "core.usecase..", BASE + "core.controller..", BASE + "core.gateway..")
                .check(CLASSES);
    }

    @Test
    void newCodeShouldNotDependOnOldPackage() {
        noClasses().that().resideOutsideOfPackage(BASE + "old..")
                .should().dependOnClassesThat().resideInAPackage(BASE + "old..")
                .check(CLASSES);
    }
}
