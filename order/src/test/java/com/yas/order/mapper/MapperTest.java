package com.yas.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
class MapperTest {

    private final CheckoutMapper checkoutMapper = new CheckoutMapperImpl();
    private final OrderMapper orderMapper = new OrderMapperImpl();

    @Test
    void testCheckoutMapper_Nulls() {
        assertNull(checkoutMapper.toModel((com.yas.order.viewmodel.checkout.CheckoutItemPostVm) null));
        assertNull(checkoutMapper.toModel((com.yas.order.viewmodel.checkout.CheckoutPostVm) null));
        assertNull(checkoutMapper.toVm((com.yas.order.model.CheckoutItem) null));
        assertNull(checkoutMapper.toVm((com.yas.order.model.Checkout) null));
        
        assertThat(checkoutMapper.map(null)).isEqualTo(BigDecimal.ZERO);
        assertThat(checkoutMapper.map(BigDecimal.TEN)).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void testOrderMapper_Nulls() {
        assertNull(orderMapper.toCsv(null));
    }
}
