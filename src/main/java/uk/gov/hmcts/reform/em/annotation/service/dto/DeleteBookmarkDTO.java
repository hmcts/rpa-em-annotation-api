package uk.gov.hmcts.reform.em.annotation.service.dto;

import java.util.List;
import java.util.UUID;

public class DeleteBookmarkDTO {

    private BookmarkDTO updated;
    private List<UUID> deleted;

    public BookmarkDTO getUpdated() {
        return updated;
    }

    public void setUpdated(BookmarkDTO updated) {
        this.updated = updated;
    }

    public List<UUID> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<UUID> deleted) {
        this.deleted = deleted;
    }
}
