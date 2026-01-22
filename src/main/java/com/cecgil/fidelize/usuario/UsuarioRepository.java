package com.cecgil.fidelize.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByEmpresaIdOrderByUsernameAsc(UUID empresaId);

}
