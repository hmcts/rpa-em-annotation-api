package uk.gov.hmcts.reform.em.annotation.service;

import uk.gov.hmcts.reform.idam.client.models.UserInfo;

public interface IdamDetailsFilterService {
    void saveIdamDetails(UserInfo userInfo);
}
