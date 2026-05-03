package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class StockServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private WarehouseService warehouseService;
    private StockHistoryService stockHistoryService;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        warehouseRepository = Mockito.mock(WarehouseRepository.class);
        stockRepository = Mockito.mock(StockRepository.class);
        productService = Mockito.mock(ProductService.class);
        warehouseService = Mockito.mock(WarehouseService.class);
        stockHistoryService = Mockito.mock(StockHistoryService.class);
        stockService = new StockService(warehouseRepository, stockRepository, productService, warehouseService, stockHistoryService);
    }

    @Test
    void testAddProductIntoWarehouse_whenStockExists_thenThrowStockExistingException() {
        StockPostVm postVm = new StockPostVm(1L, 10L);
        when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void testAddProductIntoWarehouse_whenProductMissing_thenThrowNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 10L);
        when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void testAddProductIntoWarehouse_whenWarehouseMissing_thenThrowNotFoundException() {
        StockPostVm postVm = new StockPostVm(1L, 10L);
        when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(new ProductInfoVm(1L, "P", "SKU", true));
        when(warehouseRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(postVm)));
    }

    @Test
    void testAddProductIntoWarehouse_whenValid_thenSaveStocks() {
        StockPostVm postVm = new StockPostVm(1L, 10L);
        Warehouse warehouse = Warehouse.builder().id(10L).name("WH").addressId(100L).build();

        when(stockRepository.existsByWarehouseIdAndProductId(10L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(new ProductInfoVm(1L, "P", "SKU", true));
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouse));

        stockService.addProductIntoWarehouse(List.of(postVm));

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).saveAll(captor.capture());
        List<Stock> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getProductId()).isEqualTo(1L);
        assertThat(saved.getFirst().getWarehouse().getId()).isEqualTo(10L);
        assertThat(saved.getFirst().getQuantity()).isEqualTo(0L);
        assertThat(saved.getFirst().getReservedQuantity()).isEqualTo(0L);
    }

    @Test
    void testGetStocksByWarehouseIdAndProductNameAndSku_returnsMappedStocks() {
        ProductInfoVm product = new ProductInfoVm(1L, "Product A", "SKU-A", true);
        Warehouse warehouse = Warehouse.builder().id(10L).name("WH").addressId(100L).build();
        Stock stock = Stock.builder().id(100L).productId(1L).warehouse(warehouse).quantity(9L).reservedQuantity(2L).build();

        when(warehouseService.getProductWarehouse(10L, "Product A", "SKU-A", com.yas.inventory.model.enumeration.FilterExistInWhSelection.YES))
            .thenReturn(List.of(product));
        when(stockRepository.findByWarehouseIdAndProductIdIn(10L, List.of(1L))).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(10L, "Product A", "SKU-A");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(100L);
        assertThat(result.getFirst().productName()).isEqualTo("Product A");
        assertThat(result.getFirst().warehouseId()).isEqualTo(10L);
    }

    @Test
    void testUpdateProductQuantityInStock_updatesStockAndPropagatesToHistoryAndProduct() {
        Warehouse warehouse = Warehouse.builder().id(10L).name("WH").addressId(100L).build();
        Stock stock = Stock.builder().id(1L).productId(11L).warehouse(warehouse).quantity(5L).reservedQuantity(0L).build();
        StockQuantityVm quantityVm = new StockQuantityVm(1L, 3L, "add");
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(1L))).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(request);

        assertThat(stock.getQuantity()).isEqualTo(8L);
        verify(stockRepository).saveAll(List.of(stock));
        verify(stockHistoryService).createStockHistories(List.of(stock), List.of(quantityVm));
        verify(productService).updateProductQuantity(any());
    }

    @Test
    void testUpdateProductQuantityInStock_whenNoStocks_thenDoNotUpdateProductQuantity() {
        StockQuantityVm quantityVm = new StockQuantityVm(999L, 1L, "add");
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(List.of(quantityVm));

        when(stockRepository.findAllById(List.of(999L))).thenReturn(List.of());

        stockService.updateProductQuantityInStock(request);

        verify(stockRepository).saveAll(List.of());
        verify(stockHistoryService).createStockHistories(List.of(), List.of(quantityVm));
        verify(productService, Mockito.never()).updateProductQuantity(any());
    }
}
