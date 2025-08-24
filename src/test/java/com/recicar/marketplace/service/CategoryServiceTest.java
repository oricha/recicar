package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Engine Parts");
        parentCategory.setSlug("engine-parts");
        parentCategory.setActive(true);
        parentCategory.setSortOrder(1);

        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("Filters");
        childCategory.setSlug("filters");
        childCategory.setParent(parentCategory);
        childCategory.setActive(true);
        childCategory.setSortOrder(1);
    }

    @Test
    void shouldFindCategoryById() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        // When
        Optional<Category> result = categoryService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(parentCategory);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void shouldFindCategoryBySlug() {
        // Given
        when(categoryRepository.findBySlug("engine-parts")).thenReturn(Optional.of(parentCategory));

        // When
        Optional<Category> result = categoryService.findBySlug("engine-parts");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(parentCategory);
        verify(categoryRepository).findBySlug("engine-parts");
    }

    @Test
    void shouldFindAllActiveCategories() {
        // Given
        List<Category> categories = Arrays.asList(parentCategory, childCategory);
        when(categoryRepository.findByActiveTrueOrderBySortOrderAsc()).thenReturn(categories);

        // When
        List<Category> result = categoryService.findAllActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(parentCategory, childCategory);
        verify(categoryRepository).findByActiveTrueOrderBySortOrderAsc();
    }

    @Test
    void shouldFindRootCategories() {
        // Given
        List<Category> rootCategories = Arrays.asList(parentCategory);
        when(categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc()).thenReturn(rootCategories);

        // When
        List<Category> result = categoryService.findRootCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(parentCategory);
        verify(categoryRepository).findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    }

    @Test
    void shouldFindChildCategories() {
        // Given
        List<Category> childCategories = Arrays.asList(childCategory);
        when(categoryRepository.findByParentAndActiveTrueOrderBySortOrderAsc(parentCategory)).thenReturn(childCategories);

        // When
        List<Category> result = categoryService.findChildCategories(parentCategory);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(childCategory);
        verify(categoryRepository).findByParentAndActiveTrueOrderBySortOrderAsc(parentCategory);
    }

    @Test
    void shouldSearchCategoriesByName() {
        // Given
        List<Category> categories = Arrays.asList(parentCategory);
        when(categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue("engine")).thenReturn(categories);

        // When
        List<Category> result = categoryService.searchByName("engine");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(parentCategory);
        verify(categoryRepository).findByNameContainingIgnoreCaseAndActiveTrue("engine");
    }

    @Test
    void shouldCreateCategory() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setSlug("new-category");
        newCategory.setActive(true);

        when(categoryRepository.existsBySlug("new-category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        Category result = categoryService.createCategory("New Category", "Description", null);

        // Then
        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldGenerateUniqueSlugWhenSlugExists() {
        // Given
        when(categoryRepository.existsBySlug("test-category")).thenReturn(true);
        when(categoryRepository.existsBySlug("test-category-1")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Category result = categoryService.createCategory("Test Category", "Description", null);

        // Then
        assertThat(result.getSlug()).isEqualTo("test-category-1");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldUpdateCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(parentCategory)).thenReturn(parentCategory);

        // When
        Category result = categoryService.updateCategory(1L, "Updated Name", "Updated Description", null);

        // Then
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(parentCategory);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.updateCategory(1L, "Name", "Description", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldDeactivateCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(parentCategory)).thenReturn(parentCategory);

        // When
        categoryService.deactivateCategory(1L);

        // Then
        assertThat(parentCategory.isActive()).isFalse();
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(parentCategory);
    }

    @Test
    void shouldActivateCategory() {
        // Given
        parentCategory.setActive(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(parentCategory)).thenReturn(parentCategory);

        // When
        categoryService.activateCategory(1L);

        // Then
        assertThat(parentCategory.isActive()).isTrue();
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(parentCategory);
    }

    @Test
    void shouldCheckIfSlugExists() {
        // Given
        when(categoryRepository.existsBySlug("test-slug")).thenReturn(true);

        // When
        boolean result = categoryService.slugExists("test-slug");

        // Then
        assertThat(result).isTrue();
        verify(categoryRepository).existsBySlug("test-slug");
    }

    @Test
    void shouldGetCategoryHierarchy() {
        // Given
        List<Category> hierarchy = Arrays.asList(parentCategory, childCategory);

        // When
        List<Category> result = categoryService.getCategoryHierarchy(childCategory);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(parentCategory);
        assertThat(result.get(1)).isEqualTo(childCategory);
    }

    @Test
    void shouldGetCategoryHierarchyForRootCategory() {
        // Given
        // When
        List<Category> result = categoryService.getCategoryHierarchy(parentCategory);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(parentCategory);
    }

    @Test
    void shouldCountProductsInCategory() {
        // Given
        when(categoryRepository.countProductsInCategory(1L)).thenReturn(5L);

        // When
        long result = categoryService.countProductsInCategory(1L);

        // Then
        assertThat(result).isEqualTo(5L);
        verify(categoryRepository).countProductsInCategory(1L);
    }
}