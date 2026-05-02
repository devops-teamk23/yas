package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        when(annotation.message()).thenReturn("Invalid file type");

        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString())).thenReturn(builder);
    }

    @Test
    void isValid_whenFileIsNull_thenReturnsFalse() {
        boolean result = validator.isValid(null, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnsFalse() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(null);

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeNotAllowed_thenReturnsFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xml", "application/xml", "<xml/>".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeAllowedButInvalidImage_thenReturnsFalse() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpeg", "image/jpeg", "invalid image data".getBytes());

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }

    @Test
    void isValid_whenContentTypeAllowedAndValidImage_thenReturnsTrue() throws IOException {
        // Create a real valid 1x1 image in memory
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "test.jpeg", "image/jpeg", imageBytes);

        boolean result = validator.isValid(file, context);
        assertTrue(result);
    }

    @Test
    void isValid_whenIOExceptionThrownReadImage_thenReturnsFalse() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));

        boolean result = validator.isValid(file, context);
        assertFalse(result);
    }
}
