package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.PartCodeReference;
import com.recicar.marketplace.repository.PartCodeReferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PartCodeReferenceService {

    private final PartCodeReferenceRepository partCodeReferenceRepository;

    public PartCodeReferenceService(PartCodeReferenceRepository partCodeReferenceRepository) {
        this.partCodeReferenceRepository = partCodeReferenceRepository;
    }

    public List<PartCodeReference> listAll() {
        return partCodeReferenceRepository.findAllByOrderBySortOrderAscIdAsc();
    }
}
