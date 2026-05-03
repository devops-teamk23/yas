package com.yas.product.model;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductTemplate;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ModelCoverageTest {

    @Test
    void testBrandEqualsAndHashCode() {
        Brand brand1 = new Brand();
        brand1.setId(1L);
        Brand brand2 = new Brand();
        brand2.setId(1L);
        Brand brand3 = new Brand();
        brand3.setId(2L);

        assertThat(brand1).isEqualTo(brand1);
        assertThat(brand1).isEqualTo(brand2);
        assertThat(brand1).isNotEqualTo(brand3);
        assertThat(brand1).isNotEqualTo(null);
        assertThat(brand1).isNotEqualTo(new Object());
        assertThat(brand1.hashCode()).isEqualTo(Brand.class.hashCode());
    }

    @Test
    void testCategoryEqualsAndHashCode() {
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(1L);
        Category category3 = new Category();
        category3.setId(2L);

        assertThat(category1).isEqualTo(category1);
        assertThat(category1).isEqualTo(category2);
        assertThat(category1).isNotEqualTo(category3);
        assertThat(category1).isNotEqualTo(null);
        assertThat(category1).isNotEqualTo(new Object());
        assertThat(category1.hashCode()).isEqualTo(Category.class.hashCode());
    }

    @Test
    void testProductEqualsAndHashCode() {
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(1L);
        Product product3 = new Product();
        product3.setId(2L);

        assertThat(product1).isEqualTo(product1);
        assertThat(product1).isEqualTo(product2);
        assertThat(product1).isNotEqualTo(product3);
        assertThat(product1).isNotEqualTo(null);
        assertThat(product1).isNotEqualTo(new Object());
        assertThat(product1.hashCode()).isEqualTo(Product.class.hashCode());
    }

    @Test
    void testProductOptionEqualsAndHashCode() {
        ProductOption option1 = new ProductOption();
        option1.setId(1L);
        ProductOption option2 = new ProductOption();
        option2.setId(1L);
        ProductOption option3 = new ProductOption();
        option3.setId(2L);

        assertThat(option1).isEqualTo(option1);
        assertThat(option1).isEqualTo(option2);
        assertThat(option1).isNotEqualTo(option3);
        assertThat(option1).isNotEqualTo(null);
        assertThat(option1).isNotEqualTo(new Object());
        assertThat(option1.hashCode()).isEqualTo(ProductOption.class.hashCode());
    }

    @Test
    void testProductOptionCombinationEqualsAndHashCode() {
        ProductOptionCombination comb1 = new ProductOptionCombination();
        comb1.setId(1L);
        ProductOptionCombination comb2 = new ProductOptionCombination();
        comb2.setId(1L);
        ProductOptionCombination comb3 = new ProductOptionCombination();
        comb3.setId(2L);

        assertThat(comb1).isEqualTo(comb1);
        assertThat(comb1).isEqualTo(comb2);
        assertThat(comb1).isNotEqualTo(comb3);
        assertThat(comb1).isNotEqualTo(null);
        assertThat(comb1).isNotEqualTo(new Object());
        assertThat(comb1.hashCode()).isEqualTo(ProductOptionCombination.class.hashCode());
    }

    @Test
    void testProductOptionValueEqualsAndHashCode() {
        ProductOptionValue val1 = new ProductOptionValue();
        val1.setId(1L);
        ProductOptionValue val2 = new ProductOptionValue();
        val2.setId(1L);
        ProductOptionValue val3 = new ProductOptionValue();
        val3.setId(2L);

        assertThat(val1).isEqualTo(val1);
        assertThat(val1).isEqualTo(val2);
        assertThat(val1).isNotEqualTo(val3);
        assertThat(val1).isNotEqualTo(null);
        assertThat(val1).isNotEqualTo(new Object());
        assertThat(val1.hashCode()).isEqualTo(ProductOptionValue.class.hashCode());
    }

    @Test
    void testProductRelatedEqualsAndHashCode() {
        ProductRelated rel1 = new ProductRelated();
        rel1.setId(1L);
        ProductRelated rel2 = new ProductRelated();
        rel2.setId(1L);
        ProductRelated rel3 = new ProductRelated();
        rel3.setId(2L);

        assertThat(rel1).isEqualTo(rel1);
        assertThat(rel1).isEqualTo(rel2);
        assertThat(rel1).isNotEqualTo(rel3);
        assertThat(rel1).isNotEqualTo(null);
        assertThat(rel1).isNotEqualTo(new Object());
        assertThat(rel1.hashCode()).isEqualTo(ProductRelated.class.hashCode());
    }

    @Test
    void testProductTemplateEqualsAndHashCode() {
        ProductTemplate tpl1 = new ProductTemplate();
        tpl1.setId(1L);
        ProductTemplate tpl2 = new ProductTemplate();
        tpl2.setId(1L);
        ProductTemplate tpl3 = new ProductTemplate();
        tpl3.setId(2L);

        assertThat(tpl1).isEqualTo(tpl1);
        assertThat(tpl1).isEqualTo(tpl2);
        assertThat(tpl1).isNotEqualTo(tpl3);
        assertThat(tpl1).isNotEqualTo(null);
        assertThat(tpl1).isNotEqualTo(new Object());
        assertThat(tpl1.hashCode()).isEqualTo(ProductTemplate.class.hashCode());
    }

    @Test
    void testProductAttributeEqualsAndHashCode() {
        ProductAttribute attr1 = new ProductAttribute();
        attr1.setId(1L);
        ProductAttribute attr2 = new ProductAttribute();
        attr2.setId(1L);
        ProductAttribute attr3 = new ProductAttribute();
        attr3.setId(2L);

        assertThat(attr1).isEqualTo(attr1);
        assertThat(attr1).isEqualTo(attr2);
        assertThat(attr1).isNotEqualTo(attr3);
        assertThat(attr1).isNotEqualTo(null);
        assertThat(attr1).isNotEqualTo(new Object());
        assertThat(attr1.hashCode()).isEqualTo(ProductAttribute.class.hashCode());
    }

    @Test
    void testProductAttributeGroupEqualsAndHashCode() {
        ProductAttributeGroup grp1 = new ProductAttributeGroup();
        grp1.setId(1L);
        ProductAttributeGroup grp2 = new ProductAttributeGroup();
        grp2.setId(1L);
        ProductAttributeGroup grp3 = new ProductAttributeGroup();
        grp3.setId(2L);

        assertThat(grp1).isEqualTo(grp1);
        assertThat(grp1).isEqualTo(grp2);
        assertThat(grp1).isNotEqualTo(grp3);
        assertThat(grp1).isNotEqualTo(null);
        assertThat(grp1).isNotEqualTo(new Object());
        assertThat(grp1.hashCode()).isEqualTo(ProductAttributeGroup.class.hashCode());
    }
}
