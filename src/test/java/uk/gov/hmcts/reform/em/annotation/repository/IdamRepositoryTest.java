package uk.gov.hmcts.reform.em.annotation.repository;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * This is a Unit Test to test the interaction of IdamRepository with its dependencies (ie
 * idamClient ) . Please see src/aat/java/functional package for tests that cover Integration
 * scenarios which involves passing in valid auth tokens to verify the complete flow. ie (
 * Microservice --> OAuthFilter --> Endpoint )
 */
@RunWith(SpringRunner.class)
public class IdamRepositoryTest {

    @Mock
    private IdamClient idamClient;

    @InjectMocks
    private IdamRepository idamRepository;

    @Test
    @DisplayName("UserInfo should be called by IdamClient ")
    public void testGetUserInfo() {

        final UserInfo userInfo = UserInfo.builder().uid("100").givenName("John").familyName("Doe").roles(asList("Admin", "CaseWorker")).build();
        when(idamClient.getUserInfo(any(String.class))).thenReturn(userInfo);

        idamRepository.getUserInfo(random(5, true, false));

        verify(idamClient, times(1)).getUserInfo(anyString());

    }

}

