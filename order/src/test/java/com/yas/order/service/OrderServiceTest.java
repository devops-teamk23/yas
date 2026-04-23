package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.io.IOException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

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

    private MockedStatic<AuthenticationUtils> authenticationUtilsMock;

    @BeforeEach
    void setUp() {
        authenticationUtilsMock = mockStatic(AuthenticationUtils.class);
    }

    @AfterEach
    void tearDown() {
        authenticationUtilsMock.close();
    }

    @Test
    void getOrderWithItemsById_OrderExists_ReturnsOrderVm() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).shippingAddressId(address).billingAddressId(address).build();
        OrderItem orderItem = OrderItem.builder().id(1L).orderId(1L).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(orderItem));

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getOrderWithItemsById_OrderDoesNotExist_ThrowsNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(1L));
    }

    @Test
    void rejectOrder_OrderExists_SetsStatusToReject() {
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Out of stock");

        verify(orderRepository).save(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Out of stock");
    }

    @Test
    void acceptOrder_OrderExists_SetsStatusToAccepted() {
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository).save(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void updateOrderPaymentStatus_OrderExists_UpdatesStatus() {
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
                .orderId(1L)
                .paymentId(10L)
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .build();

        PaymentOrderStatusVm result = orderService.updateOrderPaymentStatus(request);

        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.paymentId()).isEqualTo(10L);
    }

    @Test
    void getMyOrders_ReturnsOrders() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).shippingAddressId(address).billingAddressId(address).build();

        when(orderRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(order));

        var result = orderService.getMyOrders("prod", OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findOrderVmByCheckoutId_OrderExists_ReturnsOrderGetVm() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).checkoutId("checkout-1").shippingAddressId(address).billingAddressId(address).build();
        when(orderRepository.findByCheckoutId("checkout-1")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

        var result = orderService.findOrderVmByCheckoutId("checkout-1");

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void createOrder_Success() {
        OrderAddressPostVm addressPostVm = OrderAddressPostVm.builder()
                .phone("123")
                .contactName("John")
                .addressLine1("Street 1")
                .city("City")
                .zipCode("10000")
                .districtId(1L)
                .districtName("Dist")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State")
                .countryId(1L)
                .countryName("Country")
                .build();

        OrderItemPostVm itemPostVm = OrderItemPostVm.builder()
                .productId(1L)
                .productName("Product")
                .quantity(2)
                .productPrice(BigDecimal.TEN)
                .build();

        OrderPostVm postVm = OrderPostVm.builder()
                .email("test@test.com")
                .shippingAddressPostVm(addressPostVm)
                .billingAddressPostVm(addressPostVm)
                .totalPrice(BigDecimal.valueOf(20))
                .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(itemPostVm))
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(100L);
            OrderAddress addr = OrderAddress.builder().id(1L).build();
            o.setShippingAddressId(addr);
            o.setBillingAddressId(addr);
            return o;
        });

        when(orderRepository.findById(100L)).thenReturn(Optional.of(new Order()));

        OrderVm result = orderService.createOrder(postVm);

        assertThat(result).isNotNull();
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderItemRepository).saveAll(any());
        verify(productService).subtractProductStockQuantity(any());
        verify(cartService).deleteCartItems(any());
        verify(promotionService).updateUsagePromotion(anyList());
    }

    @Test
    void getAllOrder_ReturnsOrderList() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).shippingAddressId(address).billingAddressId(address).build();
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                "prod",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "123"),
                "email@test.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
    }

    @Test
    void getAllOrder_Empty_ReturnsEmptyOrderList() {
        Page<Order> page = new PageImpl<>(List.of());

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                "prod",
                List.of(OrderStatus.PENDING),
                Pair.of("VN", "123"),
                "email@test.com",
                Pair.of(0, 10)
        );

        assertThat(result.orderList()).isNull();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    void getLatestOrders_ReturnsOrders() {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).shippingAddressId(address).billingAddressId(address).build();
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of(order));

        var result = orderService.getLatestOrders(5);

        assertThat(result).hasSize(1);
    }

    @Test
    void getLatestOrders_CountZero_ReturnsEmpty() {
        var result = orderService.getLatestOrders(0);
        assertThat(result).isEmpty();
    }

    @Test
    void getLatestOrders_Empty_ReturnsEmpty() {
        when(orderRepository.getLatestOrders(any(Pageable.class))).thenReturn(List.of());
        var result = orderService.getLatestOrders(5);
        assertThat(result).isEmpty();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_ProductHasVariations_ReturnsTrue() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        ProductVariationVm varVm = new ProductVariationVm(2L, "var1", "sku");
        when(productService.getProductVariations(1L)).thenReturn(List.of(varVm));

        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.of(new Order()));

        var result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_NoVariations_ReturnsFalse() {
        authenticationUtilsMock.when(AuthenticationUtils::extractUserId).thenReturn("user-1");
        when(productService.getProductVariations(1L)).thenReturn(Collections.emptyList());

        when(orderRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        var result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void exportCsv_HasOrders_ReturnsCsvBytes() throws IOException {
        OrderAddress address = OrderAddress.builder().id(1L).build();
        Order order = Order.builder().id(1L).orderStatus(OrderStatus.PENDING).shippingAddressId(address).billingAddressId(address).build();
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        
        OrderRequest req = new OrderRequest();
        req.setPageNo(0);
        req.setPageSize(10);
        req.setOrderStatus(List.of());
        req.setCreatedFrom(ZonedDateTime.now());
        req.setCreatedTo(ZonedDateTime.now());
        req.setBillingCountry("VN");
        req.setBillingPhoneNumber("123");

        lenient().when(orderMapper.toCsv(any())).thenReturn(mock(com.yas.order.model.csv.OrderItemCsv.class));

        byte[] result = orderService.exportCsv(req);
        assertThat(result).isNotNull();
    }

    @Test
    void exportCsv_NoOrders_ReturnsCsvBytes() throws IOException {
        Page<Order> page = new PageImpl<>(List.of());

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        
        OrderRequest req = new OrderRequest();
        req.setPageNo(0);
        req.setPageSize(10);
        req.setOrderStatus(List.of());
        req.setCreatedFrom(ZonedDateTime.now());
        req.setCreatedTo(ZonedDateTime.now());
        req.setBillingCountry("VN");
        req.setBillingPhoneNumber("123");
        req.setCreatedFrom(ZonedDateTime.now());
        req.setCreatedTo(ZonedDateTime.now());
        req.setBillingCountry("VN");
        req.setBillingPhoneNumber("123");

        byte[] result = orderService.exportCsv(req);
        assertThat(result).isNotNull();
    }
}
