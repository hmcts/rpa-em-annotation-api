package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.net.URISyntaxException;
import java.util.UUID;

public class MetaDataResourceTest {

    @InjectMocks
    private MetaDataResource metaDataResource;

    @Mock
    private MetadataService metadataService;

    @Mock
    private WebDataBinder webDataBinder;

    private UUID documentId =  UUID.randomUUID();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createMetaDataSuccess() throws URISyntaxException {

        MetadataDto metadataDto = createMetadataDto();
        Mockito.when(metadataService.save(metadataDto)).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.createMetaData(metadataDto);

        MetadataDto response = responseEntity.getBody();
        Assert.assertEquals(metadataDto.getDocumentId(), response.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), response.getRotationAngle());

        Mockito.verify(metadataService, Mockito.atLeast(1)).save(metadataDto);
    }

    @Test
    public void getMetadataSuccess() {
        MetadataDto metadataDto = createMetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.getMetadata(metadataDto.getDocumentId());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    public void getMetaDataFailure() {

        MetadataDto metadataDto = new MetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.getMetadata(metadataDto.getDocumentId());

        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    public void getMetaDataFailure2() {

        MetadataDto metadataDto = new MetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(null);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.getMetadata(metadataDto.getDocumentId());

        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    public void testInitBinder() {
        metaDataResource.initBinder(webDataBinder);
        Mockito.verify(webDataBinder, Mockito.atLeast(1)).setDisallowedFields(Mockito.anyString());
    }

    private MetadataDto createMetadataDto() {

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setRotationAngle(90);
        metadataDto.setDocumentId(documentId);

        return metadataDto;
    }

}
