package uk.gov.hmcts.reform.em.annotation.testutil;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestUtil {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private S2sHelper s2sHelper;

    private String idamAuth;
    private String s2sAuth;

    private UUID documentId = UUID.randomUUID();

    @PostConstruct
    void postConstruct() {
        SerenityRest.useRelaxedHTTPSValidation();
        idamHelper.createUser("a@b.com", Stream.of("caseworker").collect(Collectors.toList()));
        idamAuth = idamHelper.authenticateUser("a@b.com");
        s2sAuth = s2sHelper.getS2sToken();
    }

    public RequestSpecification authRequest() {
        return SerenityRest
                .given()
                .header("Authorization", idamHelper.authenticateUser("a@b.com"))
                .header("ServiceAuthorization", s2sHelper.getS2sToken());
    }

    public RequestSpecification unauthenticatedRequest() {
        return SerenityRest.given();
    }

    public RequestSpecification emptyIdamAuthRequest() {
        return s2sAuthRequest()
                .header("Authorization", null);
    }

    public RequestSpecification emptyIdamAuthAndEmptyS2SAuth() {
        return SerenityRest
                .given()
                .header("ServiceAuthorization", null)
                .header("Authorization", null);
    }

    public RequestSpecification validAuthRequestWithEmptyS2SAuth() {
        return emptyS2sAuthRequest().header("Authorization", idamAuth);
    }

    public RequestSpecification validS2SAuthWithEmptyIdamAuth() {

        return s2sAuthRequest().header("Authorization", null);
    }

    private RequestSpecification emptyS2sAuthRequest() {

        return SerenityRest.given().header("ServiceAuthorization", null);
    }

    public RequestSpecification invalidIdamAuthrequest() {

        return s2sAuthRequest().header("Authorization", "invalidIDAMAuthRequest");
    }

    public RequestSpecification invalidS2SAuth() {

        return invalidS2sAuthRequest().header("Authorization", idamAuth);
    }

    private RequestSpecification invalidS2sAuthRequest() {

        return SerenityRest.given().header("ServiceAuthorization", "invalidS2SAuthorization");
    }

    private RequestSpecification s2sAuthRequest() {
        return SerenityRest
                .given()
                .header("ServiceAuthorization", s2sAuth);
    }

}
