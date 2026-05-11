package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.BrandModel;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.repository.BrandModelRepository;
import com.recicar.marketplace.repository.BrandRepository;
import com.recicar.marketplace.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = PortalNavigationCachingTest.CacheTestConfig.class)
class PortalNavigationCachingTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandModelRepository brandModelRepository;

    @Autowired
    private CacheManager cacheManager;

    private Category rootCategory;
    private Brand brand;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        clearInvocations(categoryRepository, brandRepository, brandModelRepository);

        rootCategory = new Category();
        rootCategory.setId(1L);
        rootCategory.setName("Motor");
        rootCategory.setSlug("motor");
        rootCategory.setActive(true);

        brand = new Brand();
        brand.setId(1L);
        brand.setName("BMW");
        brand.setSlug("bmw");

        when(categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc())
                .thenReturn(List.of(rootCategory));
        when(categoryRepository.findBySlug("motor"))
                .thenReturn(Optional.of(rootCategory));
        when(categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAsc(1L))
                .thenReturn(List.of(rootCategory));
        when(brandRepository.findAllByOrderByNameAsc())
                .thenReturn(List.of(brand));
        when(brandRepository.findBySlug("bmw"))
                .thenReturn(Optional.of(brand));
        when(brandModelRepository.findByBrandIdOrderByModelNameAsc(1L))
                .thenReturn(List.of(model("3-series"), model("5-series")));
    }

    @Test
    void categoryRoots_areServedFromCacheAfterFirstLoad() {
        List<Category> first = categoryService.findRootCategories();
        List<Category> second = categoryService.findRootCategories();

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
        verify(categoryRepository, times(1)).findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    }

    @Test
    void categoryChildrenLookup_isCached() {
        categoryService.findByParentId(1L);
        categoryService.findByParentId(1L);

        verify(categoryRepository, times(1)).findByParentIdAndActiveTrueOrderBySortOrderAsc(1L);
    }

    @Test
    void brandCatalogAndModels_areCached() {
        brandService.findAll();
        brandService.findAll();
        brandService.findBySlug("bmw");
        brandService.findBySlug("bmw");
        brandService.findByBrandId(1L);
        brandService.findByBrandId(1L);

        verify(brandRepository, times(1)).findAllByOrderByNameAsc();
        verify(brandRepository, times(1)).findBySlug("bmw");
        verify(brandModelRepository, times(1)).findByBrandIdOrderByModelNameAsc(1L);
    }

    private static BrandModel model(String slug) {
        BrandModel model = new BrandModel();
        model.setSlug(slug);
        model.setModelName(slug);
        return model;
    }

    @Configuration
    @EnableCaching
    @EnableTransactionManagement
    static class CacheTestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(
                    "navCategoryBySlug",
                    "navCategoryRoots",
                    "navCategoryChildren",
                    "navCategoryAll",
                    "navBrands",
                    "navBrandBySlug",
                    "navBrandSlugs",
                    "navBrandModels"
            );
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new NoOpTransactionManager();
        }

        @Bean
        CategoryRepository categoryRepository() {
            return mock(CategoryRepository.class);
        }

        @Bean
        BrandRepository brandRepository() {
            return mock(BrandRepository.class);
        }

        @Bean
        BrandModelRepository brandModelRepository() {
            return mock(BrandModelRepository.class);
        }

        @Bean
        CategoryService categoryService(CategoryRepository categoryRepository) {
            return new CategoryService(categoryRepository);
        }

        @Bean
        BrandService brandService(BrandRepository brandRepository, BrandModelRepository brandModelRepository) {
            return new BrandService(brandRepository, brandModelRepository);
        }
    }

    static class NoOpTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            // no-op
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            // no-op
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            // no-op
        }
    }
}
