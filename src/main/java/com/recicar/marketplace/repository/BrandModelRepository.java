package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.BrandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandModelRepository extends JpaRepository<BrandModel, Long> {

    List<BrandModel> findByBrandIdOrderByModelNameAsc(Long brandId);

    Optional<BrandModel> findByBrandSlugAndSlug(String brandSlug, String slug);
}
