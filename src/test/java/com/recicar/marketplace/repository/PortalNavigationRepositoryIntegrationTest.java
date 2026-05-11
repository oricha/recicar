package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.BrandModel;
import com.recicar.marketplace.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PortalNavigationRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandModelRepository brandModelRepository;

    @Test
    void categorySeed_supportsRootAndHierarchyNavigation() {
        List<Category> roots = categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
        assertThat(roots).hasSizeGreaterThanOrEqualTo(20);

        Optional<Category> motor = categoryRepository.findBySlug("motor");
        assertThat(motor).isPresent();

        List<Category> children = categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAsc(motor.get().getId());
        assertThat(children).extracting(Category::getSlug)
                .contains("motor-bloque", "motor-alimentacion-escape");

        Optional<Category> deepLeaf = categoryRepository.findBySlug("motor-bloque-cigueñal-cojinetes");
        assertThat(deepLeaf).isPresent();
        assertThat(deepLeaf.get().getParent()).isNotNull();
        assertThat(deepLeaf.get().getParent().getSlug()).isEqualTo("motor-bloque-cigueñal");
        assertThat(deepLeaf.get().getParent().getParent()).isNotNull();
        assertThat(deepLeaf.get().getParent().getParent().getSlug()).isEqualTo("motor-bloque");
    }

    @Test
    void brandSeed_exposesLargeCatalogAndLinkedModels() {
        List<Brand> brands = brandRepository.findAllByOrderByNameAsc();
        assertThat(brands).hasSizeGreaterThanOrEqualTo(200);

        Optional<Brand> bmw = brandRepository.findBySlug("bmw");
        assertThat(bmw).isPresent();

        List<BrandModel> models = brandModelRepository.findByBrandIdOrderByModelNameAsc(bmw.get().getId());
        assertThat(models).extracting(BrandModel::getSlug)
                .contains("3-series", "5-series");
    }
}
