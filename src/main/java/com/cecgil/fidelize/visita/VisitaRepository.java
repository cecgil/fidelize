package com.cecgil.fidelize.visita;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.cliente.Cliente;

public interface VisitaRepository extends JpaRepository<Visita, UUID> {
        long countByCliente(Cliente cliente);

}
