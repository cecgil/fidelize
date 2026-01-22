package com.cecgil.fidelize.fidelidade.resgate;

import com.cecgil.fidelize.cliente.Cliente;
import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.fidelidade.visita.VisitaService;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.resgate.exceptions.ResgateNegadoException;

import org.springframework.stereotype.Service;

@Service
public class ResgateService {

    private final VisitaService visitaService;

    public ResgateService(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    public void validarPodeResgatar(Empresa empresa, Cliente cliente, Recompensa recompensa) {

        if (!empresa.isFidelidadeAtiva()) {
            throw new ResgateNegadoException("Fidelidade desativada para esta empresa");
        }

        if (!recompensa.isAtiva()) {
            throw new ResgateNegadoException("Recompensa inativa");
        }

        if (!recompensa.getEmpresa().getId().equals(empresa.getId())) {
            throw new ResgateNegadoException("Recompensa não pertence a esta empresa");
        }

        long ciclo = visitaService.cicloAtual(cliente);

        if (ciclo < empresa.getVisitasParaRecompensa()) {
            throw new ResgateNegadoException("Cliente ainda não atingiu visitas suficientes");
        }
    }
}
