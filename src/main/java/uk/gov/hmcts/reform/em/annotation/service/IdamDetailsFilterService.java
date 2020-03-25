package uk.gov.hmcts.reform.em.annotation.service;

import uk.gov.hmcts.reform.idam.client.models.UserDetails;

public interface IdamDetailsFilterService {
    void saveIdamDetails(UserDetails userDetails);
}
