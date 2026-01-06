package com.cecgil.fidelize.fidelidade.visita;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.cliente.Cliente;

public interface VisitaRepository extends JpaRepository<Visita, UUID> {
        long countByCliente(Cliente cliente);

        long countByClienteAndRegistradaEmAfter(Cliente cliente, LocalDateTime data);

        boolean existsByClienteAndRegistradaEmAfter(Cliente cliente, LocalDateTime data);

}
