package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.annotation.testutil.ToggleProperties;

import java.util.List;
import java.util.Map;

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

    @Autowired
    ToggleProperties toggleProperties;

    private RequestSpecification request;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void testSaveSuccessCreate() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        JSONObject jsonObject = new JSONObject(metadataDto);

        request
                .body(jsonObject)
                .post("/api/metadata/")
                .then()
                .statusCode(201);
    }

    @Test
    public void testSaveSuccessUpdate() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        JSONObject jsonObject = new JSONObject(metadataDto);

        request
                .body(jsonObject)
                .post("/api/metadata/")
                .then()
                .statusCode(201);

        metadataDto.setRotationAngle(180);
        JSONObject updateJson = new JSONObject(metadataDto);

        request
                .body(updateJson)
                .post("/api/metadata/")
                .then()
                .statusCode(201);
    }

    @Test
    public void testSaveSuccessMissingDocId() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        metadataDto.setDocumentId(null);
        JSONObject jsonObject = new JSONObject(metadataDto);

        List result =
                request
                        .body(jsonObject)
                        .post("/api/metadata/")
                        .then()
                        .statusCode(400)
                        .extract()
                        .body()
                        .jsonPath().get("fieldErrors");

        Map error = (Map) result.get(0);

        Assert.assertEquals("NotNull", error.get("message"));
        Assert.assertEquals("documentId", error.get("field"));
    }

    @Test
    public void testSaveSuccessMissingRotationAngle() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        metadataDto.setRotationAngle(null);
        JSONObject jsonObject = new JSONObject(metadataDto);

        List result =
                request
                        .body(jsonObject)
                        .post("/api/metadata/")
                        .then()
                        .statusCode(400)
                        .extract()
                        .body()
                        .jsonPath().get("fieldErrors");

        Map error = (Map) result.get(0);

        Assert.assertEquals("NotNull", error.get("message"));
        Assert.assertEquals("rotationAngle", error.get("field"));
    }

    @Test
    public void testFindByDocumentIdSuccess() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        JSONObject jsonObject = new JSONObject(metadataDto);

        request
                .body(jsonObject)
                .post("/api/metadata/")
                .then()
                .statusCode(201);

        request
                .get("/api/metadata/" + metadataDto.getDocumentId())
                .then()
                .statusCode(200);

    }
}