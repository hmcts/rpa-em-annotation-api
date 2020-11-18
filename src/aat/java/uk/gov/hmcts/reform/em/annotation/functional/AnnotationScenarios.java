package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

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

    private RequestSpecification request;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void testGetAnnotationSets() {
        request
                .get("/api/annotation-sets")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCreateAnnotationSetAndAnnotationsThenUpdateThenDelete() {

        JSONObject jsonObject = new JSONObject();

        UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", newAnnotationSetId.toString());

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        UUID annotationId = UUID.randomUUID();
        JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        JSONArray comments = new JSONArray();
        JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", annotationId);
        comment.put("id", UUID.randomUUID().toString());
        comments.put(0, comment);
        createAnnotations.put("comments", comments);

        JSONArray rectangles = new JSONArray();
        JSONObject rectangle = new JSONObject();
        rectangle.put("id", UUID.randomUUID().toString());
        rectangle.put("annotationId", annotationId);
        rectangle.put("x", 0f);
        rectangle.put("y", 0f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        rectangles.put(0, rectangle);
        createAnnotations.put("rectangles", rectangles);

        request
                .body(createAnnotations)
                .post("/api/annotations")
                .then()
                .statusCode(201)
                .body("id", equalTo(annotationId.toString()))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .body("comments[0].content", is("text"))
                .body("comments", Matchers.hasSize(1));

        comment.put("content", "text2");
        rectangle.put("height", 13f);

        request
                .body(createAnnotations)
                .put("/api/annotations")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationId.toString()))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(13f))
                .body("comments[0].content", is("text2"))
                .body("comments", Matchers.hasSize(1));

        request
                .delete("/api/annotations/" + annotationId.toString())
                .then()
                .statusCode(200);

        request
                .get("/api/annotations/" + annotationId.toString())
                .then()
                .statusCode(404);
    }
}