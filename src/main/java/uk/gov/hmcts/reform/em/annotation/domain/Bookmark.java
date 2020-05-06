package uk.gov.hmcts.reform.em.annotation.domain;

import org.springframework.data.annotation.CreatedBy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Bookmark.
 */
@Entity
@Table(name = "bookmark")
public class Bookmark implements Serializable {

    @Id
    private UUID id;

    @Size(min = 1, max = 30)
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "num", nullable = false)
    private Integer pageNumber;

    @Column(name = "x_coordinate")
    private Double xCoordinate;

    @Column(name = "y_coordinate")
    private Double yCoordinate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
}
