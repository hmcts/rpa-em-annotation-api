package uk.gov.hmcts.reform.em.annotation.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;
import uk.gov.hmcts.reform.em.annotation.service.mapper.MetadataMapper;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final Logger log = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private final MetadataRepository metadataRepository;

    private final MetadataMapper metadataMapper;

    private final SecurityUtils securityUtils;

    @Override
    public MetadataDto save(MetadataDto metadataDto) {

        Metadata metadata = metadataRepository.findByDocumentId(metadataDto.getDocumentId());

        if(Objects.nonNull(metadata)) {
            metadata.setRotationAngle(metadataDto.getRotationAngle());
        } else {
            metadata = metadataMapper.toEntity(metadataDto);
            metadata.setCreatedBy(securityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UsernameNotFoundException("User not found.")));
        }
        metadata = metadataRepository.save(metadata);

        return metadataMapper.toDto(metadata);

    }

    @Override
    public MetadataDto findByDocumentId(UUID documentId) {

        Metadata metadata = metadataRepository.findByDocumentId(documentId);

        return metadataMapper.toDto(metadata);

    }
}
