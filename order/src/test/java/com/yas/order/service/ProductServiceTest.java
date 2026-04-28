package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private ProductService productService;

    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    private RestClient.ResponseSpec responseSpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    private RestClient.RequestBodySpec requestBodySpec;

    @BeforeEach
    void setUp() {
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
        requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(RestClient.RequestBodySpec.class);
    }

    @Test
    void getProductVariations_shouldReturnList() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractJwt).thenReturn("jwtToken");
            when(serviceUrlConfig.product()).thenReturn("http://product");

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            ProductVariationVm variation = new ProductVariationVm(1L, "Variation 1", "sku");
            ResponseEntity<List<ProductVariationVm>> responseEntity = ResponseEntity.ok(List.of(variation));
            
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

            List<ProductVariationVm> result = productService.getProductVariations(1L);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void subtractProductStockQuantity_shouldCallPut() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractJwt).thenReturn("jwtToken");
            when(serviceUrlConfig.product()).thenReturn("http://product");

            when(restClient.put()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(any(java.net.URI.class))).thenReturn(requestBodySpec);
            when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
            when(requestBodySpec.body(any(List.class))).thenReturn(requestBodySpec);
            when(requestBodySpec.retrieve()).thenReturn(responseSpec);

            OrderItemVm item = new OrderItemVm(1L, 1L, "Product", 2, BigDecimal.TEN, "Note", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
            OrderVm orderVm = OrderVm.builder()
                    .orderItemVms(Set.of(item))
                    .build();

            productService.subtractProductStockQuantity(orderVm);

            verify(requestBodySpec).retrieve();
        }
    }

    @Test
    void getProductInfomation_shouldReturnMap() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractJwt).thenReturn("jwtToken");
            when(serviceUrlConfig.product()).thenReturn("http://product");

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            ProductCheckoutListVm productCheckout = ProductCheckoutListVm.builder().id(1L).build();
            ProductGetCheckoutListVm getCheckoutListVm = new ProductGetCheckoutListVm(List.of(productCheckout), 0, 10, 1, 1, true);
            ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(getCheckoutListVm);

            when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

            Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(1L), 0, 10);

            assertThat(result).containsKey(1L);
        }
    }

    @Test
    void getProductInfomation_shouldThrowNotFound_whenResponseNull() {
        try (MockedStatic<AuthenticationUtils> authMock = mockStatic(AuthenticationUtils.class)) {
            authMock.when(AuthenticationUtils::extractJwt).thenReturn("jwtToken");
            when(serviceUrlConfig.product()).thenReturn("http://product");

            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(null);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

            assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));
        }
    }

    @Test
    void handleProductVariationListFallback_shouldThrowThrowable() {
        Throwable throwable = new RuntimeException("Error");
        assertThrows(RuntimeException.class, () -> productService.handleProductVariationListFallback(throwable));
    }

    @Test
    void handleProductInfomationFallback_shouldThrowThrowable() {
        Throwable throwable = new RuntimeException("Error");
        assertThrows(RuntimeException.class, () -> productService.handleProductInfomationFallback(throwable));
    }
}
