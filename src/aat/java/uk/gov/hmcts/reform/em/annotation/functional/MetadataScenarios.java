package uk.gov.hmcts.reform.em.annotation.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.annotation.testutil.ToggleProperties;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@PropertySource(value = "classpath:application.yml")
@EnableConfigurationProperties(ToggleProperties.class)
@RunWith(SpringIntegrationSerenityRunner.class)
public class MetadataScenarios {

    @Autowired
    TestUtil testUtil;

    @Value("${test.url}")
    String testUrl;

    @Autowired
    ToggleProperties toggleProperties;

    @Test
    public void testSaveSuccessCreate() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        JSONObject jsonObject = new JSONObject(metadataDto);

        testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject)
            .request("POST", testUrl + "/api/metadata/")
            .then()
            .statusCode(201);
    }

    @Test
    public void testSaveSuccessUpdate() {

        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        Assume.assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        MetadataDto metadataDto = testUtil.createMetadataDto();
        JSONObject jsonObject = new JSONObject(metadataDto);

        testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject)
            .request("POST", testUrl + "/api/metadata/")
            .then()
            .statusCode(201);

        metadataDto.setRotationAngle(180);
        JSONObject updateJson = new JSONObject(metadataDto);

        testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(updateJson)
            .request("POST", testUrl + "/api/metadata/")
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

        List result = testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject)
            .request("POST", testUrl + "/api/metadata/")
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

        List result = testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject)
            .request("POST", testUrl + "/api/metadata/")
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

        testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject)
            .request("POST", testUrl + "/api/metadata/")
            .then()
            .statusCode(201);

        testUtil.authRequest()
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .request("GET", testUrl + "/api/metadata/" + metadataDto.getDocumentId())
            .then()
            .statusCode(200);

    }
}
