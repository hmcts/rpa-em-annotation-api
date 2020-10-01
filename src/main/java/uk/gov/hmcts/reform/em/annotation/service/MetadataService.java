package uk.gov.hmcts.reform.em.annotation.service;

import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.util.UUID;

public interface MetadataService {

    MetadataDto save(MetadataDto metadataDto);

    MetadataDto findByDocumentId(UUID documentId);
}
