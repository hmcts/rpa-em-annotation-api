package uk.gov.hmcts.reform.em.annotation.functional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
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
public class BookmarkScenarios {

    @Autowired
    TestUtil testUtil;

    @Value("${test.url}")
    String testUrl;

    private UUID documentId = UUID.randomUUID();

    public JSONObject createBookmarkRequest() {
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

    @Before
    public void setUpBookmarks() {
        JSONObject jsonObject = createBookmarkRequest();
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);
    }

    @Test
    public void createBookmark() {
        JSONObject jsonObject = createBookmarkRequest();
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);
    }

    @Test
    public void createBookmarkInvalid() {
        JSONObject jsonObject = createBookmarkRequest();
        jsonObject.remove("name");

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(500);
    }

    @Test
    public void testGetBookmarks() {
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + String.format("/api/%s/bookmarks", documentId))
                .then()
                .statusCode(204);
    }

    @Test
    public void testGetBookmarksInvalidDocumentId() {
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/invalid/bookmarks")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetBookmarksEmptyResponse() {
        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + String.format("/api/%s/bookmarks", UUID.randomUUID()))
                .then()
                .statusCode(204);
    }

    @Test
    public void testUpdateBookmark() {
        JSONObject jsonObject = createBookmarkRequest();
        Object docId = jsonObject.get("id");

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);

        jsonObject.remove("name");
        jsonObject.put("name", "new name");

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("PUT", testUrl + "/api/bookmarks/")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateMultipleBookmarks() {
        JSONObject jsonObject = createBookmarkRequest();
        JSONArray jsonArray = new JSONArray();

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);

        jsonObject.remove("name");
        jsonObject.put("name", "new name");
        jsonArray.put(jsonObject);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(jsonArray.toString())
                .request("PUT", testUrl + "/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteBookmark() {
        JSONObject jsonObject = createBookmarkRequest();
        Object docId = jsonObject.get("id");

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("DELETE", testUrl + String.format("/api/bookmarks/%s", docId))
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteMultipleBookmarks() {
        JSONObject jsonObject = createBookmarkRequest();

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);

        JSONObject deleteBookmarkRequest = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Object bookmarkId = jsonObject.get("id");
        jsonArray.put(bookmarkId);

        deleteBookmarkRequest.put("updated", createBookmarkRequest());
        deleteBookmarkRequest.put("deleted", jsonArray);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(deleteBookmarkRequest.toString())
                .request("DELETE", testUrl + "/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteMultipleBookmarksUpdatedIsNull() {
        JSONObject jsonObject = createBookmarkRequest();

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonObject.toString())
                .request("POST", testUrl + "/api/bookmarks")
                .then()
                .statusCode(201);

        JSONObject deleteBookmarkRequest = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Object bookmarkId = jsonObject.get("id");
        jsonArray.put(bookmarkId);
        deleteBookmarkRequest.put("deleted", jsonArray);

        testUtil
                .authRequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(deleteBookmarkRequest.toString())
                .request("DELETE", testUrl + "/api/bookmarks_multiple")
                .then()
                .statusCode(200);
    }
}
