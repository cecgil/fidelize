package com.cecgil.fidelize.fidelidade.recompensa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cecgil.fidelize.empresa.Empresa;

public interface RecompensaRepository extends JpaRepository<Recompensa, UUID> {
        List<Recompensa> findByEmpresaAndAtivaTrue(Empresa empresa);

        List<Recompensa> findByEmpresaOrderByAtivaDescNomeAsc(Empresa empresa);


}
