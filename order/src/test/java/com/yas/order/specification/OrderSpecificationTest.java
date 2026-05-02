package com.yas.order.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.JoinType;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class OrderSpecificationTest {

    private final CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
    private final Root<Order> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final Root<OrderItem> orderItemRoot = mock(Root.class);
    private final Predicate conjunction = mock(Predicate.class);

    @Test
    void testFindMyOrders() {
        when(root.get("createdBy")).thenReturn(mock(Path.class));
        when(root.get("orderStatus")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(), any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        Subquery subquery1 = mock(Subquery.class);
        when(subqueryMock.select(any())).thenReturn(subquery1);
        when(subquery1.where(any(Predicate.class))).thenReturn(subquery1);
        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(mock(CriteriaBuilder.In.class));
        when(criteriaBuilder.exists(any())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.findMyOrders("user", "product", OrderStatus.PENDING);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testFindOrderByWithMulCriteria() {
        when(query.getResultType()).thenReturn((Class) Order.class);
        when(root.fetch(anyString(), any(JoinType.class))).thenReturn(null);

        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        when(criteriaBuilder.and(any(), any(), any(), any(), any(), any())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.findOrderByWithMulCriteria(
                null, null, null, null, null, null, null);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testExistsByCreatedByAndInProductIdAndOrderStatusCompleted() {
        when(root.get("createdBy")).thenReturn(mock(Path.class));
        when(root.get("orderStatus")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));

        Subquery<OrderItem> subqueryMock = mock(Subquery.class);
        when(query.subquery(OrderItem.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(subqueryMock.select(any())).thenReturn(subqueryMock);
        when(subqueryMock.where(any(Predicate.class))).thenReturn(subqueryMock);

        when(criteriaBuilder.exists(any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(), any())).thenReturn(mock(Predicate.class));

        Path pathMock = mock(Path.class);
        when(orderItemRoot.get(anyString())).thenReturn(pathMock);
        when(root.get(anyString())).thenReturn(pathMock);
        when(pathMock.in(any(Collection.class))).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.existsByCreatedByAndInProductIdAndOrderStatusCompleted("user", List.of(1L));
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testHasProductInOrderItems_whenQueryIsNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasProductInOrderItems(List.of(1L));
        Predicate predicate = spec.toPredicate(root, null, criteriaBuilder);
        assertEquals(conjunction, predicate);
    }

    @Test
    void testHasProductNameInOrderItems_whenQueryIsNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("product");
        assertEquals(conjunction, spec.toPredicate(root, null, criteriaBuilder));
    }

    @Test
    void testHasProductNameInOrderItems_whenProductNameIsEmpty_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);

        Subquery subquery1 = mock(Subquery.class);
        when(subqueryMock.select(any())).thenReturn(subquery1);
        when(subquery1.where(any(Predicate.class))).thenReturn(subquery1);

        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(mock(CriteriaBuilder.In.class));

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithProductName_whenQueryIsNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withProductName("product");
        assertEquals(conjunction, spec.toPredicate(root, null, criteriaBuilder));
    }

    @Test
    void testWithProductName_whenProductNameIsEmpty_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withProductName("");
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithProductName_whenNormalCase_thenSuccess() {
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(subqueryMock.select(any())).thenReturn(subqueryMock);
        when(subqueryMock.where(any(Predicate.class))).thenReturn(subqueryMock);

        when(criteriaBuilder.and(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.exists(any())).thenReturn(mock(Predicate.class));

        Specification<Order> spec = OrderSpecification.withProductName("product");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testHasCreatedBy_whenNormalCase_thenSuccess() {
        String createdBy = "user123";
        when(root.get("createdBy")).thenReturn(mock(Path.class));
        Predicate expectedPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("createdBy"), createdBy)).thenReturn(expectedPredicate);

        Specification<Order> spec = OrderSpecification.hasCreatedBy(createdBy);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(expectedPredicate, resultPredicate);
    }

    @Test
    void testHasOrderStatus_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.hasOrderStatus(null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testHasOrderStatus_whenNormalCase_thenSuccess() {
        when(root.get("orderStatus")).thenReturn(mock(Path.class));
        Predicate expectedPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.COMPLETED)).thenReturn(expectedPredicate);

        Specification<Order> spec = OrderSpecification.hasOrderStatus(OrderStatus.COMPLETED);
        assertEquals(expectedPredicate, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testHasProductNameInOrderItems_whenNormalCase_thenSuccess() {
        Subquery<Long> subqueryMock = mock(Subquery.class);
        when(query.subquery(Long.class)).thenReturn(subqueryMock);
        when(subqueryMock.from(OrderItem.class)).thenReturn(orderItemRoot);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        Subquery subquery1 = mock(Subquery.class);
        when(subqueryMock.select(any())).thenReturn(subquery1);
        Subquery subquery2 = mock(Subquery.class);
        when(subquery1.where(any(Predicate.class))).thenReturn(subquery2);

        CriteriaBuilder.In inMock = mock(CriteriaBuilder.In.class);
        when(criteriaBuilder.in(any())).thenReturn(inMock);
        when(inMock.value(any())).thenReturn(mock(CriteriaBuilder.In.class));

        Specification<Order> spec = OrderSpecification.hasProductNameInOrderItems("SampleProduct");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithEmail_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withEmail(null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithEmail_whenNormalCase_thenSuccess() {
        when(root.get("email")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        Specification<Order> spec = OrderSpecification.withEmail("test@example.com");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithOrderStatusList_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withOrderStatus(null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithOrderStatusList_whenNormalCase_thenSuccess() {
        Path pathMock = mock(Path.class);
        when(root.get("orderStatus")).thenReturn(pathMock);
        when(pathMock.in(any(Collection.class))).thenReturn(mock(Predicate.class));
        Specification<Order> spec = OrderSpecification.withOrderStatus(List.of(OrderStatus.COMPLETED));
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithBillingPhoneNumber_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber(null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithBillingPhoneNumber_whenNormalCase_thenSuccess() {
        Path pathMock = mock(Path.class);
        when(root.get("billingAddressId")).thenReturn(pathMock);
        when(pathMock.get("phone")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        Specification<Order> spec = OrderSpecification.withBillingPhoneNumber("1234567890");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithCountryName_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withCountryName(null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithCountryName_whenNormalCase_thenSuccess() {
        Path path = mock(Path.class);
        when(root.get("billingAddressId")).thenReturn(path);
        when(path.get("countryName")).thenReturn(mock(Path.class));
        when(criteriaBuilder.like(any(), anyString())).thenReturn(mock(Predicate.class));
        Specification<Order> spec = OrderSpecification.withCountryName("USA");
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithDateRange_whenNull_thenConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);
        Specification<Order> spec = OrderSpecification.withDateRange(null, null);
        assertEquals(conjunction, spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithDateRange_whenNormalCase_thenSuccess() {
        ZonedDateTime createdFrom = ZonedDateTime.now().minusDays(7);
        ZonedDateTime createdTo = ZonedDateTime.now();
        when(root.get("createdOn")).thenReturn(mock(Path.class));
        when(criteriaBuilder.between(root.get("createdOn"), createdFrom, createdTo)).thenReturn(mock(Predicate.class));
        Specification<Order> spec = OrderSpecification.withDateRange(createdFrom, createdTo);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }
}
