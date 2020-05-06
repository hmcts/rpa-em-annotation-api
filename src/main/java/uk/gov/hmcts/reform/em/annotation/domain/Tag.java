package uk.gov.hmcts.reform.em.annotation.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An Annotation Tag.
 */
@Entity
@Table(name = "tag")
@IdClass(TagId.class)
public class Tag implements Serializable {

    @Id
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Id
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "label", length = 20, nullable = false)
    private String label;

    @Column(name = "color", length = 20)
    private String color;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

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
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
}
