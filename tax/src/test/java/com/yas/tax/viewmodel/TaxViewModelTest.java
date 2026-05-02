package com.yas.tax.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.viewmodel.error.ErrorVm;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaxViewModelTest {

    @Test
    void taxClassPostVmShouldConvertToModel() {
        TaxClass model = new TaxClassPostVm("ignored", "Standard").toModel();

        assertThat(model.getName()).isEqualTo("Standard");
    }

    @Test
    void taxClassVmShouldMapFromModel() {
        TaxClass taxClass = TaxClass.builder().id(1L).name("Reduced").build();

        assertThat(TaxClassVm.fromModel(taxClass)).isEqualTo(new TaxClassVm(1L, "Reduced"));
    }

    @Test
    void taxRateVmShouldMapFromModel() {
        TaxClass taxClass = TaxClass.builder().id(2L).name("Standard").build();
        TaxRate taxRate = TaxRate.builder()
            .id(3L)
            .rate(8.0)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(4L)
            .countryId(5L)
            .build();

        assertThat(TaxRateVm.fromModel(taxRate)).isEqualTo(new TaxRateVm(3L, 8.0, "70000", 2L, 4L, 5L));
    }

    @Test
    void recordsShouldExposeStoredValues() {
        TaxRatePostVm taxRatePostVm = new TaxRatePostVm(10.5, "12345", 1L, 2L, 3L);
        TaxClassListGetVm taxClassListGetVm = new TaxClassListGetVm(List.of(new TaxClassVm(1L, "A")), 0, 10, 1, 1, true);
        TaxRateGetDetailVm taxRateGetDetailVm = new TaxRateGetDetailVm(1L, 9.0, "70000", "Standard", "HCM", "VN");
        TaxRateListGetVm taxRateListGetVm = new TaxRateListGetVm(List.of(taxRateGetDetailVm), 0, 10, 1, 1, true);
        StateOrProvinceAndCountryGetNameVm locationVm = new StateOrProvinceAndCountryGetNameVm(2L, "HCM", "VN");

        assertThat(taxRatePostVm.rate()).isEqualTo(10.5);
        assertThat(taxClassListGetVm.taxClassContent()).hasSize(1);
        assertThat(taxRateListGetVm.taxRateGetDetailContent()).containsExactly(taxRateGetDetailVm);
        assertThat(locationVm.countryName()).isEqualTo("VN");
    }

    @Test
    void errorVmThreeArgConstructorShouldCreateEmptyFieldErrors() {
        ErrorVm errorVm = new ErrorVm("400", "Bad request", "Validation failed");

        assertThat(errorVm.statusCode()).isEqualTo("400");
        assertThat(errorVm.fieldErrors()).isEmpty();
    }
}
