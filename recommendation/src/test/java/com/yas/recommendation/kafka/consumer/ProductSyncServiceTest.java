package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @InjectMocks
    private ProductSyncService productSyncService;

    @Test
    void shouldDeleteVectorForHardDeleteEvents() {
        ProductMsgKey key = ProductMsgKey.builder().id(1L).build();

        productSyncService.sync(key, null);
        productSyncService.sync(key, ProductCdcMessage.builder().op(DELETE).build());

        verify(productVectorSyncService, times(2)).deleteProductVector(1L);
    }

    @Test
    void shouldRouteCreateAndUpdateEvents() {
        ProductMsgKey key = ProductMsgKey.builder().id(2L).build();
        Product product = Product.builder().id(2L).isPublished(true).build();

        productSyncService.sync(key, ProductCdcMessage.builder().op(CREATE).after(product).build());
        productSyncService.sync(key, ProductCdcMessage.builder().op(UPDATE).after(product).build());

        verify(productVectorSyncService).createProductVector(product);
        verify(productVectorSyncService).updateProductVector(product);
    }

    @Test
    void shouldIgnoreMessagesWithoutAfterPayload() {
        ProductMsgKey key = ProductMsgKey.builder().id(3L).build();

        productSyncService.sync(key, ProductCdcMessage.builder().op(CREATE).build());

        verifyNoInteractions(productVectorSyncService);
    }
}
