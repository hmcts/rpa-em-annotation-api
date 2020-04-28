package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * A Annotation.
 */
@Entity
@Table(name = "annotation")
public class Annotation extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Column(name = "annotation_type")
    private String annotationType;

    @Column(name = "page")
    private Integer page;

    @Column(name = "color")
    private String color;

    @OneToMany(mappedBy = "annotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "annotation_tags",
            joinColumns = @JoinColumn(name = "annotation_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "name", referencedColumnName = "name"),
                    @JoinColumn(name = "createdBy", referencedColumnName = "created_by")
    })
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "annotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rectangle> rectangles = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("annotations")
    private AnnotationSet annotationSet;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public Annotation annotationType(String annotationType) {
        this.annotationType = annotationType;
        return this;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public Integer getPage() {
        return page;
    }

    public Annotation page(Integer page) {
        this.page = page;
        return this;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Annotation comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Annotation addComments(Comment comment) {
        this.comments.add(comment);
        comment.setAnnotation(this);
        return this;
    }

    public Annotation removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setAnnotation(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Annotation tags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Annotation addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Annotation removeTag(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

    public Set<Rectangle> getRectangles() {
        return rectangles;
    }

    public Annotation rectangles(Set<Rectangle> rectangles) {
        this.rectangles = rectangles;
        return this;
    }

    public Annotation addRectangles(Rectangle rectangle) {
        this.rectangles.add(rectangle);
        rectangle.setAnnotation(this);
        return this;
    }

    public Annotation removeRectangles(Rectangle rectangle) {
        this.rectangles.remove(rectangle);
        rectangle.setAnnotation(null);
        return this;
    }

    public void setRectangles(Set<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    public AnnotationSet getAnnotationSet() {
        return annotationSet;
    }

    public Annotation annotationSet(AnnotationSet annotationSet) {
        this.annotationSet = annotationSet;
        return this;
    }

    public void setAnnotationSet(AnnotationSet annotationSet) {
        this.annotationSet = annotationSet;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Annotation annotation = (Annotation) o;
        if (annotation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), annotation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Annotation{" +
            "id=" + id +
            ", annotationType=" + annotationType +
            ", page=" + page +
            ", color='" + color + '\'' +
            ", comments=" + comments +
            ", tags=" + tags +
            ", rectangles=" + rectangles +
            ", annotationSet=" + annotationSet +
            '}';
    }
}
