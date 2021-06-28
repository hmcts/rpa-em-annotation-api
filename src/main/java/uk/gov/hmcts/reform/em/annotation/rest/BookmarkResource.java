package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.config.Constants;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.rest.util.PaginationUtil;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.DeleteBookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.util.StringUtilities;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing Bookmarks.
 */
@RestController
@RequestMapping("/api")
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    private static final String ENTITY_NAME = "bookmark";

    private final BookmarkService bookmarkService;

    public BookmarkResource(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /bookmarks : Create a new bookmark.
     *
     * @param bookmarkDTO the bookmarkDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookmarkDTO, or with status 400 (Bad
     * Request) if the bookmark has an invalid ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @ApiOperation(value = "Create an bookmarkDTO", notes = "A POST request to create an bookmarkDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully created", response = BookmarkDTO.class),
        @ApiResponse(code = 400, message = "bookmarkDTO not valid, invalid id"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    @PostMapping("/bookmarks")
    public ResponseEntity<BookmarkDTO> createBookmark(@RequestBody BookmarkDTO bookmarkDTO) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BookmarkDTO result = bookmarkService.save(bookmarkDTO);
        return ResponseEntity.created(new URI("/api/bookmarks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks : Updates an existing bookmark.
     *
     * @param bookmarkDTO the bookmarkDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmarkDTO, or with status 400 (Bad
     * Request) if the bookmarkDTO is not valid, or with status 500 (Internal Server Error) if the bookmarkDTO couldn't
     * be updated
     */
    @ApiOperation(value = "Update an existing bookmarkDTO", notes = "A PUT request to update an bookmarkDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = BookmarkDTO.class),
        @ApiResponse(code = 400, message = "bookmarkDTO not valid, invalid id"),
        @ApiResponse(code = 500, message = "bookmarkDTO couldn't be updated"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @PutMapping("/bookmarks")
    public ResponseEntity<BookmarkDTO> updateBookmark(@Valid @RequestBody BookmarkDTO bookmarkDTO) {
        log.debug("REST request to update Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BookmarkDTO result = bookmarkService.save(bookmarkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookmarkDTO.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks_multiple : Updates multiple existing bookmarks.
     *
     * @param bookmarkDTOList the list of bookmarkDTO objects to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmarkDTO objects, or with status 400
     * (Bad Request) if the bookmarkDTO objects are not valid, or with status 500 (Internal Server Error) if the
     * bookmarkDTO objects couldn't be updated
     */
    @ApiOperation(value = "Update multiple existing bookmarkDTO objects", notes = "A PUT request to update multiple bookmarkDTO objects")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = BookmarkDTO.class),
        @ApiResponse(code = 400, message = "bookmarkDTO objects not valid, invalid id"),
        @ApiResponse(code = 500, message = "bookmarkDTO objects couldn't be updated"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @PutMapping("/bookmarks_multiple")
    public ResponseEntity<List<BookmarkDTO>> updateMultipleBookmarks(
        @Valid @RequestBody List<BookmarkDTO> bookmarkDTOList) {

        if (bookmarkDTOList.stream().anyMatch(bookmark -> bookmark.getId() == null)) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        List<UUID> sanitisedList = StringUtilities.convertValidLogUUID(bookmarkDTOList.stream()
            .map(BookmarkDTO::getId)
            .collect(Collectors.toList()));

        log.debug("REST request to update list of Bookmark objects : {}", sanitisedList);

        List<BookmarkDTO> result = bookmarkDTOList.stream()
            .map(bookmarkService::save)
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME,sanitisedList.toString()))
            .body(result);
    }

    /**
     * GET  /bookmarks/:documentId : get all the bookmarks for a specific document.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarks in body
     */
    @ApiOperation(value = "Get all bookmarks for Document ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = BookmarkDTO.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @GetMapping("/{documentId}/bookmarks")
    public ResponseEntity<List<BookmarkDTO>> getAllDocumentBookmarks(@PathVariable UUID documentId, Pageable pageable) {
        log.debug("REST request to get a page of Bookmarks");
        Page<BookmarkDTO> page = bookmarkService.findAllByDocumentId(documentId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookmarks");
        if (page.hasContent()) {
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE  /bookmarks/:id : delete the "id" bookmark.
     *
     * @param id the id of the bookmarkDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @ApiOperation(value = "Delete a BookmarkDTO", notes = "A DELETE request to delete a BookmarkDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable UUID id) {
        log.debug("REST request to delete Bookmark : {}", id);
        try {
            bookmarkService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
                .build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE  /bookmarks : delete multiple bookmarks.
     *
     * @param deleteBookmarkDTO object containing the list of bookmarkDTO objects to delete and parent to update
     * @return the ResponseEntity with status 200 (OK), or with status 400 (Bad Request) if the bookmarkDTO objects are
     * not valid, or with status 500 (Internal Server Error) if the bookmarkDTO objects couldn't be deleted
     */
    @ApiOperation(value = "Delete multiple existing bookmarkDTO objects", notes = "A DELETE request to delete multiple bookmarkDTO objects")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @DeleteMapping("/bookmarks_multiple")
    public ResponseEntity<Void> deleteMultipleBookmarks(@Valid @RequestBody DeleteBookmarkDTO deleteBookmarkDTO) {

        if (deleteBookmarkDTO.getDeleted().stream().anyMatch(Objects::isNull)) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        List<UUID> sanitisedList = StringUtilities.convertValidLogUUID(deleteBookmarkDTO.getDeleted());

        log.debug("REST request to delete list of Bookmark objects : {}", sanitisedList);

        Optional<UUID> idToBeDeleted = Optional.empty();
        try {
            for (UUID id : deleteBookmarkDTO.getDeleted()) {
                idToBeDeleted = Optional.ofNullable(id);
                bookmarkService.delete(id);
            }
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            if (idToBeDeleted.isPresent()) {
                log.debug("The Delete ID is not Found : {}", idToBeDeleted.get());
            }
            return ResponseEntity
                .notFound()
                .build();
        }

        if (!Objects.isNull(deleteBookmarkDTO.getUpdated())) {
            bookmarkService.save(deleteBookmarkDTO.getUpdated());
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert(
                ENTITY_NAME, sanitisedList.toString()))
            .build();
    }
}
