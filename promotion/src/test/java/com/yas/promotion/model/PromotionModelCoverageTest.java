package com.yas.promotion.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class PromotionModelCoverageTest {

    @Test
    public void testPromotion() {
        Promotion promotion1 = new Promotion();
        promotion1.setId(1L);
        promotion1.setName("Name");
        promotion1.setSlug("slug");
        promotion1.setDescription("Desc");
        promotion1.setCouponCode("CODE");
        promotion1.setDiscountType(com.yas.promotion.model.enumeration.DiscountType.PERCENTAGE);
        promotion1.setUsageType(com.yas.promotion.model.enumeration.UsageType.LIMITED);
        promotion1.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.PRODUCT);
        promotion1.setUsageLimit(10);
        promotion1.setUsageCount(1);
        promotion1.setDiscountPercentage(10L);
        promotion1.setDiscountAmount(0L);
        promotion1.setMinimumOrderPurchaseAmount(100L);
        promotion1.setIsActive(true);
        promotion1.setStartDate(Instant.now());
        promotion1.setEndDate(Instant.now());
        promotion1.setPromotionApplies(Collections.emptyList());

        assertEquals(1L, promotion1.getId());
        assertEquals("Name", promotion1.getName());
        assertEquals("slug", promotion1.getSlug());
        assertEquals("Desc", promotion1.getDescription());
        assertEquals("CODE", promotion1.getCouponCode());
        assertEquals(com.yas.promotion.model.enumeration.DiscountType.PERCENTAGE, promotion1.getDiscountType());
        assertEquals(com.yas.promotion.model.enumeration.UsageType.LIMITED, promotion1.getUsageType());
        assertEquals(com.yas.promotion.model.enumeration.ApplyTo.PRODUCT, promotion1.getApplyTo());
        assertEquals(10, promotion1.getUsageLimit());
        assertEquals(1, promotion1.getUsageCount());
        assertEquals(10L, promotion1.getDiscountPercentage());
        assertEquals(0L, promotion1.getDiscountAmount());
        assertEquals(100L, promotion1.getMinimumOrderPurchaseAmount());
        assertTrue(promotion1.getIsActive());
        assertNotNull(promotion1.getStartDate());
        assertNotNull(promotion1.getEndDate());
        assertNotNull(promotion1.getPromotionApplies());

        Promotion promotion2 = Promotion.builder()
                .id(1L).name("Name").slug("slug").description("Desc").couponCode("CODE")
                .discountType(com.yas.promotion.model.enumeration.DiscountType.PERCENTAGE)
                .usageType(com.yas.promotion.model.enumeration.UsageType.LIMITED)
                .applyTo(com.yas.promotion.model.enumeration.ApplyTo.PRODUCT)
                .usageLimit(10).usageCount(1).discountPercentage(10L).discountAmount(0L)
                .minimumOrderPurchaseAmount(100L).isActive(true)
                .startDate(promotion1.getStartDate()).endDate(promotion1.getEndDate())
                .promotionApplies(Collections.emptyList())
                .build();
        
        // Equals and HashCode coverage
        promotion1.equals(promotion2);
        promotion1.hashCode();
        
        Promotion p3 = Promotion.builder().build();
        promotion1.equals(p3);
        p3.equals(promotion1);

        Promotion promotion3 = new Promotion();
        assertNotEquals(promotion1, promotion3);
        
        // Coverage for equals branches
        assertTrue(promotion1.equals(promotion1));
        assertFalse(promotion1.equals(null));
        assertFalse(promotion1.equals(new Object()));
        Promotion diffId = new Promotion();
        diffId.setId(2L);
        assertFalse(promotion1.equals(diffId));
    }

    @Test
    public void testPromotionApply() {
        PromotionApply obj = new PromotionApply();
        obj.setId(1L);
        obj.setProductId(2L);
        Promotion p = new Promotion();
        p.setId(3L);
        obj.setPromotion(p);

        assertEquals(1L, obj.getId());
        assertEquals(2L, obj.getProductId());
        assertEquals(3L, obj.getPromotion().getId());

        PromotionApply obj2 = PromotionApply.builder()
            .id(1L).productId(2L).promotion(p).build();
        
        obj.equals(obj2);
        obj.hashCode();
        
        assertTrue(obj.equals(obj));
        assertFalse(obj.equals(null));
        assertFalse(obj.equals(new Object()));
        
        PromotionApply obj3 = new PromotionApply();
        assertNotEquals(obj, obj3);
    }

    @Test
    public void testPromotionUsage() {
        PromotionUsage obj = new PromotionUsage();
        obj.setId(1L);
        obj.setUserId("user1");
        Promotion p = new Promotion();
        p.setId(3L);
        obj.setPromotion(p);

        assertEquals(1L, obj.getId());
        assertEquals("user1", obj.getUserId());
        assertEquals(3L, obj.getPromotion().getId());

        PromotionUsage obj2 = PromotionUsage.builder()
            .id(1L).userId("user1").promotion(p).build();
        
        obj.equals(obj2);
        obj.hashCode();
        
        assertTrue(obj.equals(obj));
        assertFalse(obj.equals(null));
        assertFalse(obj.equals(new Object()));
        
        PromotionUsage obj3 = new PromotionUsage();
        assertNotEquals(obj, obj3);
    }
}
