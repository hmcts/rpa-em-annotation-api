package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing AnnotationSet.
 */
@Service
@Transactional
public class AnnotationSetServiceImpl implements AnnotationSetService {

    private final Logger log = LoggerFactory.getLogger(AnnotationSetServiceImpl.class);

    private final AnnotationSetRepository annotationSetRepository;

    private final AnnotationSetMapper annotationSetMapper;

    private final SecurityUtils securityUtils;

    public AnnotationSetServiceImpl(AnnotationSetRepository annotationSetRepository, AnnotationSetMapper annotationSetMapper,
                                    final SecurityUtils securityUtils) {
        this.annotationSetRepository = annotationSetRepository;
        this.annotationSetMapper = annotationSetMapper;
        this.securityUtils = securityUtils;
    }

    /**
     * Save a annotationSet.
     *
     * @param annotationSetDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public AnnotationSetDTO save(AnnotationSetDTO annotationSetDTO) {
        log.debug("Request to save AnnotationSet : {}", annotationSetDTO);
        AnnotationSet annotationSet = annotationSetMapper.toEntity(annotationSetDTO);
        annotationSet = annotationSetRepository.save(annotationSet);
        return annotationSetMapper.toDto(annotationSet);
    }

    /**
     * Get all the annotationSets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AnnotationSetDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AnnotationSets");
        return annotationSetRepository.findAll(pageable)
            .map(annotationSetMapper::toDto);
    }


    /**
     * Get one annotationSet by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AnnotationSetDTO> findOne(UUID id) {
        log.debug("Request to get AnnotationSet : {}", id);
        return annotationSetRepository.findById(id)
            .map(annotationSetMapper::toDto);
    }

    /**
     * Delete the annotationSet by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete AnnotationSet : {}", id);
        annotationSetRepository.deleteById(id);
    }

    @Override
    public Optional<AnnotationSetDTO> findOneByDocumentId(String documentId) {
        return securityUtils.getCurrentUserLogin().flatMap( login ->
            annotationSetRepository.findByDocumentIdAndCreatedBy(documentId, login).map(annotationSetMapper::toDto)
        );
    }

}
