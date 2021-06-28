package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FilterAnnotationSet REST controller.
 *
 * @see FilterAnnotationSet
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class FilterAnnotationSetTest  extends BaseTest {

    @Autowired
    private AnnotationSetMapper annotationSetMapper;

    @MockBean
    private AnnotationSetService annotationSetService;

    @Autowired
    private AnnotationSetRepository annotationSetRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private AnnotationSet annotationSet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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

        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(UUID.randomUUID());
        annotationSet.setDocumentId("Test");
        annotationSet.setCreatedBy("user");
        annotationSet.setId(UUID.randomUUID());

        return annotationSet;
    }

    @Before
    public void initTest() {
        annotationSet = createEntity(em);
    }

    @Test
    @Transactional
    public void testGetAllAnnotationSets() throws Exception {
        annotationSetRepository.saveAndFlush(annotationSet);
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);
        Optional<AnnotationSetDTO> annotationSetDTOs = Optional.of(annotationSetDTO);

        Mockito.when(annotationSetService.findOneByDocumentId("Test")).thenReturn(annotationSetDTOs);

        restLogoutMockMvc.perform(get("/api/annotation-sets/filter?documentId=Test")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(annotationSetDTOs)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testGetNonExistingAnnotationSet() throws Exception {
        Optional<AnnotationSetDTO> annotationSetDTOs = Optional.empty();

        Mockito.when(annotationSetService.findOneByDocumentId("Test")).thenReturn(annotationSetDTOs);

        restLogoutMockMvc.perform(get("/api/annotation-sets/filter?documentId=Test")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(annotationSetDTOs)))
                .andExpect(status().isNotFound());
    }
}
