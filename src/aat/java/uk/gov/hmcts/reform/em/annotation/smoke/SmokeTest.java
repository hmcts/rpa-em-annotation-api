package uk.gov.hmcts.reform.em.annotation.smoke;

import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@PropertySource(value = "classpath:application.yml")
public class SmokeTest {

    @Value("${test.url}")
    String testUrl;

    @Test
    public void testHealthEndpoint() {

        RestAssured.useRelaxedHTTPSValidation();

        RestAssured.given()
            .request("GET", testUrl + "/health")
            .then()
            .statusCode(200);


    }
}
