package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.annotation.testutil.ToggleProperties;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@EnableConfigurationProperties(ToggleProperties.class)
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class MetadataScenarios {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

    @Autowired
    ToggleProperties toggleProperties;

    private RequestSpecification request;
    private RequestSpecification unAuthenticatedRequest;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);

        unAuthenticatedRequest = testUtil
                .unauthenticatedRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void shouldReturn201WhenCreateNewMetadata() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createMetadata(documentId);

        response
                .statusCode(201)
                .body("rotationAngle", equalTo(90))
                .body("documentId", equalTo(documentId))
                .header("Location", equalTo("/api/metadata/" + documentId))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenCreateNewMetadataWithoutDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        metadataPayload.remove("documentId");

        request
                .body(metadataPayload.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(400)
                .body("fieldErrors", Matchers.hasSize(1))
                .body("fieldErrors[0].field", equalTo("documentId"))
                .body("fieldErrors[0].message", equalTo("NotNull"))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenCreateNewMetadataWithoutRotationAngle() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        metadataPayload.remove("rotationAngle");

        request
                .body(metadataPayload.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(400)
                .body("fieldErrors", Matchers.hasSize(1))
                .body("fieldErrors[0].field", equalTo("rotationAngle"))
                .body("fieldErrors[0].message", equalTo("NotNull"))
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserCreateNewMetadata() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        unAuthenticatedRequest
                .body(metadataPayload)
                .post("/api/metadata/")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetMetadataByDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        request
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(200)
                .body("rotationAngle", equalTo(90))
                .body("documentId", equalTo(documentId))
                .log().all();
    }

    @Test
    public void shouldReturn204WhenGetMetadataByNonExistentDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();

        request
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetMetadataByDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        unAuthenticatedRequest
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(401)
                .log().all();
    }

    public JSONObject createMetadataPayload(final String documentId) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("rotationAngle", 90);
        jsonObject.put("documentId", documentId);

        return jsonObject;
    }

    @NotNull
    private ValidatableResponse createMetadata(final String documentId) {
        final JSONObject metadata = createMetadataPayload(documentId);

        return request
                .body(metadata.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(201)
                .log().all();
    }
}
