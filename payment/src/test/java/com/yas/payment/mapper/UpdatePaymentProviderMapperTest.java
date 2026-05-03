package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePaymentProviderMapperTest {

    private UpdatePaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new UpdatePaymentProviderMapperImpl();
    }

    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toVm_shouldReturnUpdatePaymentVm() {
        PaymentProvider model = PaymentProvider.builder()
                .id("1")
                .enabled(true)
                .name("Stripe")
                .configureUrl("http://stripe")
                .landingViewComponentName("StripeComponent")
                .additionalSettings("settings")
                .mediaId(2L)
                .build();

        UpdatePaymentVm vm = mapper.toVm(model);

        assertEquals("1", vm.getId());
        assertTrue(vm.isEnabled());
        assertEquals("Stripe", vm.getName());
        assertEquals("http://stripe", vm.getConfigureUrl());
        assertEquals("StripeComponent", vm.getLandingViewComponentName());
        assertEquals("settings", vm.getAdditionalSettings());
        assertEquals(2L, vm.getMediaId());
    }

    @Test
    void partialUpdate_shouldDoNothing_whenVmIsNull() {
        PaymentProvider model = PaymentProvider.builder().id("1").build();
        mapper.partialUpdate(model, null);
        assertEquals("1", model.getId());
    }

    @Test
    void partialUpdate_shouldUpdateFields_whenVmHasValues() {
        PaymentProvider model = PaymentProvider.builder().id("1").name("old").build();
        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("2");
        vm.setName("new");
        vm.setConfigureUrl("url");
        vm.setLandingViewComponentName("Component");
        vm.setAdditionalSettings("settings");
        vm.setMediaId(2L);
        vm.setEnabled(true);

        mapper.partialUpdate(model, vm);

        assertEquals("2", model.getId());
        assertEquals("new", model.getName());
        assertEquals("url", model.getConfigureUrl());
        assertEquals("Component", model.getLandingViewComponentName());
        assertEquals("settings", model.getAdditionalSettings());
        assertEquals(2L, model.getMediaId());
        assertTrue(model.isEnabled());
    }

    @Test
    void partialUpdate_shouldNotUpdateFields_whenVmHasNullValues() {
        PaymentProvider model = PaymentProvider.builder()
                .id("1")
                .name("old")
                .configureUrl("url")
                .landingViewComponentName("Component")
                .additionalSettings("settings")
                .mediaId(2L)
                .build();
        
        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId(null);
        vm.setName(null);
        vm.setConfigureUrl(null);
        vm.setLandingViewComponentName(null);
        vm.setAdditionalSettings(null);
        vm.setMediaId(null);

        mapper.partialUpdate(model, vm);

        assertEquals("1", model.getId());
        assertEquals("old", model.getName());
        assertEquals("url", model.getConfigureUrl());
        assertEquals("Component", model.getLandingViewComponentName());
        assertEquals("settings", model.getAdditionalSettings());
        assertEquals(2L, model.getMediaId());
    }

    @Test
    void toVmResponse_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVmResponse(null));
    }

    @Test
    void toVmResponse_shouldReturnPaymentProviderVm() {
        PaymentProvider model = PaymentProvider.builder()
                .id("1")
                .name("Stripe")
                .configureUrl("http://stripe")
                .mediaId(2L)
                .build();
        model.setVersion(1);

        PaymentProviderVm vm = mapper.toVmResponse(model);

        assertEquals("1", vm.getId());
        assertEquals("Stripe", vm.getName());
        assertEquals("http://stripe", vm.getConfigureUrl());
        assertEquals(2L, vm.getMediaId());
        assertEquals(1, vm.getVersion());
    }

    @Test
    void toModel_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void toModel_shouldReturnModel() {
        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("1");
        vm.setEnabled(true);
        vm.setName("Stripe");
        vm.setConfigureUrl("http://stripe");
        vm.setLandingViewComponentName("Component");
        vm.setAdditionalSettings("settings");
        vm.setMediaId(2L);

        PaymentProvider model = mapper.toModel(vm);

        assertEquals("1", model.getId());
        assertTrue(model.isEnabled());
        assertEquals("Stripe", model.getName());
        assertEquals("http://stripe", model.getConfigureUrl());
        assertEquals("Component", model.getLandingViewComponentName());
        assertEquals("settings", model.getAdditionalSettings());
        assertEquals(2L, model.getMediaId());
    }
}
