package uk.gov.hmcts.reform.em.annotation.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

/**
 * REST controller for managing Rectangle.
 */
@RestController
@RequestMapping("/api")
public class RectangleResource {

    private final Logger log = LoggerFactory.getLogger(RectangleResource.class);

    private static final String ENTITY_NAME = "rectangle";

    @Autowired
    private RectangleService rectangleService;

    @Autowired
    private AnnotationService annotationService;

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /rectangles : Create a new rectangle.
     *
     * @param rectangleDTO the rectangleDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new rectangleDTO, or with status 400 (Bad
     * Request) if the rectangle has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @ApiOperation(value = "Create a rectangleDTO", notes = "A POST request to create a rectangleDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully created", response = RectangleDTO.class),
        @ApiResponse(code = 400, message = "rectangleDTO not valid, invalid id"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 404, message = "Annotation Id not Found"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    @PostMapping("/rectangles")
    public ResponseEntity<RectangleDTO> createRectangle(@RequestBody RectangleDTO rectangleDTO)
        throws URISyntaxException {
        log.debug("REST request to save Rectangle : {}", rectangleDTO);
        if (Objects.isNull(rectangleDTO.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (Objects.isNull(rectangleDTO.getAnnotationId())) {
            throw new BadRequestAlertException("Invalid Annotation id", ENTITY_NAME, "idnull");
        }
        Optional<AnnotationDTO> annotationDTOOptional
            = annotationService.findOne(rectangleDTO.getAnnotationId());
        if (!annotationDTOOptional.isPresent() || Objects.isNull(annotationDTOOptional.get().getId())) {
            return ResponseEntity
                .notFound()
                .build();
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.created(new URI("/api/rectangles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rectangles : Updates an existing rectangle.
     *
     * @param rectangleDTO the rectangleDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated rectangleDTO, or with status 400 (Bad
     * Request) if the rectangleDTO is not valid, or with status 500 (Internal Server Error) if the rectangleDTO
     * couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @ApiOperation(value = "Update an existing rectangleDTO", notes = "A PUT request to update a rectangleDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = RectangleDTO.class),
        @ApiResponse(code = 400, message = "rectangleDTO not valid, invalid id"),
        @ApiResponse(code = 500, message = "rectangleDTO couldn't be updated"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @PutMapping("/rectangles")
    public ResponseEntity<RectangleDTO> updateRectangle(@RequestBody RectangleDTO rectangleDTO)
        throws URISyntaxException {
        log.debug("REST request to update Rectangle : {}", rectangleDTO);
        if (rectangleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rectangleDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rectangles : get all the rectangles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of rectangles in body
     */
    @ApiOperation(value = "Get all comments", notes = "A GET request without a body is used to retrieve all comments")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = RectangleDTO.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @GetMapping("/rectangles")
    public ResponseEntity<List<RectangleDTO>> getAllRectangles(Pageable pageable) {
        log.debug("REST request to get a page of Rectangles");
        Page<RectangleDTO> page = rectangleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rectangles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /rectangles/:id : get the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the rectangleDTO, or with status 404 (Not Found)
     */
    @ApiOperation(value = "Get an existing rectangleDTO", notes = "A GET request to retrieve a rectangleDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = RectangleDTO.class),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @GetMapping("/rectangles/{id}")
    public ResponseEntity<RectangleDTO> getRectangle(@PathVariable UUID id) {
        log.debug("REST request to get Rectangle : {}", id);
        Optional<RectangleDTO> rectangleDTO = rectangleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rectangleDTO);
    }

    /**
     * DELETE  /rectangles/:id : delete the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @ApiOperation(value = "Delete a commentDTO", notes = "A DELETE request to delete a commentDTO")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorised"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
    })
    @DeleteMapping("/rectangles/{id}")
    public ResponseEntity<Void> deleteRectangle(@PathVariable UUID id) {
        log.debug("REST request to delete Rectangle : {}", id);
        try {
            rectangleService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
                .build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
