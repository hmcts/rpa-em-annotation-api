package uk.gov.hmcts.reform.em.annotation.service.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A DTO for the Metadata entity.
 */
@Data
public class MetadataDto {

    @NotNull(message = "Rotation Angle can not null")
    private Integer rotationAngle;

    @NotNull(message = "DocumentId can not null")
    private UUID documentId;
}
