# RestClient Mock Setup Analysis

## Summary

Found **15+ Java test files** that mock RestClient with various spec types (`RequestHeadersUriSpec`, `RequestBodyUriSpec`, and `ResponseSpec`).

---

## Files Found with RestClient Mocking

### 1. **recommendation/src/test/java/com/yas/recommendation/service/ProductServiceTest.java**

**Mock Pattern:** `@Mock` annotation with `MockitoExtension`

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private RestClient restClient;

    @Mock
    private RecommendationConfig config;

    @Mock
    private RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private ResponseSpec responseSpec;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(restClient, config);
    }
}
```

**Mock Setup:**

```java
when(restClient.get()).thenReturn(requestSpec);
when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
when(requestSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
    .thenReturn(ResponseEntity.ok(expectedProduct));
```

**RequestSpec Type:** `RequestHeadersUriSpec<?>`

---

### 2. **cart/src/test/java/com/yas/cart/service/ProductServiceTest.java**

**Mock Pattern:** `Mockito.mock()` in `setUp()` method

```java
@BeforeEach
void setUp() {
    restClient = Mockito.mock(RestClient.class);
    serviceUrlConfig = Mockito.mock(ServiceUrlConfig.class);
    productService = new ProductService(restClient, serviceUrlConfig);
    requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup:**

```java
when(serviceUrlConfig.product()).thenReturn("http://api.yas.local/media");
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductThumbnailVm>>() {}))
    .thenReturn(ResponseEntity.ok(getProductThumbnailVms()));
```

**RequestSpec Type:** `RestClient.RequestHeadersUriSpec`

---

### 3. **inventory/src/test/java/com/yas/inventory/service/ProductServiceTest.java**

**Mock Pattern:** `mock()` static import in `setUp()`

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    productService = new ProductService(restClient, serviceUrlConfig);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
}
```

**Mock Setup for GET:**

```java
RestClient.RequestHeadersUriSpec requestHeadersUriSpec
    = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.body(ProductInfoVm.class)).thenReturn(productInfoVm);
```

**Mock Setup for PUT:**

```java
RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
when(restClient.put()).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.body(productQuantityPostVms)).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
```

**RequestSpec Types:** `RequestHeadersUriSpec`, `RequestBodyUriSpec`

---

### 4. **inventory/src/test/java/com/yas/inventory/service/LocationServiceTest.java**

**Mock Pattern:** `mock()` with chained method calls

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    locationService = new LocationService(restClient, serviceUrlConfig);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup for GET:**

```java
RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.body(AddressDetailVm.class)).thenReturn(addressDetail);
```

**Mock Setup for POST:**

```java
RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
when(restClient.post()).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.body(addressPost)).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
```

**RequestSpec Types:** `RequestHeadersUriSpec`, `RequestBodyUriSpec`

---

### 5. **customer/src/test/java/com/yas/customer/service/LocationServiceTest.java**

**Mock Pattern:** `mock()` with query parameters

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    locationService = new LocationService(restClient, serviceUrlConfig);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup:**

```java
URI uri = UriComponentsBuilder.fromUriString(INVENTORY_URL)
    .path("/storefront/addresses")
    .queryParam("ids", ids)
    .build()
    .toUri();

RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.body(new ParameterizedTypeReference<List<AddressDetailVm>>() {}))
    .thenReturn(Collections.singletonList(addressDetail));
```

**RequestSpec Type:** `RequestHeadersUriSpec`

---

### 6. **common-library/src/it/java/common/kafka/CdcConsumerTest.java**

**Mock Pattern:** `@MockitoBean` and `@Mock` annotations (Integration Test Base Class)

```java
@Getter
public abstract class CdcConsumerTest<K, M> {
    @Autowired
    private KafkaContainer kafkaContainer;

    @MockitoBean
    private RestClient restClient;

    @Mock
    RestClient.ResponseSpec responseSpec;

    @Mock
    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }
}
```

**Mock Setup:**

```java
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
```

**RequestSpec Types:** `RequestHeadersUriSpec`, `ResponseSpec`

---

### 7. **search/src/test/java/com/yas/search/service/ProductSyncDataServiceTest.java**

**Mock Pattern:** `mock()` static method

```java
@BeforeEach
void setUp() {
    productRepository = mock(ProductRepository.class);
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    productSyncDataService = new ProductSyncDataService(restClient, serviceUrlConfig, productRepository);
    requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    responseSpec = mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup:**

```java
final URI url = UriComponentsBuilder.fromUriString(PRODUCT_URL)
    .path("/storefront/products-es/{id}")
    .buildAndExpand(ID)
    .toUri();

when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.body(ProductEsDetailVm.class))
    .thenReturn(getProductThumbnailVms());
```

**RequestSpec Type:** `RequestHeadersUriSpec`

---

### 8. **rating/src/test/java/com/yas/rating/service/OrderServiceTest.java**

**Mock Pattern:** `mock()` with header chains

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    orderService = new OrderService(restClient, serviceUrlConfig);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup:**

```java
RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
```

**RequestSpec Type:** `RequestHeadersUriSpec`

---

### 9. **order/src/test/java/com/yas/order/service/PromotionServiceTest.java**

**Mock Pattern:** `mock()` with POST request

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    serviceUrlConfig = mock(ServiceUrlConfig.class);
    promotionService = new PromotionService(restClient, serviceUrlConfig);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

**Mock Setup for POST:**

```java
RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
when(restClient.post()).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
```

**RequestSpec Type:** `RequestBodyUriSpec`

---

## Common Mock Setup Patterns

### Pattern 1: Using @Mock Annotation with MockitoExtension

```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private RestClient restClient;

    @Mock
    private RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private ResponseSpec responseSpec;
}
```

### Pattern 2: Using Mockito.mock() in @BeforeEach

```java
@BeforeEach
void setUp() {
    restClient = Mockito.mock(RestClient.class);
    requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

### Pattern 3: Using static mock() Import

```java
@BeforeEach
void setUp() {
    restClient = mock(RestClient.class);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
}
```

### Pattern 4: Using @MockitoBean (Integration Tests)

```java
@MockitoBean
private RestClient restClient;

@Mock
RestClient.ResponseSpec responseSpec;

@Mock
RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
```

---

## RequestSpec Types Used

| Type                    | Used For             | HTTP Methods           |
| ----------------------- | -------------------- | ---------------------- |
| `RequestHeadersUriSpec` | GET, DELETE requests | `.get()`, `.delete()`  |
| `RequestBodyUriSpec`    | POST, PUT requests   | `.post()`, `.put()`    |
| `ResponseSpec`          | Response handling    | `.retrieve()` chaining |

---

## Common Mock Chaining Pattern

```java
// Basic GET chain
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
when(responseSpec.body(SomeClass.class)).thenReturn(expectedObject);

// GET with headers
when(restClient.get()).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

// POST/PUT chain with body
when(restClient.post()).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
```

---

## Summary of Locations

- **Recommendation module:** `recommendation/src/test/java/com/yas/recommendation/service/ProductServiceTest.java`
- **Cart module:** `cart/src/test/java/com/yas/cart/service/ProductServiceTest.java`
- **Inventory module:**
  - `inventory/src/test/java/com/yas/inventory/service/ProductServiceTest.java`
  - `inventory/src/test/java/com/yas/inventory/service/LocationServiceTest.java`
- **Customer module:** `customer/src/test/java/com/yas/customer/service/LocationServiceTest.java`
- **Common Library:** `common-library/src/it/java/common/kafka/CdcConsumerTest.java` (Base test class)
- **Search module:** `search/src/test/java/com/yas/search/service/ProductSyncDataServiceTest.java`
- **Rating module:** `rating/src/test/java/com/yas/rating/service/OrderServiceTest.java`
- **Order module:** `order/src/test/java/com/yas/order/service/PromotionServiceTest.java`

---

## Key Observations

1. **Two main mocking strategies:**
   - `@Mock` annotation with `@ExtendWith(MockitoExtension.class)`
   - `Mockito.mock()` static calls in `@BeforeEach`

2. **Fluent API pattern:** All setups use method chaining with `.thenReturn()` to build the mock behavior

3. **RequestSpec types:**
   - `RequestHeadersUriSpec` for GET/DELETE operations
   - `RequestBodyUriSpec` for POST/PUT operations

4. **Security context:** Many tests call `setUpSecurityContext()` before setting up mocks

5. **URI construction:** Most tests use `UriComponentsBuilder` to construct test URLs programmatically

6. **Response handling:** Both `.body()` and `.toEntity()` patterns used depending on context
