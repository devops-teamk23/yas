package com.yas.recommendation.vector.product.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductVectorSyncServiceTest {

    @Mock
    private ProductVectorRepository productVectorRepository;

    @InjectMocks
    private ProductVectorSyncService productVectorSyncService;

    @Test
    void shouldCreateVectorOnlyWhenProductIsPublished() {
        Product publishedProduct = Product.builder().id(10L).isPublished(true).build();
        Product hiddenProduct = Product.builder().id(11L).isPublished(false).build();

        productVectorSyncService.createProductVector(publishedProduct);
        productVectorSyncService.createProductVector(hiddenProduct);

        verify(productVectorRepository).add(10L);
        verify(productVectorRepository, never()).add(11L);
    }

    @Test
    void shouldUpdateOrDeleteVectorDependingOnPublishState() {
        Product publishedProduct = Product.builder().id(20L).isPublished(true).build();
        Product hiddenProduct = Product.builder().id(21L).isPublished(false).build();

        productVectorSyncService.updateProductVector(publishedProduct);
        productVectorSyncService.updateProductVector(hiddenProduct);
        productVectorSyncService.deleteProductVector(22L);

        verify(productVectorRepository).update(20L);
        verify(productVectorRepository).delete(21L);
        verify(productVectorRepository).delete(22L);
    }
}
