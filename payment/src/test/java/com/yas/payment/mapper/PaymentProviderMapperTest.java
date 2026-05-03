package com.yas.payment.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentProviderMapperTest {

    private PaymentProviderMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentProviderMapperImpl();
    }

    @Test
    void toVm_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toVm(null));
    }

    @Test
    void toVm_shouldReturnVm() {
        PaymentProvider model = PaymentProvider.builder()
                .id("1")
                .name("Stripe")
                .configureUrl("http://stripe")
                .mediaId(2L)
                .build();
        model.setVersion(1);

        PaymentProviderVm vm = mapper.toVm(model);

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
        PaymentProviderVm vm = new PaymentProviderVm("1", "Stripe", "http://stripe", 1, 2L, "icon");
        PaymentProvider model = mapper.toModel(vm);

        assertEquals("1", model.getId());
        assertEquals("Stripe", model.getName());
        assertEquals("http://stripe", model.getConfigureUrl());
        assertEquals(2L, model.getMediaId());
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
        PaymentProviderVm vm = new PaymentProviderVm("2", "new", "url", 1, 2L, "icon");
        mapper.partialUpdate(model, vm);

        assertEquals("2", model.getId());
        assertEquals("new", model.getName());
        assertEquals("url", model.getConfigureUrl());
        assertEquals(2L, model.getMediaId());
    }

    @Test
    void partialUpdate_shouldNotUpdateFields_whenVmHasNullValues() {
        PaymentProvider model = PaymentProvider.builder()
                .id("1")
                .name("old")
                .configureUrl("url")
                .mediaId(2L)
                .build();

        PaymentProviderVm vm = new PaymentProviderVm(null, null, null, 1, null, "icon");

        mapper.partialUpdate(model, vm);

        assertEquals("1", model.getId());
        assertEquals("old", model.getName());
        assertEquals("url", model.getConfigureUrl());
        assertEquals(2L, model.getMediaId());
    }
}
