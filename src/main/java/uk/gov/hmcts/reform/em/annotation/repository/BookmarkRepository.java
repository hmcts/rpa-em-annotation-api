package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;

import java.util.UUID;

/**
 * Spring Data repository for the Bookmark entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Page<Bookmark> findByDocumentIdAndCreatedBy(UUID documentId, String createdBy, Pageable pageable);
}
