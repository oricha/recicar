package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.BrandModel;
import com.recicar.marketplace.repository.BrandModelRepository;
import com.recicar.marketplace.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandModelRepository brandModelRepository;

    public BrandService(BrandRepository brandRepository, BrandModelRepository brandModelRepository) {
        this.brandRepository = brandRepository;
        this.brandModelRepository = brandModelRepository;
    }

    @Transactional(readOnly = true)
    public List<Brand> findAll() {
        return brandRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Brand> findBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public List<String> findAllSlugs() {
        return brandRepository.findAllSlugs();
    }

    @Transactional(readOnly = true)
    public List<BrandModel> findByBrandId(Long brandId) {
        return brandModelRepository.findByBrandIdOrderByModelNameAsc(brandId);
    }

    @Transactional(readOnly = true)
    public Optional<BrandModel> findByBrandAndSlug(String brandSlug, String modelSlug) {
        return brandModelRepository.findByBrandSlugAndSlug(brandSlug, modelSlug);
    }
}
