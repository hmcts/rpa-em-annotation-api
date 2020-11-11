package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnnotationSetResource REST controller.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class AnnotationSetResourceIntTest extends BaseTest {

    private static final String DEFAULT_DOCUMENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_ID = "BBBBBBBBBB";

    @Autowired
    private AnnotationSetRepository annotationSetRepository;

    @Autowired
    private AnnotationSetMapper annotationSetMapper;
    
    @Autowired
    private AnnotationSetService annotationSetService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private AnnotationSet annotationSet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnnotationSetResource annotationSetResource = new AnnotationSetResource(annotationSetService);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnnotationSet createEntity(EntityManager em) {
        AnnotationSet annotationSet = new AnnotationSet()
            .documentId(DEFAULT_DOCUMENT_ID);

        annotationSet.setId(UUID.randomUUID());
        return annotationSet;
    }

    @Before
    public void initTest() {
        annotationSet = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnnotationSetEmptyUUID() throws Exception {
        int databaseSizeBeforeCreate = annotationSetRepository.findAll().size();

        // Create the AnnotationSet
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);
        annotationSetDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createAnnotationSetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = annotationSetRepository.findAll().size();

        // Create the AnnotationSet with an existing ID
        annotationSet.setId(UUID.randomUUID());
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isCreated());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void getAllAnnotationSets() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        // Get all the annotationSetList
        restLogoutMockMvc.perform(get("/api/annotation-sets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annotationSet.getId().toString())))
            .andExpect(jsonPath("$.[*].documentId").value(hasItem(DEFAULT_DOCUMENT_ID.toString())));
    }
    
    @Test
    @Transactional
    public void getAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        // Get the annotationSet
        restLogoutMockMvc.perform(get("/api/annotation-sets/{id}", annotationSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(annotationSet.getId().toString()))
            .andExpect(jsonPath("$.documentId").value(DEFAULT_DOCUMENT_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAnnotationSet() throws Exception {
        // Get the annotationSet
        restLogoutMockMvc.perform(get("/api/annotation-sets/{id}", UUID.randomUUID()))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void updateAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        int databaseSizeBeforeUpdate = annotationSetRepository.findAll().size();

        // Update the annotationSet
        AnnotationSet updatedAnnotationSet = annotationSetRepository.findById(annotationSet.getId()).get();
        // Disconnect from session so that the updates on updatedAnnotationSet are not directly saved in db
        em.detach(updatedAnnotationSet);
        updatedAnnotationSet
            .documentId(UPDATED_DOCUMENT_ID);
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(updatedAnnotationSet);

        restLogoutMockMvc.perform(put("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isOk());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeUpdate);
        AnnotationSet testAnnotationSet = annotationSetList.get(annotationSetList.size() - 1);
        assertThat(testAnnotationSet.getDocumentId()).isEqualTo(UPDATED_DOCUMENT_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingAnnotationSet() throws Exception {
        int databaseSizeBeforeUpdate = annotationSetRepository.findAll().size();

        // Create the AnnotationSet
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogoutMockMvc.perform(put("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isOk());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        int databaseSizeBeforeDelete = annotationSetRepository.findAll().size();

        // Get the annotationSet
        restLogoutMockMvc.perform(delete("/api/annotation-sets/{id}", annotationSet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnnotationSet.class);
        AnnotationSet annotationSet1 = new AnnotationSet();
        annotationSet1.setId(UUID.randomUUID());
        AnnotationSet annotationSet2 = new AnnotationSet();
        annotationSet2.setId(annotationSet1.getId());
        assertThat(annotationSet1).isEqualTo(annotationSet2);
        annotationSet2.setId(UUID.randomUUID());
        assertThat(annotationSet1).isNotEqualTo(annotationSet2);
        annotationSet1.setId(null);
        assertThat(annotationSet1).isNotEqualTo(annotationSet2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnnotationSetDTO.class);
        AnnotationSetDTO annotationSetDTO1 = new AnnotationSetDTO();
        annotationSetDTO1.setId(UUID.randomUUID());
        AnnotationSetDTO annotationSetDTO2 = new AnnotationSetDTO();
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
        annotationSetDTO2.setId(annotationSetDTO1.getId());
        assertThat(annotationSetDTO1).isEqualTo(annotationSetDTO2);
        annotationSetDTO2.setId(UUID.randomUUID());
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
        annotationSetDTO1.setId(null);
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        UUID id = UUID.randomUUID();
        assertThat(annotationSetMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(annotationSetMapper.fromId(null)).isNull();
    }
}
