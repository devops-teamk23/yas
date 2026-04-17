package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TaxRateService.class)
@DisplayName("TaxRateService Tests")
public class TaxRateServiceTest {

    @MockitoBean
    private TaxRateRepository taxRateRepository;

    @MockitoBean
    private TaxClassRepository taxClassRepository;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private TaxRateService taxRateService;

    private TaxRate taxRate;
    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.of(TaxClass.class)
            .set(field("id"), 1L)
            .create();
        taxRate = Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .set(field("id"), 1L)
            .set(field("rate"), 10.0)
            .set(field("stateOrProvinceId"), 100L)
            .set(field("countryId"), 1L)
            .create();
        // Setup default mocks that will be used by multiple tests
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {
        @Test
        @DisplayName("Should return all tax rates")
        void testFindAll_shouldReturnAllTaxRate() {
            // Act
            List<TaxRateVm> result = taxRateService.findAll();
            
            // Assert
            assertThat(result).hasSize(1).contains(TaxRateVm.fromModel(taxRate));
        }

        @Test
        @DisplayName("Should return empty list when no tax rates exist")
        void testFindAll_shouldReturnEmptyList() {
            when(taxRateRepository.findAll()).thenReturn(List.of());
            
            List<TaxRateVm> result = taxRateService.findAll();
            
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {
        @Test
        @DisplayName("Should return tax rate by valid ID")
        void testFindById_shouldReturnTaxRate() {
            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
            
            TaxRateVm result = taxRateService.findById(1L);
            
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(taxRate.getId());
        }

        @Test
        @DisplayName("Should throw NotFoundException when ID not found")
        void testFindById_shouldThrowNotFoundExceptionWhenIdNotFound() {
            when(taxRateRepository.findById(999L)).thenReturn(Optional.empty());
            
            assertThatThrownBy(() -> taxRateService.findById(999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createTaxRate")
    class CreateTaxRateTests {
        @Test
        @DisplayName("Should successfully create tax rate with valid tax class")
        void testCreateTaxRate_shouldSuccessfullyCreate() {
            // Arrange - use explicit taxClassId 1L to match mock setup
            TaxRatePostVm postVm = new TaxRatePostVm(
                10.0, "12345", 100L, 1L, 1L
            );
            when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);
            
            // Act
            TaxRate result = taxRateService.createTaxRate(postVm);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRate()).isEqualTo(10.0);
            verify(taxRateRepository).save(any(TaxRate.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when tax class does not exist")
        void testCreateTaxRate_shouldThrowNotFoundExceptionWhenTaxClassNotFound() {
            // Arrange
            TaxRatePostVm postVm = new TaxRatePostVm(
                10.0, "12345", 100L, 1L, 999L
            );
            lenient().when(taxClassRepository.existsById(999L)).thenReturn(false);
            
            // Act & Assert
            assertThatThrownBy(() -> taxRateService.createTaxRate(postVm))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateTaxRate")
    class UpdateTaxRateTests {
        @Test
        @DisplayName("Should successfully update tax rate with valid data")
        void testUpdateTaxRate_shouldSuccessfullyUpdate() {
            // Arrange - use explicit taxClassId 1L to match mock setup
            TaxRatePostVm postVm = new TaxRatePostVm(
                15.0, "54321", 200L, 1L, 1L
            );
            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
            
            // Act
            taxRateService.updateTaxRate(postVm, 1L);
            
            // Assert
            verify(taxRateRepository).save(any(TaxRate.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when tax rate not found")
        void testUpdateTaxRate_shouldThrowNotFoundExceptionWhenTaxRateNotFound() {
            // Arrange
            TaxRatePostVm postVm = new TaxRatePostVm(
                15.0, "54321", 200L, 1L, taxClass.getId()
            );
            when(taxRateRepository.findById(999L)).thenReturn(Optional.empty());
            
            // Act & Assert
            assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 999L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Should throw NotFoundException when new tax class not found during update")
        void testUpdateTaxRate_shouldThrowNotFoundExceptionWhenNewTaxClassNotFound() {
            // Arrange
            TaxRatePostVm postVm = new TaxRatePostVm(
                15.0, "54321", 200L, 1L, 999L
            );
            when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
            lenient().when(taxClassRepository.existsById(999L)).thenReturn(false);
            
            // Act & Assert
            assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {
        @Test
        @DisplayName("Should successfully delete tax rate")
        void testDelete_shouldSuccessfullyDelete() {
            when(taxRateRepository.existsById(1L)).thenReturn(true);
            
            taxRateService.delete(1L);
            
            verify(taxRateRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when tax rate not found")
        void testDelete_shouldThrowNotFoundExceptionWhenNotFound() {
            when(taxRateRepository.existsById(999L)).thenReturn(false);
            
            assertThatThrownBy(() -> taxRateService.delete(999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getTaxPercent")
    class GetTaxPercentTests {
        @Test
        @DisplayName("Should return tax percent when found")
        void testGetTaxPercent_shouldReturnTaxPercentWhenFound() {
            // Service signature: getTaxPercent(taxClassId, countryId, stateOrProvinceId, zipCode)
            // Repository is called with: getTaxPercent(countryId, stateOrProvinceId, zipCode, taxClassId)
            when(taxRateRepository.getTaxPercent(100L, 1L, "12345", 1L))
                .thenReturn(10.0);
            
            Double result = taxRateService.getTaxPercent(1L, 100L, 1L, "12345");
            
            assertThat(result).isEqualTo(10.0);
        }

        @Test
        @DisplayName("Should return 0 when tax percent not found")
        void testGetTaxPercent_shouldReturnZeroWhenNotFound() {
            when(taxRateRepository.getTaxPercent(100L, 1L, "12345", 1L))
                .thenReturn(null);
            
            Double result = taxRateService.getTaxPercent(1L, 100L, 1L, "12345");
            
            assertThat(result).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("getPageableTaxRates")
    class GetPageableTaxRatesTests {
        @Test
        @DisplayName("Should return paginated tax rates with location details")
        void testGetPageableTaxRates_shouldReturnPaginatedResults() {
            // Arrange
            Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 10), 1);
            when(taxRateRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
            
            StateOrProvinceAndCountryGetNameVm locationVm = new StateOrProvinceAndCountryGetNameVm(
                100L, "California", "United States"
            );
            when(locationService.getStateOrProvinceAndCountryNames(anyList()))
                .thenReturn(List.of(locationVm));
            
            // Act
            TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);
            
            // Assert
            assertThat(result).isNotNull();
            assertThat(result.taxRateGetDetailContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should return empty page when no tax rates exist")
        void testGetPageableTaxRates_shouldReturnEmptyPageWhenNoResults() {
            // Arrange
            Page<TaxRate> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(taxRateRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(emptyPage);
            
            // Act
            TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);
            
            // Assert
            assertThat(result.taxRateGetDetailContent()).isEmpty();
        }
    }
}
