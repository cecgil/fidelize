package com.cecgil.fidelize.cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.empresa.Empresa;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    
    Optional<Cliente> findByTelefoneAndEmpresa(String telefone, Empresa empresa);

    long countByEmpresaId(UUID empresaId);

    List<Cliente> findByEmpresaIdOrderByNomeAsc(UUID empresaId);
}
