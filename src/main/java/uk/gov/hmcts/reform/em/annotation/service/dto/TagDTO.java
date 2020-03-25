package uk.gov.hmcts.reform.em.annotation.service.dto;

import javax.validation.constraints.Size;

/**
 * A DTO for the Tag entity.
 */
public class TagDTO {

    @Size(max = 35)
    private String name;

    private String createdBy;

    @Size(max = 20)
    private String label;

    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
