package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;

import java.util.Set;
import java.util.UUID;

/**
 * Mapper for the entity Annotation and its DTO AnnotationDTO.
 */
@Mapper(componentModel = "spring",
        uses = {AnnotationSetMapper.class, RectangleMapper.class, CommentMapper.class, TagMapper.class})
public interface AnnotationMapper extends EntityMapper<AnnotationDTO, Annotation> {

    @Mapping(source = "annotationSet.id", target = "annotationSetId")
    AnnotationDTO toDto(Annotation annotation);

    @Mapping(target = "comments")
    @Mapping(target = "tags")
    @Mapping(target = "rectangles")
    @Mapping(source = "annotationSetId", target = "annotationSet")
    @Mapping(target = "createdByDetails", ignore = true)
    @Mapping(target = "lastModifiedByDetails", ignore = true)
    Annotation toEntity(AnnotationDTO annotationDTO);

    default Annotation fromId(UUID id) {
        if (id == null) {
            return null;
        }
        Annotation annotation = new Annotation();
        annotation.setId(id);
        return annotation;
    }
}
