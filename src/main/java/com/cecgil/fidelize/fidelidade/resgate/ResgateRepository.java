package com.cecgil.fidelize.fidelidade.resgate;

import java.util.List;
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

    List<Resgate> findTop10ByClienteEmpresaIdAndStatusOrderByUtilizadoEmDesc(
            UUID empresaId, StatusResgate status
    );
    
    long countByCliente_Empresa_IdAndStatus(UUID empresaId, StatusResgate status);

    List<Resgate> findTop10ByCliente_Empresa_IdAndStatusOrderByUtilizadoEmDesc(
            UUID empresaId,
            StatusResgate status
    );
}
