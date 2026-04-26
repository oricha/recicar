package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.PartCodeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartCodeReferenceRepository extends JpaRepository<PartCodeReference, Long> {

    List<PartCodeReference> findAllByOrderBySortOrderAscIdAsc();
}
