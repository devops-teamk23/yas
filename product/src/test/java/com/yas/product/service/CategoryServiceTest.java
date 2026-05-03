package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private MediaService mediaService;
    
    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryPostVm categoryPostVm;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setSlug("cat-1");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);

        categoryPostVm = new CategoryPostVm("Category 1", "cat-1", "desc", null, "meta", "keyword", (short)1, true, 1L);
    }

    @Test
    void getPageableCategories_ReturnCategories() {
        Page<Category> page = new PageImpl<>(List.of(category), PageRequest.of(0, 10), 1);
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        CategoryListGetVm result = categoryService.getPageableCategories(0, 10);
        assertThat(result.categoryContent()).hasSize(1);
    }

    @Test
    void create_DuplicateName_ThrowException() {
        when(categoryRepository.findExistedName(anyString(), isNull())).thenReturn(new Category());
        assertThrows(DuplicatedException.class, () -> categoryService.create(categoryPostVm));
    }

    @Test
    void create_Success() {
        when(categoryRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        Category result = categoryService.create(categoryPostVm);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void update_CategoryNotFound_ThrowException() {
        when(categoryRepository.findExistedName(anyString(), eq(1L))).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> categoryService.update(categoryPostVm, 1L));
    }

    @Test
    void update_Success() {
        when(categoryRepository.findExistedName(anyString(), eq(1L))).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        categoryService.update(categoryPostVm, 1L);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void update_WithParent_Success() {
        CategoryPostVm postVmWithParent = new CategoryPostVm("Category 1", "cat-1", "desc", 2L, "meta", "keyword", (short)1, true, 1L);
        Category parent = new Category();
        parent.setId(2L);
        
        when(categoryRepository.findExistedName(anyString(), eq(1L))).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        
        categoryService.update(postVmWithParent, 1L);
        assertThat(category.getParent()).isEqualTo(parent);
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "url"));
        
        CategoryGetDetailVm result = categoryService.getCategoryById(1L);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.categoryImage().url()).isEqualTo("url");
    }

    @Test
    void getCategories_Success() {
        when(categoryRepository.findByNameContainingIgnoreCase("cat")).thenReturn(List.of(category));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "caption", "filename", "mediaType", "url"));
        
        List<CategoryGetVm> result = categoryService.getCategories("cat");
        assertThat(result).hasSize(1);
    }

    @Test
    void getCategoryByIds_Success() {
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(1L));
        assertThat(result).hasSize(1);
    }

    @Test
    void getTopNthCategories_Success() {
        when(categoryRepository.findCategoriesOrderedByProductCount(any(Pageable.class))).thenReturn(List.of("Cat1"));
        List<String> result = categoryService.getTopNthCategories(10);
        assertThat(result).hasSize(1);
    }
}