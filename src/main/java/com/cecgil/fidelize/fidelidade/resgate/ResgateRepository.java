package com.cecgil.fidelize.fidelidade.resgate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.cliente.Cliente;

public interface ResgateRepository extends JpaRepository<Resgate, UUID> {
    Optional<Resgate> findByIdAndStatus(UUID id, StatusResgate status);

    Optional<Resgate> findTopByClienteAndStatusOrderByUtilizadoEmDesc(
        Cliente cliente,
        StatusResgate status
    );
    
}
