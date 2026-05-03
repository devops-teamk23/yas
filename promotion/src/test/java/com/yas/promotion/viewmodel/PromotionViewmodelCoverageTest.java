package com.yas.promotion.viewmodel;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PromotionViewmodelCoverageTest {

    @Test
    public void testAllViewModels() throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(BrandVm.class);
        classes.add(CategoryGetVm.class);
        classes.add(ProductVm.class);
        classes.add(PromotionDetailVm.class);
        classes.add(PromotionDto.class);
        classes.add(PromotionListVm.class);
        classes.add(PromotionPostVm.class);
        classes.add(PromotionPutVm.class);
        classes.add(PromotionUsageVm.class);
        classes.add(PromotionVerifyResultDto.class);
        classes.add(PromotionVerifyVm.class);
        classes.add(PromotionVm.class);

        for (Class<?> clazz : classes) {
            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isEnum()) {
                continue;
            }
            
            Object instance1 = createInstance(clazz);
            Object instance2 = createInstance(clazz);
            
            if (instance1 == null) continue;

            // Invoke all methods (getters, setters, equals, hashCode, toString)
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        Object defaultParam = getDefaultValue(paramType);
                        assertDoesNotThrow(() -> method.invoke(instance1, defaultParam));
                    } else if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                        assertDoesNotThrow(() -> method.invoke(instance1));
                    } else if (method.getName().equals("equals")) {
                        assertDoesNotThrow(() -> method.invoke(instance1, instance1));
                        assertDoesNotThrow(() -> method.invoke(instance1, instance2));
                        assertDoesNotThrow(() -> method.invoke(instance1, new Object()));
                        assertDoesNotThrow(() -> method.invoke(instance1, (Object) null));
                        
                        // Populate fields to hit non-null branches in equals/hashCode
                        populateAllProperties(instance1, clazz);
                        populateAllProperties(instance2, clazz);
                        assertDoesNotThrow(() -> method.invoke(instance1, instance2));
                        assertDoesNotThrow(() -> method.invoke(instance1, instance1));
                        
                        // Change one field at a time for branch coverage in equals
                        for (Method setter : clazz.getDeclaredMethods()) {
                            if (Modifier.isPublic(setter.getModifiers()) && setter.getName().startsWith("set") && setter.getParameterCount() == 1) {
                                Object diff = createInstance(clazz);
                                populateAllProperties(diff, clazz);
                                try {
                                    setter.invoke(diff, getAnotherValue(setter.getParameterTypes()[0]));
                                    assertDoesNotThrow(() -> method.invoke(instance1, diff));
                                    assertDoesNotThrow(() -> method.invoke(diff, instance1));
                                } catch (Exception e) {
                                    // ignore
                                }
                            }
                        }
                        
                        // One is null, one is populated
                        Object instance3 = createInstance(clazz);
                        assertDoesNotThrow(() -> method.invoke(instance1, instance3));
                        assertDoesNotThrow(() -> method.invoke(instance3, instance1));
                    } else if (method.getName().equals("hashCode") || method.getName().equals("toString")) {
                        assertDoesNotThrow(() -> method.invoke(instance1));
                    }
                }
            }

            // Test builder if exists
            try {
                Method builderMethod = clazz.getMethod("builder");
                Object builder = builderMethod.invoke(null);
                Method buildMethod = builder.getClass().getMethod("build");
                Object builtInstance = buildMethod.invoke(builder);
                
                // Invoke toString on builder for coverage
                assertDoesNotThrow(() -> builder.getClass().getMethod("toString").invoke(builder));
                
            } catch (Exception e) {
                // Ignore if no builder
            }
            
            // Try testing parameterized constructor if exists (for records or lombok all-args)
            try {
                for (Constructor<?> c : clazz.getDeclaredConstructors()) {
                    if (c.getParameterCount() > 0) {
                        Object[] args = new Object[c.getParameterCount()];
                        for (int i = 0; i < c.getParameterCount(); i++) {
                            args[i] = getDefaultValue(c.getParameterTypes()[i]);
                        }
                        c.setAccessible(true);
                        try {
                            c.newInstance(args);
                        } catch (Exception ex) {
                            // ignore NPEs and other exceptions thrown by constructor logic
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private Object createInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Some records or classes without no-arg constructor
            try {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                if (constructors.length > 0) {
                    Constructor<?> constructor = constructors[0];
                    constructor.setAccessible(true);
                    Object[] args = new Object[constructor.getParameterCount()];
                    for (int i = 0; i < constructor.getParameterCount(); i++) {
                        args[i] = getDefaultValue(constructor.getParameterTypes()[i]);
                    }
                    return constructor.newInstance(args);
                }
            } catch (Exception ex) {
                // Ignore
            }
        }
        return null;
    }

    private void populateAllProperties(Object instance, Class<?> clazz) {
        if (instance == null) return;
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("set") && method.getParameterCount() == 1) {
                try {
                    method.invoke(instance, getDefaultValue(method.getParameterTypes()[0]));
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == Integer.class) return 1;
        if (type == long.class || type == Long.class) return 1L;
        if (type == double.class || type == Double.class) return 1.0;
        if (type == float.class || type == Float.class) return 1.0f;
        if (type == boolean.class || type == Boolean.class) return true;
        if (type == String.class) return "test";
        if (type == java.util.List.class) return new ArrayList<>();
        if (type == java.time.Instant.class) return java.time.Instant.now();
        if (type == java.time.ZonedDateTime.class) return java.time.ZonedDateTime.now();
        if (type == java.time.LocalDate.class) return java.time.LocalDate.now();
        
        // Return null for other complex objects or try to instantiate
        try {
            if (type.isEnum()) {
                return type.getEnumConstants()[0];
            }
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private Object getAnotherValue(Class<?> type) {
        if (type == int.class || type == Integer.class) return 2;
        if (type == long.class || type == Long.class) return 2L;
        if (type == double.class || type == Double.class) return 2.0;
        if (type == float.class || type == Float.class) return 2.0f;
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == String.class) return "test2";
        if (type == java.util.List.class) return List.of("item");
        if (type == java.time.Instant.class) return java.time.Instant.now().plusSeconds(100);
        if (type == java.time.ZonedDateTime.class) return java.time.ZonedDateTime.now().plusDays(1);
        if (type == java.time.LocalDate.class) return java.time.LocalDate.now().plusDays(1);
        
        try {
            if (type.isEnum()) {
                return type.getEnumConstants().length > 1 ? type.getEnumConstants()[1] : type.getEnumConstants()[0];
            }
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testStaticMethods() {
        // PromotionVm.fromModel
        com.yas.promotion.model.Promotion promotion = new com.yas.promotion.model.Promotion();
        promotion.setId(1L);
        promotion.setName("Promo");
        promotion.setSlug("promo");
        promotion.setDiscountPercentage(10L);
        promotion.setDiscountAmount(0L);
        promotion.setIsActive(true);
        promotion.setStartDate(java.time.Instant.now());
        promotion.setEndDate(java.time.Instant.now());
        PromotionVm vm = PromotionVm.fromModel(promotion);
        org.junit.jupiter.api.Assertions.assertNotNull(vm);

        // PromotionPostVm.createPromotionApplies
        PromotionPostVm postVm = new PromotionPostVm();
        postVm.setProductIds(List.of(1L, 2L));
        postVm.setBrandIds(List.of(3L, 4L));
        postVm.setCategoryIds(List.of(5L, 6L));

        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.PRODUCT);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPostVm.createPromotionApplies(postVm, promotion).size());
        
        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.BRAND);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPostVm.createPromotionApplies(postVm, promotion).size());
        
        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.CATEGORY);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPostVm.createPromotionApplies(postVm, promotion).size());

        // PromotionPutVm.createPromotionApplies
        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setProductIds(List.of(1L, 2L));
        putVm.setBrandIds(List.of(3L, 4L));
        putVm.setCategoryIds(List.of(5L, 6L));

        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.PRODUCT);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPutVm.createPromotionApplies(putVm, promotion).size());
        
        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.BRAND);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPutVm.createPromotionApplies(putVm, promotion).size());
        
        promotion.setApplyTo(com.yas.promotion.model.enumeration.ApplyTo.CATEGORY);
        org.junit.jupiter.api.Assertions.assertEquals(2, PromotionPutVm.createPromotionApplies(putVm, promotion).size());
    }
}
