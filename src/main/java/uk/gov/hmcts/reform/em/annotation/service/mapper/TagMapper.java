package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

/**
 * Mapper for the entity Tag and its DTO TagDTO.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
}
