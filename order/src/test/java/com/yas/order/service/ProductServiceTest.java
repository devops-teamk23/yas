package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
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
public class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductService productService;

    private MockedStatic<AuthenticationUtils> authenticationUtilsMock;

    @BeforeEach
    void setUp() {
        authenticationUtilsMock = mockStatic(AuthenticationUtils.class);
        authenticationUtilsMock.when(AuthenticationUtils::extractJwt).thenReturn("fake-jwt");
        when(serviceUrlConfig.product()).thenReturn("http://product-service");
    }

    @AfterEach
    void tearDown() {
        authenticationUtilsMock.close();
    }

    @Test
    void getProductVariations_ReturnsList() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        List<ProductVariationVm> mockResponse = List.of(new ProductVariationVm(1L, "var1", "sku"));
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        List<ProductVariationVm> result = productService.getProductVariations(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void getProductInfomation_ReturnsMap() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        ProductCheckoutListVm item = ProductCheckoutListVm.builder()
                .id(1L)
                .name("prod1")
                .price(10.0)
                .taxClassId(1L)
                .build();
        ProductGetCheckoutListVm mockResponse = new ProductGetCheckoutListVm(List.of(item), 1, 10, 1, 1, true);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(1L), 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(1L).getName()).isEqualTo("prod1");
    }

    @Test
    void getProductInfomation_NotFound_ThrowsException() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(new ProductGetCheckoutListVm(null, 0, 0, 0, 0, true)));

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(1L), 0, 10));
    }

    @Test
    void subtractProductStockQuantity_Success() {
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.body(any(List.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        OrderVm orderVm = mock(OrderVm.class);
        when(orderVm.orderItemVms()).thenReturn(Set.of(OrderItemVm.builder()
                .id(1L)
                .productId(1L)
                .productName("prod1")
                .quantity(2)
                .productPrice(BigDecimal.TEN)
                .note("note")
                .build()));

        productService.subtractProductStockQuantity(orderVm);

        verify(requestBodySpec).retrieve();
    }
}
