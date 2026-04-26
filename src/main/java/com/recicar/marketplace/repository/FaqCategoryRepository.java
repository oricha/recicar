package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.FaqCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqCategoryRepository extends JpaRepository<FaqCategory, Long> {

    @EntityGraph(attributePaths = { "entries" })
    List<FaqCategory> findAllByOrderBySortOrderAscIdAsc();
}
