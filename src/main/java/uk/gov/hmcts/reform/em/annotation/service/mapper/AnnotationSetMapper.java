package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.UUID;

/**
 * Mapper for the entity AnnotationSet and its DTO AnnotationSetDTO.
 */
@Mapper(componentModel = "spring", uses = {AnnotationMapper.class})
public interface AnnotationSetMapper extends EntityMapper<AnnotationSetDTO, AnnotationSet> {

    @Mapping(target = "annotations", ignore = true)
    @Mapping(target = "createdByDetails", ignore = true)
    @Mapping(target = "lastModifiedByDetails", ignore = true)
    AnnotationSet toEntity(AnnotationSetDTO annotationSetDTO);

    default AnnotationSet fromId(UUID id) {
        if (id == null) {
            return null;
        }
        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(id);
        return annotationSet;
    }
}
