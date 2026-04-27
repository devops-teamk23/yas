package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WarehouseServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = Mockito.mock(WarehouseRepository.class);
        stockRepository = Mockito.mock(StockRepository.class);
        productService = Mockito.mock(ProductService.class);
        locationService = Mockito.mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void testFindAllWarehouses_returnsMappedList() {
        Warehouse first = Warehouse.builder().id(1L).name("W1").addressId(100L).build();
        Warehouse second = Warehouse.builder().id(2L).name("W2").addressId(200L).build();
        when(warehouseRepository.findAll()).thenReturn(List.of(first, second));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().name()).isEqualTo("W1");
    }

    @Test
    void testGetProductWarehouse_whenProductIdsExist_setsExistFlag() {
        when(stockRepository.getProductIdsInWarehouse(10L)).thenReturn(List.of(1L));
        when(productService.filterProducts("P", "SKU", List.of(1L), FilterExistInWhSelection.YES))
            .thenReturn(List.of(
                new ProductInfoVm(1L, "P1", "SKU1", false),
                new ProductInfoVm(2L, "P2", "SKU2", false)
            ));

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(10L, "P", "SKU", FilterExistInWhSelection.YES);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).existInWh()).isTrue();
        assertThat(result.get(1).existInWh()).isFalse();
    }

    @Test
    void testGetProductWarehouse_whenNoProductIds_returnOriginalList() {
        List<ProductInfoVm> products = List.of(new ProductInfoVm(1L, "P1", "SKU1", true));
        when(stockRepository.getProductIdsInWarehouse(10L)).thenReturn(List.of());
        when(productService.filterProducts("P", "SKU", List.of(), FilterExistInWhSelection.NO)).thenReturn(products);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(10L, "P", "SKU", FilterExistInWhSelection.NO);

        assertThat(result).isEqualTo(products);
    }

    @Test
    void testFindById_whenNotFound_thenThrowNotFoundException() {
        when(warehouseRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(11L));
    }

    @Test
    void testFindById_whenFound_thenReturnWarehouseDetail() {
        Warehouse warehouse = Warehouse.builder().id(11L).name("W-11").addressId(300L).build();
        AddressDetailVm address = new AddressDetailVm(
            300L, "John", "0123", "L1", "L2", "City", "70000", 1L,
            "District", 2L, "State", 3L, "Country"
        );
        when(warehouseRepository.findById(11L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(300L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(11L);

        assertThat(result.id()).isEqualTo(11L);
        assertThat(result.name()).isEqualTo("W-11");
        assertThat(result.addressLine2()).isEqualTo("L2");
    }

    @Test
    void testCreate_whenNameDuplicated_thenThrowDuplicatedException() {
        WarehousePostVm vm = createWarehousePostVm("WH");
        when(warehouseRepository.existsByName("WH")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(vm));
    }

    @Test
    void testCreate_whenValid_thenSaveWarehouseWithCreatedAddress() {
        WarehousePostVm vm = createWarehousePostVm("WH");
        when(warehouseRepository.existsByName("WH")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(999L).build());
        when(warehouseRepository.save(org.mockito.ArgumentMatchers.any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Warehouse result = warehouseService.create(vm);

        assertThat(result.getName()).isEqualTo("WH");
        assertThat(result.getAddressId()).isEqualTo(999L);
    }

    @Test
    void testUpdate_whenWarehouseNotFound_thenThrowNotFoundException() {
        when(warehouseRepository.findById(22L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(createWarehousePostVm("NEW"), 22L));
    }

    @Test
    void testUpdate_whenNameDuplicated_thenThrowDuplicatedException() {
        Warehouse existing = Warehouse.builder().id(22L).name("OLD").addressId(500L).build();
        when(warehouseRepository.findById(22L)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("NEW", 22L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(createWarehousePostVm("NEW"), 22L));
    }

    @Test
    void testUpdate_whenValid_thenUpdateAddressAndSaveWarehouse() {
        Warehouse existing = Warehouse.builder().id(22L).name("OLD").addressId(500L).build();
        WarehousePostVm vm = createWarehousePostVm("NEW");
        when(warehouseRepository.findById(22L)).thenReturn(Optional.of(existing));
        when(warehouseRepository.existsByNameWithDifferentId("NEW", 22L)).thenReturn(false);

        warehouseService.update(vm, 22L);

        assertThat(existing.getName()).isEqualTo("NEW");
        verify(locationService).updateAddress(org.mockito.ArgumentMatchers.eq(500L), any());
        verify(warehouseRepository).save(existing);
    }

    @Test
    void testDelete_whenWarehouseNotFound_thenThrowNotFoundException() {
        when(warehouseRepository.findById(33L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.delete(33L));
    }

    @Test
    void testDelete_whenWarehouseFound_thenDeleteWarehouseAndAddress() {
        Warehouse existing = Warehouse.builder().id(33L).name("W").addressId(700L).build();
        when(warehouseRepository.findById(33L)).thenReturn(Optional.of(existing));

        warehouseService.delete(33L);

        verify(warehouseRepository).deleteById(33L);
        verify(locationService).deleteAddress(700L);
    }

    @Test
    void testGetPageableWarehouses_returnsWarehouseListVm() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("WH").addressId(100L).build();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse), PageRequest.of(0, 10), 1);
        when(warehouseRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).hasSize(1);
        assertThat(result.pageNo()).isEqualTo(0);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    private WarehousePostVm createWarehousePostVm(String name) {
        return WarehousePostVm.builder()
            .name(name)
            .contactName("John")
            .phone("0123")
            .addressLine1("L1")
            .addressLine2("L2")
            .city("City")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();
    }
}
