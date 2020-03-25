package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

import java.util.Optional;

@Service
@Transactional
public class IdamDetailsFilterServiceImpl implements IdamDetailsFilterService {

    private final IdamDetailsRepository idamDetailsRepository;

    public IdamDetailsFilterServiceImpl(IdamDetailsRepository idamDetailsRepository) {
        this.idamDetailsRepository = idamDetailsRepository;
    }

    @Override
    public void saveIdamDetails(UserDetails userDetails) {

        if (StringUtils.isNotBlank(userDetails.getId())
                && !idamDetailsRepository.existsById(userDetails.getId())) {
            IdamDetails idamDetails = new IdamDetails();
            idamDetails.setId(userDetails.getId());
            idamDetails.setForename(userDetails.getForename());
            Optional<String> optSurname = userDetails.getSurname();
            if(optSurname.isPresent()) {
                idamDetails.setSurname(optSurname.get());
            }
            idamDetails.setEmail(userDetails.getEmail());
            idamDetailsRepository.save(idamDetails);
        }
    }

}
