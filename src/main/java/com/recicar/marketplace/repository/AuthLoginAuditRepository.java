package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.AuthLoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLoginAuditRepository extends JpaRepository<AuthLoginAudit, Long> {
}
