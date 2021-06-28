package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class AnnotationSetScenarios {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

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
    public void shouldReturn201WhenCreateNewAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);

        response
                .statusCode(201)
                .body("documentId", equalTo(documentId.toString()))
                .body("id", equalTo(annotationSetId.toString()))
                .header("Location", equalTo("/api/annotation-sets/" + annotationSetId))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenCreateNewAnnotationSetWithoutId() {
        final JSONObject annotationSet = new JSONObject();
        final UUID documentId = UUID.randomUUID();
        annotationSet.put("documentId", documentId);

        request
                .body(annotationSet.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserCreateNewAnnotationSet() {
        final JSONObject annotationSet = new JSONObject();
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        annotationSet.put("documentId", documentId);
        annotationSet.put("id", annotationSetId.toString());

        unAuthenticatedRequest
                .body(annotationSet.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAnnotationSetById() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final String id = annotationSet.getString("id");

        request
                .get("/api/annotation-sets/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationSetId.toString()))
                .body("documentId", is(documentId.toString()))
                .log().all();
    }

    @Test
    public void shouldReturn204WhenGetAnnotationSetNotFoundById() {
        final String annotationSetId = UUID.randomUUID().toString();

        request
                .get("/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetAnnotationSetById() {
        final String annotationSetId = UUID.randomUUID().toString();

        unAuthenticatedRequest
                .get("/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllAnnotationSets() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        createAnnotationSet(annotationSetId, documentId);

        request
                .get("/api/annotation-sets")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetAllAnnotationSets() {
        unAuthenticatedRequest
                .get("/api/annotation-sets")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final UUID newDocumentId = UUID.randomUUID();
        annotationSet.put("documentId", newDocumentId);

        request
                .body(annotationSet.toString())
                .put("/api/annotation-sets")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationSetId.toString()))
                .body("documentId", is(newDocumentId.toString()))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateAnnotationSetWithoutId() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        annotationSet.remove("id");

        request
                .body(annotationSet.toString())
                .put("/api/annotation-sets")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserUpdateAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final UUID newDocumentId = UUID.randomUUID();
        annotationSet.put("documentId", newDocumentId);

        unAuthenticatedRequest
                .body(annotationSet.toString())
                .put("/api/annotation-sets")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteAnnotationSetById() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final String id = extractJsonObjectFromResponse(response).getString("id");
        final ValidatableResponse deletedResponse = deleteAnnotationSetById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    public void shouldReturn404WhenDeleteAnnotationSetByNonExistentId() {
        final String nonExistentAnnotationSetId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteAnnotationSetById(nonExistentAnnotationSetId);

        deletedResponse.statusCode(404);
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserDeleteAnnotationSet() {
        unAuthenticatedRequest
                .delete("/api/annotation-sets/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateAnnotationSetAfterItHasBeenDeleted() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final UUID newDocumentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final String id = extractJsonObjectFromResponse(response).getString("id");
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        deleteAnnotationSetById(id).statusCode(200);

        annotationSet.put("documentId", newDocumentId);

        request
                .body(annotationSet.toString())
                .put("/api/annotation-sets/")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationSetId.toString()))
                .body("documentId", is(newDocumentId.toString()))
                .log().all();
    }

    @NotNull
    private ValidatableResponse deleteAnnotationSetById(String annotationSetId) {
        return request
                .delete("/api/annotation-sets/" + annotationSetId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createAnnotationSet(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = createAnnotationSetPayload(annotationSetId, documentId);
        return request
                .body(annotationSet.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201)
                .log().all();
    }

    @NotNull
    private JSONObject createAnnotationSetPayload(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = new JSONObject();
        annotationSet.put("documentId", documentId);
        annotationSet.put("id", annotationSetId.toString());

        return annotationSet;
    }


    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }

}
