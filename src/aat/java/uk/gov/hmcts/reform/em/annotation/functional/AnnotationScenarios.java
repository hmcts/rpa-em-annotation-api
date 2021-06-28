package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
public class AnnotationScenarios {

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
    public void shouldReturn201WhenCreateNewAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);

        response
                .statusCode(201)
                .body("id", equalTo(annotationId))
                .body("page", is(1))
                .body("color", is("d1d1d1"))
                .body("comments", Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .header("Location", equalTo("/api/annotations/" + annotationId))
                .log().all();

    }

    @Test
    public void shouldReturn400WhenCreateNewAnnotationWithBadPayload() {
        final String newAnnotationSetId = createAnnotationSet();
        final JSONObject annotation = new JSONObject();
        annotation.put("annotationSetId", newAnnotationSetId);
        annotation.put("annotationType", "highlight");
        annotation.put("page", 1);
        annotation.put("color", "d1d1d1");

        request
                .body(annotation)
                .post("/api/annotations")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserCreateNewAnnotation() {
        final String newAnnotationSetId = UUID.randomUUID().toString();
        final String annotationId = UUID.randomUUID().toString();

        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        unAuthenticatedRequest
                .body(createAnnotations.toString())
                .post("/api/annotations")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAnnotationById() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final String id = extractJsonObjectFromResponse(response).getString("id");

        request
                .get("/api/annotations/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationId))
                .body("page", is(1))
                .body("color", is("d1d1d1"))
                .body("comments", Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .log().all();
    }

    @Test
    public void shouldReturn404WhenGetAnnotationNotFoundById() {
        final String annotationId = UUID.randomUUID().toString();
        request
                .get("/api/annotations/" + annotationId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetAnnotationById() {
        final String annotationId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get("/api/annotations/" + annotationId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllAnnotations() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        createAnnotation(annotationId, annotationSetId);

        request
                .get("/api/annotations")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetAllAnnotations() {
        unAuthenticatedRequest
                .get("/api/annotations")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);
        annotation.put("page", 2);
        annotation.put("color", "f1f1f1");

        request
                .body(annotation.toString())
                .put("/api/annotations")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationId))
                .body("page", is(2))
                .body("color", is("f1f1f1"))
                .body("comments", Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateAnnotationWithoutId() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);

        annotation.remove("id");

        request
                .body(annotation.toString())
                .put("/api/annotations")
                .then()
                .statusCode(400)
                .log().all();
    }


    @Test
    public void shouldReturn401WhenUnAuthenticatedUserUpdateAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);

        unAuthenticatedRequest
                .body(annotation.toString())
                .put("/api/annotations")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteAnnotationById() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);

        final String id = extractJsonObjectFromResponse(response).getString("id");
        final ValidatableResponse deletedResponse = deleteAnnotationById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    public void shouldReturn404WhenDeleteAnnotationByNonExistentId() {
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteAnnotationById(nonExistentAnnotationId);

        deletedResponse.statusCode(404);
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserDeleteAnnotation() {
        unAuthenticatedRequest
                .delete("/api/annotations/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateAnnotationAfterItHasBeenDeleted() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);

        final JSONObject annotation = extractJsonObjectFromResponse(response);
        final String id = annotation.getString("id");
        deleteAnnotationById(id).statusCode(200);
        annotation.put("page", 3);
        annotation.put("color", "e1e1e1");

        request
                .body(annotation.toString())
                .put("/api/annotations")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationId))
                .body("page", is(3))
                .body("color", is("e1e1e1"))
                .body("comments", Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .log().all();
    }

    @NotNull
    private ValidatableResponse deleteAnnotationById(String annotationId) {
        return request
                .delete("/api/annotations/" + annotationId)
                .then()
                .log().all();
    }

    @NotNull
    private String createAnnotationSet() {
        final JSONObject jsonObject = new JSONObject();
        final UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", newAnnotationSetId.toString());

        return request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201)
                .body("id", equalTo(newAnnotationSetId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("id");
    }

    @NotNull
    private ValidatableResponse createAnnotation(String annotationId, String annotationSetId) {
        final JSONObject annotation = createAnnotationPayload(annotationId, annotationSetId);
        return request
                .body(annotation)
                .post("/api/annotations")
                .then()
                .statusCode(201)
                .log().all();
    }

    @NotNull
    private JSONObject createAnnotationPayload(String annotationId, String annotationSetId) {
        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", annotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        final JSONArray comments = new JSONArray();
        final JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", annotationId);
        comment.put("id", UUID.randomUUID().toString());
        comments.put(0, comment);
        createAnnotations.put("comments", comments);

        final JSONArray rectangles = new JSONArray();
        final JSONObject rectangle = new JSONObject();
        rectangle.put("id", UUID.randomUUID().toString());
        rectangle.put("annotationId", annotationId);
        rectangle.put("x", 0f);
        rectangle.put("y", 0f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        rectangles.put(0, rectangle);
        createAnnotations.put("rectangles", rectangles);

        return createAnnotations;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
