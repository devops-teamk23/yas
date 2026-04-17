package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TaxClassService.class)
@DisplayName("TaxClassService Tests")
public class TaxClassServiceTest {

    @MockitoBean
    private TaxClassRepository taxClassRepository;

    @Autowired
    private TaxClassService taxClassService;

    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.of(TaxClass.class)
            .set(field("id"), 1L)
            .create();
        taxClass.setName("Electronics");
    }

    @Nested
    @DisplayName("findAllTaxClasses")
    class FindAllTaxClassesTests {
        @Test
        @DisplayName("Should return all tax classes sorted by name ascending")
        void testFindAllTaxClasses_shouldReturnAllSorted() {
            // Arrange
            TaxClass taxClass2 = Instancio.create(TaxClass.class);
            taxClass2.setId(2L);
            taxClass2.setName("Clothing");
            when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of(taxClass2, taxClass));
            
            // Act
            List<TaxClassVm> result = taxClassService.findAllTaxClasses();
            
            // Assert
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when no tax classes exist")
        void testFindAllTaxClasses_shouldReturnEmptyList() {
            when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
                .thenReturn(List.of());
            
            List<TaxClassVm> result = taxClassService.findAllTaxClasses();
            
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {
        @Test
        @DisplayName("Should return tax class by valid ID")
        void testFindById_shouldReturnTaxClass() {
            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
            
            TaxClassVm result = taxClassService.findById(1L);
            
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(taxClass.getId());
            assertThat(result.name()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should throw NotFoundException when ID not found")
        void testFindById_shouldThrowNotFoundExceptionWhenIdNotFound() {
            when(taxClassRepository.findById(999L)).thenReturn(Optional.empty());
            
            assertThatThrownBy(() -> taxClassService.findById(999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTests {
        @Test
        @DisplayName("Should successfully create tax class with unique name")
        void testCreate_shouldSuccessfullyCreateWithUniqueName() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Books");
            when(taxClassRepository.existsByName("Books")).thenReturn(false);
            when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);
            
            // Act
            TaxClass result = taxClassService.create(postVm);
            
            // Assert
            assertThat(result).isNotNull();
            verify(taxClassRepository).save(any(TaxClass.class));
        }

        @Test
        @DisplayName("Should throw DuplicatedException when name already exists")
        void testCreate_shouldThrowDuplicatedExceptionWhenNameExists() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Electronics");
            when(taxClassRepository.existsByName("Electronics")).thenReturn(true);
            
            // Act & Assert
            assertThatThrownBy(() -> taxClassService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
            
            verify(taxClassRepository).existsByName("Electronics");
        }

        @Test
        @DisplayName("Should handle case-sensitive duplicate check")
        void testCreate_shouldHandleCaseSensitiveDuplicateCheck() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "electronics");
            when(taxClassRepository.existsByName("electronics")).thenReturn(false);
            when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);
            
            // Act
            TaxClass result = taxClassService.create(postVm);
            
            // Assert
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {
        @Test
        @DisplayName("Should successfully update tax class with unique name")
        void testUpdate_shouldSuccessfullyUpdateWithUniqueName() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Updated Electronics");
            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
            when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Electronics", 1L))
                .thenReturn(false);
            
            // Act
            taxClassService.update(postVm, 1L);
            
            // Assert
            assertThat(taxClass.getName()).isEqualTo("Updated Electronics");
            verify(taxClassRepository).save(taxClass);
        }

        @Test
        @DisplayName("Should throw NotFoundException when tax class not found")
        void testUpdate_shouldThrowNotFoundExceptionWhenNotFound() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Updated Name");
            when(taxClassRepository.findById(999L)).thenReturn(Optional.empty());
            
            // Act & Assert
            assertThatThrownBy(() -> taxClassService.update(postVm, 999L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Should throw DuplicatedException when name already used by another record")
        void testUpdate_shouldThrowDuplicatedExceptionWhenNameUsedByAnother() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Already Used Name");
            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
            when(taxClassRepository.existsByNameNotUpdatingTaxClass("Already Used Name", 1L))
                .thenReturn(true);
            
            // Act & Assert
            assertThatThrownBy(() -> taxClassService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
        }

        @Test
        @DisplayName("Should allow same name during update (itself)")
        void testUpdate_shouldAllowSameNameDuringUpdate() {
            // Arrange
            TaxClassPostVm postVm = new TaxClassPostVm("1", "Electronics");
            when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
            when(taxClassRepository.existsByNameNotUpdatingTaxClass("Electronics", 1L))
                .thenReturn(false);
            
            // Act
            taxClassService.update(postVm, 1L);
            
            // Assert
            assertThat(taxClass.getName()).isEqualTo("Electronics");
            verify(taxClassRepository).save(taxClass);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {
        @Test
        @DisplayName("Should successfully delete tax class")
        void testDelete_shouldSuccessfullyDelete() {
            when(taxClassRepository.existsById(1L)).thenReturn(true);
            
            taxClassService.delete(1L);
            
            verify(taxClassRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when tax class not found")
        void testDelete_shouldThrowNotFoundExceptionWhenNotFound() {
            when(taxClassRepository.existsById(999L)).thenReturn(false);
            
            assertThatThrownBy(() -> taxClassService.delete(999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getPageableTaxClasses")
    class GetPageableTaxClassesTests {
        @Test
        @DisplayName("Should return paginated tax classes")
        void testGetPageableTaxClasses_shouldReturnPaginatedResults() {
            // Arrange
            Page<TaxClass> page = new PageImpl<>(List.of(taxClass), PageRequest.of(0, 10), 1);
            when(taxClassRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
            
            // Act
            TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);
            
            // Assert
            assertThat(result.taxClassContent()).hasSize(1);
            assertThat(result.pageNo()).isEqualTo(0);
            assertThat(result.pageSize()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.isLast()).isTrue();
        }

        @Test
        @DisplayName("Should return empty page when no tax classes exist")
        void testGetPageableTaxClasses_shouldReturnEmptyPageWhenNoResults() {
            // Arrange
            Page<TaxClass> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(taxClassRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);
            
            // Act
            TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);
            
            // Assert
            assertThat(result.taxClassContent()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }
}
