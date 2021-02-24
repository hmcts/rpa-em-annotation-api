package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class BookmarkScenarios {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

    private final UUID documentId = UUID.randomUUID();

    private RequestSpecification request;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void createBookmark() {
        JSONObject jsonObject = createBookmarkRequest();
        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);
    }

    @Test
    public void createBookmarkInvalid() {
        JSONObject jsonObject = createBookmarkRequest();
        jsonObject.remove("name");

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(500);
    }

    @Test
    public void testGetBookmarks() {
        request
                .get(String.format("/api/%s/bookmarks", documentId))
                .then()
                .statusCode(204);
    }

    @Test
    public void testGetBookmarksInvalidDocumentId() {
        request
                .get("/api/invalid/bookmarks")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetBookmarksEmptyResponse() {
        request
                .get(String.format("/api/%s/bookmarks", UUID.randomUUID()))
                .then()
                .statusCode(204);
    }

    @Test
    public void testUpdateBookmark() {
        JSONObject jsonObject = createBookmarkRequest();

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);

        jsonObject.remove("name");
        jsonObject.put("name", "new name");

        request
                .body(jsonObject.toString())
                .put("/api/bookmarks/")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateMultipleBookmarks() {
        JSONObject jsonObject = createBookmarkRequest();
        JSONArray jsonArray = new JSONArray();

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);

        jsonObject.remove("name");
        jsonObject.put("name", "new name");
        jsonArray.put(jsonObject);

        request
                .body(jsonArray.toString())
                .put("/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteBookmark() {
        JSONObject jsonObject = createBookmarkRequest();
        Object docId = jsonObject.get("id");

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);

        request
                .delete(String.format("/api/bookmarks/%s", docId))
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteMultipleBookmarks() {
        JSONObject jsonObject = createBookmarkRequest();

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);

        JSONObject deleteBookmarkRequest = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Object bookmarkId = jsonObject.get("id");
        jsonArray.put(bookmarkId);

        deleteBookmarkRequest.put("updated", createBookmarkRequest());
        deleteBookmarkRequest.put("deleted", jsonArray);

        request
                .body(deleteBookmarkRequest.toString())
                .delete("/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteMultipleBookmarksUpdatedIsNull() {
        JSONObject jsonObject = createBookmarkRequest();

        request
                .body(jsonObject.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);

        JSONObject deleteBookmarkRequest = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Object bookmarkId = jsonObject.get("id");
        jsonArray.put(bookmarkId);
        deleteBookmarkRequest.put("deleted", jsonArray);

        request
                .body(deleteBookmarkRequest.toString())
                .delete("/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }

    private JSONObject createBookmarkRequest() {
        JSONObject jsonObject = new JSONObject();
        UUID bookmarkId = UUID.randomUUID();

        jsonObject.put("id", bookmarkId);
        jsonObject.put("documentId", documentId);
        jsonObject.put("name", "Bookmark for test");
        jsonObject.put("createdBy", "user");
        jsonObject.put("pageNumber", 1);
        jsonObject.put("xCoordinate", 100.00);
        jsonObject.put("yCoordinate", 100.00);
        jsonObject.put("parent", UUID.randomUUID().toString());
        jsonObject.put("previous", UUID.randomUUID().toString());

        return jsonObject;
    }
}
