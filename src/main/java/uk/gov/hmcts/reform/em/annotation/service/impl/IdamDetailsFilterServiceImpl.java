package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

@Service
@Transactional
public class IdamDetailsFilterServiceImpl implements IdamDetailsFilterService {

    private final IdamDetailsRepository idamDetailsRepository;

    public IdamDetailsFilterServiceImpl(IdamDetailsRepository idamDetailsRepository) {
        this.idamDetailsRepository = idamDetailsRepository;
    }

    @Override
    public void saveIdamDetails(UserInfo userInfo) {

        if (StringUtils.isNotBlank(userInfo.getUid())
                && !idamDetailsRepository.existsById(userInfo.getUid())) {
            IdamDetails idamDetails = new IdamDetails();
            idamDetails.setId(userInfo.getUid());
            idamDetails.setForename(userInfo.getGivenName());
            idamDetails.setSurname(userInfo.getFamilyName());
            idamDetails.setEmail(userInfo.getSub());
            idamDetailsRepository.save(idamDetails);
        }
    }

}
