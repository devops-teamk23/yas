package com.yas.tax.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class TaxClassControllerTest {

    @Mock
    private TaxClassService taxClassService;

    private TaxClassController controller;

    @BeforeEach
    void setUp() {
        controller = new TaxClassController(taxClassService);
    }

    @Test
    void getPageableTaxClassesShouldReturnOkResponse() {
        TaxClassListGetVm body = new TaxClassListGetVm(List.of(new TaxClassVm(1L, "Standard")), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(body);

        var response = controller.getPageableTaxClasses(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(body);
    }

    @Test
    void listTaxClassesShouldReturnOkResponse() {
        List<TaxClassVm> taxClasses = List.of(new TaxClassVm(1L, "Standard"));
        when(taxClassService.findAllTaxClasses()).thenReturn(taxClasses);

        var response = controller.listTaxClasses();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxClasses);
    }

    @Test
    void getTaxClassShouldReturnRequestedItem() {
        TaxClassVm taxClassVm = new TaxClassVm(1L, "Standard");
        when(taxClassService.findById(1L)).thenReturn(taxClassVm);

        var response = controller.getTaxClass(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taxClassVm);
    }

    @Test
    void createTaxClassShouldReturnCreatedResponseWithLocation() {
        TaxClassPostVm postVm = new TaxClassPostVm("ignored", "Standard");
        TaxClass created = TaxClass.builder().id(7L).name("Standard").build();
        when(taxClassService.create(postVm)).thenReturn(created);

        var response = controller.createTaxClass(postVm, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString("/tax-classes/7");
        assertThat(response.getBody()).isEqualTo(new TaxClassVm(7L, "Standard"));
    }

    @Test
    void updateTaxClassShouldReturnNoContent() {
        TaxClassPostVm postVm = new TaxClassPostVm("ignored", "Updated");

        var response = controller.updateTaxClass(5L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).update(postVm, 5L);
    }

    @Test
    void deleteTaxClassShouldReturnNoContent() {
        var response = controller.deleteTaxClass(9L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taxClassService).delete(9L);
    }
}
