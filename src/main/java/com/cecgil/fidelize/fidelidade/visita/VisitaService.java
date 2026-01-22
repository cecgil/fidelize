package com.cecgil.fidelize.fidelidade.visita;

import com.cecgil.fidelize.cliente.Cliente;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final ResgateRepository resgateRepository;

    public VisitaService(VisitaRepository visitaRepository,
                         ResgateRepository resgateRepository) {
        this.visitaRepository = visitaRepository;
        this.resgateRepository = resgateRepository;
    }

    public long cicloAtual(Cliente cliente) {

        LocalDateTime ultimoResgate = resgateRepository
                .findTopByClienteAndStatusOrderByUtilizadoEmDesc(cliente, StatusResgate.UTILIZADO)
                .map(r -> r.getUtilizadoEm())
                .orElse(LocalDateTime.MIN);

        return visitaRepository.countByClienteAndRegistradaEmAfter(cliente, ultimoResgate);
    }
}
