package uk.gov.hmcts.reform.em.annotation.functional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.io.IOException;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@PropertySource(value = "classpath:application.yml")
@RunWith(SpringRunner.class)
public class OpenIdConnectScenarios {

    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();

    @Autowired
    TestUtil testUtil;

    @Value("${test.url}")
    String testUrl;

    @Test
    // Invalid IdamAuth
    public void testWithInvalidIdamAuth() throws IOException, InterruptedException {

        testUtil
                .invalidIdamAuthrequest()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(401);
    }

    @Test
    // Empty S2SAuth
    public void testWithEmptyS2SAuth() throws IOException, InterruptedException {

        exceptionThrown.expect(IllegalArgumentException.class);

        testUtil
                .validAuthRequestWithEmptyS2SAuth()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets")
                .then()
                .statusCode(401);
    }

    @Test
    // Empty IdamAuth and Valid S2S Auth
    public void testWithEmptyIdamAuthAndValidS2SAuth() throws IOException, InterruptedException {
        exceptionThrown.expect(IllegalArgumentException.class);

        testUtil
                .validS2SAuthWithEmptyIdamAuth()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets");

        exceptionThrown.expectMessage("Header value cannot be null");
    }

    @Test
    // Empty IdamAuth and Empty S2SAuth
    public void testIdamAuthAndS2SAuthAreEmpty() throws IOException, InterruptedException {
        exceptionThrown.expect(IllegalArgumentException.class);

        testUtil
                .emptyIdamAuthAndEmptyS2SAuth()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", testUrl + "/api/annotation-sets");
    }

}
