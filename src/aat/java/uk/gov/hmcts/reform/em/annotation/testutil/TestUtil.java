package uk.gov.hmcts.reform.em.annotation.testutil;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestUtil {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private S2sHelper s2sHelper;

    @PostConstruct
    void postConstruct() {
        RestAssured.useRelaxedHTTPSValidation();
        idamHelper.createUser("a@b.com", Stream.of("caseworker").collect(Collectors.toList()));
    }

    public RequestSpecification authRequest() {
        return RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", s2sHelper.getS2sToken());
    }

}
