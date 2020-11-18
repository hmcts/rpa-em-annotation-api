package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
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

    private final String documentId = UUID.randomUUID().toString();

    private RequestSpecification request;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void testCreateAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", UUID.randomUUID().toString());

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);
    }

    @Test
    public void testGetAnnotationSetsSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", UUID.randomUUID().toString());

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        request
                .get("/api/annotation-sets")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAnnotationSetByIdSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        request
                .get("/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAnnotationSetByIdNoContent() {
        request
                .get("/api/annotation-sets/" + UUID.randomUUID().toString())
                .then()
                .statusCode(204);
    }

    @Test
    public void testUpdateAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        jsonObject.remove("documentId");
        jsonObject.put("documentId", UUID.randomUUID().toString());

        request
                .body(jsonObject.toString())
                .put("/api/annotation-sets")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        request
                .delete("/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testFilterAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", documentId);
        jsonObject.put("id", UUID.randomUUID().toString());

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);

        request
                .get("/api/annotation-sets/filter?documentId=" + documentId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testFilterAnnotationSetNotFound() {
        request
                .get("/api/annotation-sets/filter?documentId=" + "1234")
                .then()
                .statusCode(404);
    }
}