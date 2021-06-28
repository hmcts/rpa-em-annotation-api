package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.Optional;

/**
 * REST controller for managing AnnotationSet.
 */
@RestController
@RequestMapping("/api")
public class FilterAnnotationSet {

    private final Logger log = LoggerFactory.getLogger(FilterAnnotationSet.class);

    private final AnnotationSetService annotationSetService;

    public FilterAnnotationSet(AnnotationSetService annotationSetService) {
        this.annotationSetService = annotationSetService;
    }

    @ApiOperation(value = "Filter an annotationSet", notes = "A GET request to filter an annotationSetDTO")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = AnnotationSetDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @GetMapping("/annotation-sets/filter")
    public ResponseEntity<AnnotationSetDTO> getAllAnnotationSets(@RequestParam("documentId") String documentId) {
        log.debug("REST request to get a page of AnnotationSets");
        Optional<AnnotationSetDTO> optionalAnnotationSetDTO = annotationSetService.findOneByDocumentId(documentId);
        return optionalAnnotationSetDTO
            .map( annotationSetDTO -> ResponseEntity.ok(annotationSetDTO))
            .orElseThrow( () -> new ResourceNotFoundException("Could not find annotation set for this document id#" + documentId) );
    }
}
