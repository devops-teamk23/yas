package com.yas.order.viewmodel.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderViewModelTest {

    @Test
    void testOrderVmFromModel_WithNullOrderItems() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .shippingAddressId(address)
                .billingAddressId(address)
                .note("note")
                .tax(0f)
                .discount(0f)
                .numberItem(1)
                .totalPrice(BigDecimal.TEN)
                .deliveryFee(BigDecimal.ONE)
                .couponCode("code")
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        OrderVm result = OrderVm.fromModel(order, null);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.orderItemVms()).isNull();
    }

    @Test
    void testOrderVmFromModel_WithOrderItems() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).shippingAddressId(address).billingAddressId(address).build();
        OrderItem item = OrderItem.builder().id(1L).build();

        OrderVm result = OrderVm.fromModel(order, Set.of(item));

        assertThat(result.orderItemVms()).hasSize(1);
    }

    @Test
    void testOrderBriefVmFromModel() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).billingAddressId(address).build();

        OrderBriefVm result = OrderBriefVm.fromModel(order);

        assertThat(result.id()).isEqualTo(1L);
    }
}
