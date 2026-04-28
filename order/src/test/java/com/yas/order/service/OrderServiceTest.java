package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        OrderAddress address = OrderAddress.builder()
                .id(1L)
                .phone("123")
                .contactName("Name")
                .addressLine1("Line1")
                .build();
        
        order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .shippingAddressId(address)
                .billingAddressId(address)
                .build();
        orderItem = OrderItem.builder()
                .id(1L)
                .orderId(1L)
                .productId(1L)
                .productName("Product 1")
                .quantity(2)
                .productPrice(BigDecimal.TEN)
                .build();
    }

    @Test
    void createOrder_shouldReturnOrderVm() {
        OrderAddressPostVm billing = new OrderAddressPostVm("123", "Name", "Line1", "Line2", "City", "Zip", 1L, "District", 1L, "State", 1L, "Country");
        OrderAddressPostVm shipping = new OrderAddressPostVm("123", "Name", "Line1", "Line2", "City", "Zip", 1L, "District", 1L, "State", 1L, "Country");
        OrderItemPostVm itemPostVm = new OrderItemPostVm(1L, "Product 1", 2, BigDecimal.TEN, "Note", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        OrderPostVm orderPostVm = new OrderPostVm("checkout123", "test@example.com", shipping, billing, "Note", 0f, 0f, 1, BigDecimal.TEN, BigDecimal.ZERO, "COUPON", com.yas.order.model.enumeration.DeliveryMethod.YAS_EXPRESS, com.yas.order.model.enumeration.PaymentMethod.COD, PaymentStatus.PENDING, List.of(itemPostVm));

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.saveAll(any())).thenReturn(List.of(orderItem));
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        OrderVm result = orderService.createOrder(orderPostVm);

        assertThat(result).isNotNull();
        verify(productService).subtractProductStockQuantity(any());
        verify(cartService).deleteCartItems(any());
        verify(promotionService).updateUsagePromotion(any());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void getOrderWithItemsById_shouldReturnOrderVm() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result).isNotNull();
        assertEquals(1L, result.id());
    }

    @Test
    void getOrderWithItemsById_shouldThrowNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void getAllOrder_shouldReturnOrderListVm() {
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "Product 1",
                List.of(OrderStatus.PENDING),
                Pair.of("Country", "123"),
                "test@example.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
    }

    @Test
    void getAllOrder_shouldReturnEmpty() {
        Page<Order> page = new PageImpl<>(List.of());
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now()),
                "Product 1",
                List.of(OrderStatus.PENDING),
                Pair.of("Country", "123"),
                "test@example.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).isNull();
    }

    @Test
    void getLatestOrders_shouldReturnList() {
        when(orderRepository.getLatestOrders(any())).thenReturn(List.of(order));
        List<?> result = orderService.getLatestOrders(5);
        assertThat(result).hasSize(1);
    }

    @Test
    void getLatestOrders_whenCountZero_shouldReturnEmptyList() {
        List<?> result = orderService.getLatestOrders(0);
        assertThat(result).isEmpty();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_shouldReturnTrue() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractUserId).thenReturn("user1");
            when(productService.getProductVariations(1L)).thenReturn(List.of());
            when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(order));

            OrderExistsByProductAndUserGetVm result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

            assertThat(result.isPresent()).isTrue();
        }
    }

    @Test
    void getMyOrders_shouldReturnList() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractUserId).thenReturn("user1");
            when(orderRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class))).thenReturn(List.of(order));

            List<OrderGetVm> result = orderService.getMyOrders("Product 1", OrderStatus.PENDING);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void findOrderVmByCheckoutId_shouldReturnOrderGetVm() {
        when(orderRepository.findByCheckoutId("checkout123")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout123");

        assertThat(result).isNotNull();
    }

    @Test
    void findOrderByCheckoutId_shouldThrowNotFoundException() {
        when(orderRepository.findByCheckoutId("checkout123")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("checkout123"));
    }

    @Test
    void updateOrderPaymentStatus_shouldReturnUpdatedVm() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(1L, "PENDING", 2L, "COMPLETED");
        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(result).isNotNull();
        assertEquals("PAID", result.orderStatus());
    }

    @Test
    void rejectOrder_shouldSetStatusToReject() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.rejectOrder(1L, "Reason");
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertEquals("Reason", order.getRejectReason());
    }

    @Test
    void acceptOrder_shouldSetStatusToAccepted() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.acceptOrder(1L);
        verify(orderRepository).save(order);
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }
}
