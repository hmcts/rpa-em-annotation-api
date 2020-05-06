package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see BookmarkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BookmarkResourceIntTest extends BaseTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BookmarkMapper bookmarkMapper;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @MockBean
    private SecurityUtils securityUtils;

    private Bookmark bookmark;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookmarkResource bookmarkResource = new BookmarkResource(bookmarkService);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    public static Bookmark createEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(UUID.randomUUID());
        bookmark.setCreatedBy("bob");
        bookmark.setName("My Bookmark");
        bookmark.setPageNumber(426);
        bookmark.setxCoordinate(32.7);
        bookmark.setyCoordinate(100.9);
        return bookmark;
    }

    @Before
    public void initTest() {
        bookmark = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmarkUUIDNull() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Comment
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        bookmarkDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createBookmarkCreatedByNull() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();
        bookmark.setCreatedBy(null);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("fabio"));

        // Create the Comment
        bookmark.setId(UUID.randomUUID());
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        restLogoutMockMvc.perform(post("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void createBookmarkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        bookmark.setId(UUID.randomUUID());
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);

        restLogoutMockMvc.perform(post("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isCreated());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void updateBookmark() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();

        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();

        em.detach(updatedBookmark);
        updatedBookmark
                .setName("Updated Bookmark");
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(updatedBookmark);

        restLogoutMockMvc.perform(put("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
        assertThat(testBookmark.getName()).isEqualTo("Updated Bookmark");
    }

    @Test
    @Transactional
    public void updateNonExistentBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(null);

        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);

        restLogoutMockMvc.perform(put("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isBadRequest());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void getBookmarksByDocumentId() throws Exception {
        bookmark = bookmarkRepository.saveAndFlush(bookmark);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("bob"));

        restLogoutMockMvc.perform(get("/api/" + bookmark.getDocumentId() + "/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(bookmark.getName())));
    }

    @Test
    @Transactional
    public void getBookmarksByDocumentIdNoContent() throws Exception {
        bookmark = bookmarkRepository.saveAndFlush(bookmark);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("fabio"));

        restLogoutMockMvc.perform(get("/api/" + bookmark.getDocumentId() + "/bookmarks"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void deleteBookmark() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        restLogoutMockMvc.perform(delete("/api/bookmarks/{id}", bookmark.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
