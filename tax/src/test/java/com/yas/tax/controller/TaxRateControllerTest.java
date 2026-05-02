package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TaxRateControllerTest {

    @Mock
    private TaxRateService taxRateService;

    private TaxRateController controller;

    @BeforeEach
    void setUp() {
        controller = new TaxRateController(taxRateService);
    }

    @Test
    void getPageableTaxRatesShouldReturnOkResponse() {
        TaxRateListGetVm body = new TaxRateListGetVm(
            List.of(new TaxRateGetDetailVm(1L, 10.0, "70000", "Standard", "HCM", "VN")),
            0, 10, 1, 1, true);
        when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(body);

        var response = controller.getPageableTaxRates(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(body);
    }

    @Test
    void getTaxRateShouldReturnRequestedItem() {
        TaxRateVm taxRateVm = new TaxRateVm(1L, 10.0, "70000", 2L, 3L, 4L);
        when(taxRateService.findById(1L)).thenReturn(taxRateVm);

        var response = controller.getTaxRate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxRateVm);
    }

    @Test
    void createTaxRateShouldReturnCreatedResponseWithLocation() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "70000", 2L, 3L, 4L);
        TaxClass taxClass = TaxClass.builder().id(2L).name("Standard").build();
        TaxRate created = TaxRate.builder()
            .id(8L)
            .rate(10.0)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(3L)
            .countryId(4L)
            .build();
        when(taxRateService.createTaxRate(postVm)).thenReturn(created);

        var response = controller.createTaxRate(postVm, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("/tax-rates/8");
        assertThat(response.getBody()).isEqualTo(new TaxRateVm(8L, 10.0, "70000", 2L, 3L, 4L));
    }

    @Test
    void updateTaxRateShouldReturnNoContent() {
        TaxRatePostVm postVm = new TaxRatePostVm(12.0, "75000", 2L, 5L, 4L);

        var response = controller.updateTaxRate(5L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).updateTaxRate(postVm, 5L);
    }

    @Test
    void deleteTaxRateShouldReturnNoContent() {
        var response = controller.deleteTaxRate(6L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxRateService).delete(6L);
    }

    @Test
    void getTaxPercentByAddressShouldReturnValue() {
        when(taxRateService.getTaxPercent(1L, 2L, 3L, "70000")).thenReturn(7.5);

        var response = controller.getTaxPercentByAddress(1L, 2L, 3L, "70000");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(7.5);
    }

    @Test
    void getBatchTaxPercentsByAddressShouldReturnValues() {
        List<TaxRateVm> body = List.of(new TaxRateVm(1L, 7.5, "70000", 10L, 3L, 2L));
        when(taxRateService.getBulkTaxRate(List.of(10L), 2L, 3L, "70000")).thenReturn(body);

        var response = controller.getBatchTaxPercentsByAddress(List.of(10L), 2L, 3L, "70000");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(body);
    }
}
