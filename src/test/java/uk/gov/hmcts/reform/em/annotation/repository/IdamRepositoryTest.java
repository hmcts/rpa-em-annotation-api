package uk.gov.hmcts.reform.em.annotation.repository;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;


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
    @DisplayName("UserDetails should be called by IdamClient ")
    public void testGetUserDetails() {

        final UserDetails userDetails = new UserDetails("100", "email@gmail.com", "John", "Doe", asList("Admin", "CaseWorker"));

        when(idamClient.getUserDetails(any(String.class))).thenReturn(userDetails);

        idamRepository.getUserDetails(random(5, true, false));

        verify(idamClient, times(1)).getUserDetails(anyString());

    }

}

