package com.yas.order.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderModelTest {

    @Test
    void testOrder() {
        OrderAddress address = OrderAddress.builder()
                .id(1L)
                .phone("123")
                .contactName("Name")
                .addressLine1("Line1")
                .city("City")
                .zipCode("00000")
                .districtId(1L)
                .districtName("Dist")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();

        Order order = Order.builder()
                .id(1L)
                .email("test@test.com")
                .shippingAddressId(address)
                .billingAddressId(address)
                .note("Note")
                .tax(5.0f)
                .discount(2.0f)
                .numberItem(1)
                .totalPrice(BigDecimal.TEN)
                .deliveryFee(BigDecimal.ONE)
                .couponCode("CODE")
                .build();

        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getEmail()).isEqualTo("test@test.com");
        assertThat(order.getShippingAddressId().getPhone()).isEqualTo("123");
        assertThat(order.getBillingAddressId().getPhone()).isEqualTo("123");
        assertThat(order.getNote()).isEqualTo("Note");
        assertThat(order.getTax()).isEqualTo(5.0f);
        assertThat(order.getDiscount()).isEqualTo(2.0f);
        assertThat(order.getNumberItem()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(order.getDeliveryFee()).isEqualTo(BigDecimal.ONE);
        assertThat(order.getCouponCode()).isEqualTo("CODE");
    }

    @Test
    void testOrderItem() {
        OrderItem item = OrderItem.builder()
                .id(1L)
                .productId(1L)
                .productName("Product")
                .quantity(2)
                .productPrice(BigDecimal.TEN)
                .note("Note")
                .orderId(1L)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .taxPercent(BigDecimal.ZERO)
                .build();

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getProductId()).isEqualTo(1L);
        assertThat(item.getProductName()).isEqualTo("Product");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getProductPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(item.getNote()).isEqualTo("Note");
        assertThat(item.getOrderId()).isEqualTo(1L);
    }

    @Test
    void testCheckoutAndCheckoutItem() {
        CheckoutItem checkoutItem = CheckoutItem.builder()
                .id(1L)
                .productId(1L)
                .productName("Prod")
                .quantity(1)
                .productPrice(BigDecimal.TEN)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .checkout(null)
                .build();

        Checkout checkout = Checkout.builder()
                .id("chk-1")
                .email("email")
                .note("note")
                .promotionCode("code")
                .checkoutItems(List.of(checkoutItem))
                .build();

        assertThat(checkoutItem.getId()).isEqualTo(1L);
        assertThat(checkout.getId()).isEqualTo("chk-1");
        assertThat(checkout.getCheckoutItems()).hasSize(1);
    }
}
