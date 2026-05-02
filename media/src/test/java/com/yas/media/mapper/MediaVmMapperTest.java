package com.yas.media.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class MediaVmMapperTest {

    private MediaVmMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MediaVmMapper.class);
    }

    @Test
    void toVm_whenMediaIsNull_thenReturnsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toVm_whenMediaIsValid_thenReturnsMappedVm() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("test caption");
        media.setFileName("test.jpg");
        media.setMediaType("image/jpeg");

        MediaVm vm = mapper.toVm(media);

        assertEquals(media.getId(), vm.getId());
        assertEquals(media.getCaption(), vm.getCaption());
        assertEquals(media.getFileName(), vm.getFileName());
        assertEquals(media.getMediaType(), vm.getMediaType());
    }

    @Test
    void toModel_whenMediaVmIsNull_thenReturnsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_whenMediaVmIsValid_thenReturnsMappedModel() {
        MediaVm vm = new MediaVm(2L, "caption", "file.png", "image/png", "url");

        Media media = mapper.toModel(vm);

        assertEquals(vm.getId(), media.getId());
        assertEquals(vm.getCaption(), media.getCaption());
        assertEquals(vm.getFileName(), media.getFileName());
        assertEquals(vm.getMediaType(), media.getMediaType());
    }

    @Test
    void partialUpdate_whenMediaVmIsNull_thenDoesNothing() {
        Media media = new Media();
        media.setCaption("Original");

        mapper.partialUpdate(media, null);

        assertEquals("Original", media.getCaption());
    }

    @Test
    void partialUpdate_whenMediaVmHasNullFields_thenOnlyUpdatesNonNullFields() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Old Caption");
        media.setFileName("Old File");
        media.setMediaType("image/jpeg");

        // MediaVm with some null fields
        MediaVm vm = new MediaVm(2L, null, "New File", null, "url");

        mapper.partialUpdate(media, vm);

        assertEquals(2L, media.getId());
        assertEquals("Old Caption", media.getCaption()); // Unchanged
        assertEquals("New File", media.getFileName());
        assertEquals("image/jpeg", media.getMediaType()); // Unchanged
    }
}
