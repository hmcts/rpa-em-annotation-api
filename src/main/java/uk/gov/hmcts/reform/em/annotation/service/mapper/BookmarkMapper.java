package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;

/**
 * Mapper for the entity Bookmark and its DTO BookmarkDTO.
 */
@Mapper(componentModel = "spring")
public interface BookmarkMapper extends EntityMapper<BookmarkDTO, Bookmark> {
}
