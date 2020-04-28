package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.TagMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Tag.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    /**
     * Get all tags belonging to a particular user.
     *
     * @param createdBy the user who owns the tags
     * @return a list of tags belonging to the required user
     */
    @Override
    public List<TagDTO> findTagByCreatedBy(String createdBy) {
        return tagRepository.findTagByCreatedBy(createdBy)
                .stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Persist tag entity in the database
     *
     * @param tag the tag to be persisted to the database
     */
    @Override
    public void persistTag(Tag tag) {
        tagRepository.saveAndFlush(tag);
    }
}
