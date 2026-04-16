package com.yas.recommendation.kafka.consumer;

import com.yas.commonlibrary.kafka.cdc.message.Operation;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductSyncService class.
 * Tests cover all CDC operations: CREATE, READ, UPDATE, DELETE operations
 * and edge cases like null messages.
 */
@DisplayName("ProductSyncService Unit Tests")
@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    private ProductSyncService productSyncService;

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @BeforeEach
    void setUp() {
        productSyncService = new ProductSyncService(productVectorSyncService);
    }

    @Test
    @DisplayName("Should delete product vector when CDC message is null (hard delete)")
    void testSync_HardDelete_NullMessage() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(123L);

        // Act
        productSyncService.sync(key, null);

        // Assert
        verify(productVectorSyncService).deleteProductVector(123L);
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should delete product vector when CDC operation is DELETE")
    void testSync_DeleteOperation() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(456L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.DELETE);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).deleteProductVector(456L);
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should create product vector when CDC operation is CREATE")
    void testSync_CreateOperation() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(789L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.CREATE);
        Object mockProduct = new Object();
        cdcMessage.setAfter(mockProduct);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).createProductVector(any());
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should create product vector when CDC operation is READ")
    void testSync_ReadOperation() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(101L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.READ);
        Object mockProduct = new Object();
        cdcMessage.setAfter(mockProduct);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).createProductVector(any());
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should update product vector when CDC operation is UPDATE")
    void testSync_UpdateOperation() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(202L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.UPDATE);
        Object mockProduct = new Object();
        cdcMessage.setAfter(mockProduct);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).updateProductVector(any());
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should handle null 'after' field in CDC message")
    void testSync_NullAfterField() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(303L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.CREATE);
        cdcMessage.setAfter(null);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verifyNoInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should handle delete operation with null 'after' field")
    void testSync_DeleteWithNullAfter() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(505L);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.DELETE);
        cdcMessage.setAfter(null);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).deleteProductVector(505L);
        verifyNoMoreInteractions(productVectorSyncService);
    }

    @Test
    @DisplayName("Should process multiple operations sequentially")
    void testSync_MultipleOperations() {
        // Test CREATE
        ProductMsgKey createKey = new ProductMsgKey();
        createKey.setId(1L);
        ProductCdcMessage createMessage = new ProductCdcMessage();
        createMessage.setOp(Operation.CREATE);
        createMessage.setAfter(new Object());
        productSyncService.sync(createKey, createMessage);

        // Test UPDATE
        ProductMsgKey updateKey = new ProductMsgKey();
        updateKey.setId(2L);
        ProductCdcMessage updateMessage = new ProductCdcMessage();
        updateMessage.setOp(Operation.UPDATE);
        updateMessage.setAfter(new Object());
        productSyncService.sync(updateKey, updateMessage);

        // Test DELETE
        ProductMsgKey deleteKey = new ProductMsgKey();
        deleteKey.setId(3L);
        ProductCdcMessage deleteMessage = new ProductCdcMessage();
        deleteMessage.setOp(Operation.DELETE);
        productSyncService.sync(deleteKey, deleteMessage);

        // Assert
        verify(productVectorSyncService).createProductVector(any());
        verify(productVectorSyncService).updateProductVector(any());
        verify(productVectorSyncService).deleteProductVector(3L);
    }

    @Test
    @DisplayName("Should correctly identify hard delete event")
    void testSync_IdentifyHardDeleteEvent() {
        // Test case 1: Null message
        ProductMsgKey key1 = new ProductMsgKey();
        key1.setId(600L);
        productSyncService.sync(key1, null);
        verify(productVectorSyncService).deleteProductVector(600L);

        reset(productVectorSyncService);

        // Test case 2: DELETE operation
        ProductMsgKey key2 = new ProductMsgKey();
        key2.setId(601L);
        ProductCdcMessage msg = new ProductCdcMessage();
        msg.setOp(Operation.DELETE);
        productSyncService.sync(key2, msg);
        verify(productVectorSyncService).deleteProductVector(601L);
    }

    @Test
    @DisplayName("Should handle large product IDs")
    void testSync_LargeProductId() {
        // Arrange
        ProductMsgKey key = new ProductMsgKey();
        key.setId(Long.MAX_VALUE);
        ProductCdcMessage cdcMessage = new ProductCdcMessage();
        cdcMessage.setOp(Operation.DELETE);

        // Act
        productSyncService.sync(key, cdcMessage);

        // Assert
        verify(productVectorSyncService).deleteProductVector(Long.MAX_VALUE);
    }
}
