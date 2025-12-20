package com.cecgil.fidelize.fidelidade.resgate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResgateRepository extends JpaRepository<Resgate, UUID> {
    Optional<Resgate> findByIdAndStatus(UUID id, StatusResgate status);
    
}
