package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    List<Brand> findAllByOrderByNameAsc();

    @Query("SELECT b.slug FROM Brand b ORDER BY b.name ASC")
    List<String> findAllSlugs();
}
