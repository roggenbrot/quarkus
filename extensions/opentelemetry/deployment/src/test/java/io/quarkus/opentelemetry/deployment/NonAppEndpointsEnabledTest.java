package io.quarkus.opentelemetry.deployment;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.opentelemetry.deployment.common.TestSpanExporter;
import io.quarkus.opentelemetry.deployment.common.TracerRouter;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;

public class NonAppEndpointsEnabledTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.opentelemetry.tracer.suppress-non-application-uris", "false")
            .withApplicationRoot((jar) -> jar
                    .addClass(TracerRouter.class)
                    .addClass(TestSpanExporter.class));

    @Inject
    TestSpanExporter testSpanExporter;

    @Test
    void testHealthEndpointNotTraced() throws InterruptedException {
        RestAssured.when().get("/q/health").then()
                .statusCode(200)
                .body(containsString("\"status\": \"UP\""));

        RestAssured.when().get("/q/health/live").then()
                .statusCode(200)
                .body(containsString("\"status\": \"UP\""));

        RestAssured.when().get("/q/health/ready").then()
                .statusCode(200)
                .body(containsString("\"status\": \"UP\""));

        RestAssured.when().get("/tracer").then()
                .statusCode(200)
                .body(is("Hello Tracer!"));

        testSpanExporter.assertSpanCount(5);
    }
}
