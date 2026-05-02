package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_whenValidInput_thenReturnsOkAndNoFileMediaVm() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        MediaPostVm mediaPostVm = new MediaPostVm("test caption", file, "test.jpg");
        Media media = new Media();
        media.setId(1L);
        media.setCaption("test caption");
        media.setFileName("test.jpg");
        media.setMediaType("image/jpeg");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        NoFileMediaVm body = (NoFileMediaVm) response.getBody();
        assertEquals(1L, body.id());
        assertEquals("test caption", body.caption());
        assertEquals("test.jpg", body.fileName());
        assertEquals("image/jpeg", body.mediaType());
    }

    @Test
    void delete_whenValidId_thenReturnsNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void get_whenValidId_thenReturnsOkAndMediaVm() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "fileName.jpg", "image/jpeg", "http://localhost/media/1");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    void get_whenInvalidId_thenReturnsNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByIds_whenValidIds_thenReturnsOkAndMediaVms() {
        List<Long> ids = List.of(1L, 2L);
        MediaVm mediaVm1 = new MediaVm(1L, "caption1", "fileName1.jpg", "image/jpeg", "http://localhost/media/1");
        MediaVm mediaVm2 = new MediaVm(2L, "caption2", "fileName2.jpg", "image/jpeg", "http://localhost/media/2");
        when(mediaService.getMediaByIds(ids)).thenReturn(List.of(mediaVm1, mediaVm2));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getByIds_whenIdsNotFound_thenReturnsNotFound() {
        List<Long> ids = List.of(1L, 2L);
        when(mediaService.getMediaByIds(ids)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFile_whenValidIdAndFileName_thenReturnsFile() throws Exception {
        InputStream is = new ByteArrayInputStream("test data".getBytes());
        MediaDto mediaDto = MediaDto.builder()
                .content(is)
                .mediaType(MediaType.IMAGE_JPEG)
                .build();
        when(mediaService.getFile(1L, "fileName.jpg")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "fileName.jpg");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"fileName.jpg\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(is, ((InputStreamResource) response.getBody()).getInputStream());
    }
}
