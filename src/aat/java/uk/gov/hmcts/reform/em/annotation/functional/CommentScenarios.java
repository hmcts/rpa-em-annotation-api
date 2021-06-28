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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class CommentScenarios {

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
    public void shouldReturn201WhenCreateNewComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);

        response
                .statusCode(201)
                .body("id", equalTo(commentId))
                .body("content", equalTo("text"))
                .body("annotationId", equalTo(annotationId))
                .header("Location", equalTo("/api/comments/" + commentId))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenCreateNewCommentWithoutId() {
        final String annotationId = UUID.randomUUID().toString();
        final JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", annotationId);

        request
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserCreateNewComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(annotationId, commentId);

        unAuthenticatedRequest
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn500WhenCreateNewCommentWithNonExistentAnnotationId() {
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(nonExistentAnnotationId, commentId);
        request
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetCommentById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString("id");

        request
                .get("/api/comments/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("content", equalTo("text"))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    @Test
    public void shouldReturn404WhenGetCommentNotFoundById() {
        final String commentId = UUID.randomUUID().toString();
        request
                .get("/api/comments/" + commentId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetCommentById() {
        final String commentId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get("/api/comments/" + commentId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllComments() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString("id");

        request
                .get("/api/comments")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }


    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetAllComments() {
        unAuthenticatedRequest
                .get("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final JSONObject comment = extractJsonObjectFromResponse(response);
        comment.put("content", "updated text");

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("content", equalTo("updated text"))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    @Test
    public void shouldReturn500WhenUpdateCommentWithNonExistentAnnotationId() {
        final String commentId = UUID.randomUUID().toString();
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(nonExistentAnnotationId, commentId);
        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateCommentWithoutId() {
        final JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserUpdateComment() {
        final JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        unAuthenticatedRequest
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteCommentById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString("id");
        final ValidatableResponse deletedResponse = deleteCommentById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    public void shouldReturn404WhenDeleteCommentByNonExistentId() {
        final String nonExistentId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteCommentById(nonExistentId);

        deletedResponse.statusCode(404);
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserDeleteComment() {
        unAuthenticatedRequest
                .delete("/api/comments/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenTryToUpdateCommentAfterItHasBeenDeleted() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final JSONObject comment = extractJsonObjectFromResponse(response);
        final String id = comment.getString("id");
        deleteCommentById(id).statusCode(200);
        comment.put("content", "new text");

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("content", equalTo("new text"))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    private ValidatableResponse deleteCommentById(String commentId) {
        return request
                .delete("/api/comments/" + commentId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createComment(String annotationId, String commentId) {
        final JSONObject comment = createCommentPayload(annotationId, commentId);

        return request.log().all()
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(201);
    }

    @NotNull
    private String createAnnotation(String newAnnotationSetId) {
        final UUID annotationId = UUID.randomUUID();
        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        return request
                .body(createAnnotations)
                .post("/api/annotations")
                .then()
                .statusCode(201)
                .body("id", equalTo(annotationId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("id");
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
    private JSONObject createCommentPayload(String annotationId, String commentId) {
        final JSONObject comment = new JSONObject();
        comment.put("id", commentId);
        comment.put("content", "text");
        comment.put("annotationId", annotationId);
        return comment;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
