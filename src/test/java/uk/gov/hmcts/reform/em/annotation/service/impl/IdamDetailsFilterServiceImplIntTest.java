package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class IdamDetailsFilterServiceImplIntTest extends BaseTest {

    @Autowired
    IdamDetailsFilterService idamDetailsFilterService;
    @Autowired
    IdamDetailsRepository idamDetailsRepository;

    @Test
    public void testSaveIdamDetails() {

        assertFalse(idamDetailsRepository.existsById("1"));

        final UserDetails userDetails = UserDetails.builder()
                .id("1")
                .forename("FN")
                .email("user@idam.com")
                .build();
        idamDetailsFilterService.saveIdamDetails(userDetails);

        Assert.assertTrue(idamDetailsRepository.existsById("1"));

    }

    @Test
    @DisplayName("Save IdamDetails with empty User.id")
    public void testSaveIdamDetailsWithEmptyUserDetailsId() {

        final int countBeforeSave = idamDetailsRepository.findAll().size();

        final UserDetails userDetails = UserDetails.builder()
                .id(null)
                .forename("FN")
                .email("user@idam.com")
                .build();
        idamDetailsFilterService.saveIdamDetails(userDetails);

        final int countPostSave = idamDetailsRepository.findAll().size();

        Assert.assertEquals(countBeforeSave, countPostSave);


    }

    @Test
    @DisplayName("Save IdamDetails with UserDetails.surname and Retrieve it to ensure surname has been persisted.")
    public void testSaveIdamDetailSurname() {

        final String randomId = random(3, false, true);
        final String surname = random(8, true, false);

        final UserDetails userDetails = UserDetails.builder()
                .id(randomId)
                .forename("FN")
                .surname(surname)
                .email("user@idam.com")
                .build();
        idamDetailsFilterService.saveIdamDetails(userDetails);

        assertTrue(idamDetailsRepository.existsById(randomId));

        final Optional<IdamDetails> optUserDetails = idamDetailsRepository.findById(randomId);

        if (optUserDetails.isPresent()) {
            final IdamDetails idamDetails = optUserDetails.get();
            assertThat(idamDetails.getSurname(), is(surname));
        } else {
            fail(" userDetails not found for %s " + randomId);
        }
    }
}
