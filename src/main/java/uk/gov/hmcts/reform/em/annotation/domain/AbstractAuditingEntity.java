package uk.gov.hmcts.reform.em.annotation.domain;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.hmcts.reform.em.annotation.config.audit.EntityAuditEventListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@MappedSuperclass
@Audited
@EntityListeners({AuditingEntityListener.class, EntityAuditEventListener.class})
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="created_by",insertable = false, updatable = false)
    private IdamDetails createdByDetails;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="last_modified_by",insertable = false, updatable = false)
    private IdamDetails lastModifiedByDetails;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate = Instant.now();

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public IdamDetails getCreatedByDetails() {
        return createdByDetails;
    }

    public void setCreatedByDetails(IdamDetails createdByDetails) {
        this.createdByDetails = createdByDetails;
    }

    public IdamDetails getLastModifiedByDetails() {
        return lastModifiedByDetails;
    }

    public void setLastModifiedByDetails(IdamDetails lastModifiedByDetails) {
        this.lastModifiedByDetails = lastModifiedByDetails;
    }
}
