package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class StockHistoryServiceTest {

    private StockHistoryRepository stockHistoryRepository;
    private ProductService productService;
    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryRepository = Mockito.mock(StockHistoryRepository.class);
        productService = Mockito.mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void testCreateStockHistories_savesOnlyMatchedEntries() {
        Warehouse warehouse = Warehouse.builder().id(10L).name("W-1").addressId(100L).build();
        Stock first = Stock.builder().id(1L).productId(101L).warehouse(warehouse).quantity(3L).reservedQuantity(0L).build();
        Stock second = Stock.builder().id(2L).productId(102L).warehouse(warehouse).quantity(4L).reservedQuantity(0L).build();

        StockQuantityVm matched = new StockQuantityVm(1L, 5L, "restock");

        stockHistoryService.createStockHistories(List.of(first, second), List.of(matched));

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());
        List<StockHistory> saved = captor.getValue();

        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getProductId()).isEqualTo(101L);
        assertThat(saved.getFirst().getAdjustedQuantity()).isEqualTo(5L);
        assertThat(saved.getFirst().getNote()).isEqualTo("restock");
    }

    @Test
    void testGetStockHistories_mapsToViewModelList() {
        Warehouse warehouse = Warehouse.builder().id(10L).name("W-1").addressId(100L).build();
        StockHistory history = StockHistory.builder()
            .id(99L)
            .productId(101L)
            .adjustedQuantity(7L)
            .note("note")
            .warehouse(warehouse)
            .build();

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(101L, 10L))
            .thenReturn(List.of(history));
        when(productService.getProduct(101L)).thenReturn(new ProductInfoVm(101L, "Product A", "SKU-A", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(101L, 10L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().id()).isEqualTo(99L);
        assertThat(result.data().getFirst().productName()).isEqualTo("Product A");
        assertThat(result.data().getFirst().adjustedQuantity()).isEqualTo(7L);
    }
}
