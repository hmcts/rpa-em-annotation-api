package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
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

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void shouldReturn201WhenCreateNewBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        response
                .statusCode(201)
                .body("id", equalTo(bookmarkId.toString()))
                .body("documentId", equalTo(documentId.toString()))
                .body("name", equalTo("Bookmark for test"))
                .body("createdBy", equalTo("user"))
                .body("pageNumber", equalTo(1))
                .body("xCoordinate", equalTo(100f))
                .body("yCoordinate", equalTo(100f))
                .body("parent", notNullValue())
                .body("previous", notNullValue())
                .header("Location", equalTo("/api/bookmarks/" + bookmarkId))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenCreateNewBookmarkWithoutId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);

        bookmarkRequestPayload.remove("id");

        request
                .body(bookmarkRequestPayload.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserCreateNewBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);

        unAuthenticatedRequest
                .body(bookmarkRequestPayload.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn500WhenCreateNewBookmarkWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);
        bookmarkRequestPayload.remove("name");

        request
                .body(bookmarkRequestPayload.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllBookmarksByDocumentId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject jsonObject = createBookmarkRequestPayload(bookmarkId);
        jsonObject.remove("createdBy");

        final ValidatableResponse response =
                request.log().all()
                        .body(jsonObject.toString())
                        .post("/api/bookmarks")
                        .then()
                        .statusCode(201);

        final JSONObject newJsonObject = extractJsonObjectFromResponse(response);
        final String documentId = newJsonObject.getString("documentId");

        request
                .get(String.format("/api/%s/bookmarks", documentId))
                .then()
                .statusCode(200)
                .body("id", equalTo(Arrays.asList(bookmarkId.toString())))
                .body("documentId", equalTo(Arrays.asList(documentId)))
                .body("name", equalTo(Arrays.asList("Bookmark for test")))
                .body("pageNumber", equalTo(Arrays.asList(1)))
                .body("xCoordinate", equalTo(Arrays.asList(100.00f)))
                .body("yCoordinate", equalTo(Arrays.asList(100.00f)))
                .log().all();
    }

    @Test
    public void shouldReturn404WhenResponseBodyIsEmptyForGivenDocId() {
        request
                .get(String.format("/api/%s/bookmarks", UUID.randomUUID()))
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserGetBookmarksById() {
        unAuthenticatedRequest
                .get(String.format("/api/%s/bookmarks", UUID.randomUUID()))
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.put("name", "new name");

        request
                .body(jsonObject.toString())
                .put("/api/bookmarks")
                .then()
                .statusCode(200)
                .body("id", equalTo(bookmarkId.toString()))
                .body("documentId", equalTo(documentId.toString()))
                .body("name", equalTo("new name"))
                .body("createdBy", equalTo("user"))
                .body("pageNumber", equalTo(1))
                .body("xCoordinate", equalTo(100f))
                .body("yCoordinate", equalTo(100f))
                .body("parent", notNullValue())
                .body("previous", notNullValue())
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateBookmarkWithBadRequestPayload() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove("id");

        request
                .body(jsonObject.toString())
                .put("/api/bookmarks/")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserUpdateBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);

        unAuthenticatedRequest
                .body(jsonObject.toString())
                .put("/api/bookmarks")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn500WhenUpdateBookmarkWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove("name");
        request
                .body(jsonObject.toString())
                .put("/api/bookmarks")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenUpdateMultipleBookmarks() {
        final UUID bookmarkId1 = UUID.randomUUID();
        final ValidatableResponse response1 = createBookmark(bookmarkId1);
        final JSONObject jsonObject1 = extractJsonObjectFromResponse(response1);

        final UUID bookmarkId2 = UUID.randomUUID();
        final ValidatableResponse response2 = createBookmark(bookmarkId2);
        final JSONObject jsonObject2 = extractJsonObjectFromResponse(response2);

        final JSONArray jsonArray = new JSONArray();

        jsonObject1.put("name", "new name-1");
        jsonArray.put(jsonObject1);

        jsonObject2.put("name", "new name-2");
        jsonArray.put(jsonObject2);

        request
                .body(jsonArray.toString())
                .put("/api/bookmarks_multiple")
                .then()
                .statusCode(200)
                .body("documentId", hasItems(documentId.toString()))
                .body("id", hasItems(bookmarkId1.toString(), bookmarkId2.toString()))
                .body("name", hasItems("new name-1", "new name-2"))
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateMultipleBookmarksWithBadRequestPayload() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        final JSONArray jsonArray = new JSONArray();
        jsonObject.remove("id");
        jsonArray.put(jsonObject);

        request
                .body(jsonArray.toString())
                .put("/api/bookmarks_multiple")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserUpdateMultipleBookmarks() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        unAuthenticatedRequest
                .body(jsonArray.toString())
                .put("/api/bookmarks_multiple")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn500WhenUpdateMultipleBookmarksWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove("name");
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        request
                .body(jsonArray.toString())
                .put("/api/bookmarks_multiple")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteBookmarkById() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        final String id = jsonObject.getString("id");
        request
                .delete(String.format("/api/bookmarks/%s", id))
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserDeleteBookmarkById() {
        unAuthenticatedRequest
                .delete(String.format("/api/bookmarks/%s", UUID.randomUUID()))
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn404WhenDeleteBookmarkByNonExistentId() {
        request
                .delete(String.format("/api/bookmarks/%s", UUID.randomUUID()))
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteMultipleBookmarks() {
        final UUID bookmarkId1 = UUID.randomUUID();
        final ValidatableResponse response1 = createBookmark(bookmarkId1);
        final JSONObject jsonObject1 = extractJsonObjectFromResponse(response1);

        final UUID bookmarkId2 = UUID.randomUUID();
        final ValidatableResponse response2 = createBookmark(bookmarkId2);
        final JSONObject jsonObject2 = extractJsonObjectFromResponse(response2);

        final JSONObject deleteBookmarkRequest = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        final Object id1 = jsonObject1.get("id");
        final Object id2 = jsonObject2.get("id");
        jsonArray.put(id1);
        jsonArray.put(id2);

        deleteBookmarkRequest.put("deleted", jsonArray);

        request
                .body(deleteBookmarkRequest.toString())
                .delete("/api/bookmarks_multiple")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUnAuthenticatedUserDeleteMultipleBookmarks() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject deleteBookmarkRequest = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(bookmarkId);
        deleteBookmarkRequest.put("deleted", jsonArray);

        unAuthenticatedRequest
                .body(deleteBookmarkRequest.toString())
                .delete("/api/bookmarks_multiple")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn404WhenDeleteMultipleBookmarksWithNonExistentId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject deleteBookmarkRequest = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        jsonArray.put(bookmarkId);
        deleteBookmarkRequest.put("deleted", jsonArray);

        request
                .body(deleteBookmarkRequest.toString())
                .delete("/api/bookmarks_multiple")
                .then()
                .statusCode(404)
                .log().all();
    }

    @NotNull
    private ValidatableResponse createBookmark(final UUID bookmarkId) {
        final JSONObject bookmark = createBookmarkRequestPayload(bookmarkId);
        return request.log().all()
                .body(bookmark.toString())
                .post("/api/bookmarks")
                .then()
                .statusCode(201);
    }

    private JSONObject createBookmarkRequestPayload(final UUID bookmarkId) {
        final JSONObject jsonObject = new JSONObject();

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

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
