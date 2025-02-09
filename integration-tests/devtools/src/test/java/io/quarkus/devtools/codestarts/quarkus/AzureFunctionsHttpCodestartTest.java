package io.quarkus.devtools.codestarts.quarkus;

import static io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog.Language.JAVA;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class AzureFunctionsHttpCodestartTest {
    @RegisterExtension
    public static QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .codestarts("azure-functions-http")
            .languages(JAVA)
            .build();

    @Test
    void testContent() throws Throwable {
        codestartTest.assertThatGeneratedFileMatchSnapshot(JAVA, "azure-config/function.json");
        codestartTest.assertThatGeneratedFileMatchSnapshot(JAVA, "azure-config/host.json");
        codestartTest.assertThatGeneratedFileMatchSnapshot(JAVA, "azure-config/local.settings.json");
    }

    @Test
    @EnabledIfSystemProperty(named = "build-projects", matches = "true")
    void buildAllProjectsForLocalUse() throws Throwable {
        codestartTest.buildAllProjects();
    }
}
