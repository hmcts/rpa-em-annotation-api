package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

/**
 * Mapper for the entity Metadata and its DTO MetadataDto.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MetadataMapper extends EntityMapper<MetadataDto, Metadata> {
}
