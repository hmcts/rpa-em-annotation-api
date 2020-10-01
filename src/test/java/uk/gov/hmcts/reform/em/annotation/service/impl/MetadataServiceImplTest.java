package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;
import uk.gov.hmcts.reform.em.annotation.service.mapper.MetadataMapper;

import java.util.Optional;
import java.util.UUID;

public class MetadataServiceImplTest {

    @InjectMocks
    private MetadataServiceImpl metadataService;

    @Mock
    private MetadataRepository metadataRepository;

    @Mock
    private MetadataMapper metadataMapper;

    @Mock
    private SecurityUtils securityUtils;

    private UUID documentId =  UUID.randomUUID();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveSuccessCreate() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();
        Mockito.when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        Mockito.when(metadataMapper.toEntity(metadataDto)).thenReturn(metadata);
        Mockito.when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);
        Mockito.when(metadataRepository.save(metadata)).thenReturn(metadata);
        Mockito.when(metadataRepository.findByDocumentId(metadataDto.getDocumentId())).thenReturn(null);

        MetadataDto updatedDto = metadataService.save(metadataDto);

        Mockito.verify(metadataRepository, Mockito.atLeast(1)).save(metadata);
        Mockito.verify(metadataMapper, Mockito.atLeast(1)).toEntity(metadataDto);
        Mockito.verify(metadataMapper, Mockito.atLeast(1)).toDto(metadata);


        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test
    public void testSaveSuccessUpdate() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();
        Mockito.when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        Mockito.when(metadataMapper.toEntity(metadataDto)).thenReturn(metadata);
        Mockito.when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);
        Mockito.when(metadataRepository.save(metadata)).thenReturn(metadata);
        Mockito.when(metadataRepository.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadata);

        MetadataDto updatedDto = metadataService.save(metadataDto);

        Mockito.verify(metadataRepository, Mockito.atLeast(1)).save(metadata);
        Mockito.verify(metadataMapper, Mockito.atLeast(0)).toEntity(metadataDto);
        Mockito.verify(metadataMapper, Mockito.atLeast(1)).toDto(metadata);


        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test(expected = UsernameNotFoundException.class)
    public void testSaveFailure() {

        MetadataDto metadataDto = createMetadataDto();

        metadataService.save(metadataDto);
    }

    @Test
    public void testFindByDocumentIdSuccess() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();


        Mockito.when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        Mockito.when(metadataRepository.findByDocumentId(metadataDto.getDocumentId()))
            .thenReturn(metadata);
        Mockito.when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);

        MetadataDto updatedDto = metadataService.findByDocumentId(metadataDto.getDocumentId());

        Mockito.verify(metadataRepository, Mockito.atLeast(1))
            .findByDocumentId(metadataDto.getDocumentId());

        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());
    }

    private MetadataDto createMetadataDto() {

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setRotationAngle(90);
        metadataDto.setDocumentId(documentId);

        return metadataDto;
    }

    private Metadata createMetadata() {
        Metadata metadata = new Metadata();
        metadata.setRotationAngle(90);
        metadata.setDocumentId(documentId);

        return metadata;
    }
}
