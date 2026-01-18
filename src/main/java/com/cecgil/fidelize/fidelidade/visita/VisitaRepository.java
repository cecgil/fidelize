package com.cecgil.fidelize.fidelidade.visita;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.cliente.Cliente;

public interface VisitaRepository extends JpaRepository<Visita, UUID> {
        long countByCliente(Cliente cliente);

        long countByClienteAndRegistradaEmAfter(Cliente cliente, LocalDateTime data);

        boolean existsByClienteAndRegistradaEmAfter(Cliente cliente, LocalDateTime data);

        Optional<Visita> findTopByClienteOrderByRegistradaEmDesc(Cliente cliente);

        long countByCliente_Empresa_Id(UUID empresaId);

}
