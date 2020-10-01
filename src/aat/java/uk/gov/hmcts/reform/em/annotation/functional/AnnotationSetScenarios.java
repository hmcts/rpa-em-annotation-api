package uk.gov.hmcts.reform.em.annotation.functional;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@PropertySource(value = "classpath:application.yml")
@RunWith(SpringRunner.class)
public class AnnotationSetScenarios {

    @Autowired
    TestUtil testUtil;

    @Value("${test.url}")
    String testUrl;

    private String documentId = UUID.randomUUID().toString();

    @Test
    public void testCreateAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", UUID.randomUUID().toString());

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);
    }

    @Test
    public void testGetAnnotationSetsSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", UUID.randomUUID().toString());

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAnnotationSetByIdSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAnnotationSetByIdNoContent() {
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets/" + UUID.randomUUID().toString())
                .then()
                .statusCode(204);
    }

    @Test
    public void testUpdateAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);

        jsonObject.remove("documentId");
        jsonObject.put("documentId", UUID.randomUUID().toString());

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("PUT", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();
        String annotationSetId = UUID.randomUUID().toString();

        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", annotationSetId);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("DELETE", testUrl + "/api/annotation-sets/" + annotationSetId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testFilterAnnotationSetSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("documentId", documentId);
        jsonObject.put("id", UUID.randomUUID().toString());

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(201);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets/filter?documentId=" + documentId)
                .then()
                .statusCode(200);
    }

    @Test
    public void testFilterAnnotationSetNotFound() {
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets/filter?documentId=" + "1234")
                .then()
                .statusCode(404);
    }
}
