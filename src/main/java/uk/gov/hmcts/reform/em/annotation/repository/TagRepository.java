package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;

import java.util.List;
import java.util.UUID;

/**
* Spring Data repository for the Tag entity.
*/
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findTagByCreatedBy(String createdBy);
}
