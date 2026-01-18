package com.cecgil.fidelize.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import com.cecgil.fidelize.fidelidade.visita.VisitaRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final ResgateRepository resgateRepository;

    public AdminController(EmpresaRepository empresaRepository,
                           ClienteRepository clienteRepository,
                           VisitaRepository visitaRepository,
                           ResgateRepository resgateRepository) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.visitaRepository = visitaRepository;
        this.resgateRepository = resgateRepository;
    }

    @GetMapping("/{empresaId}")
    public String painel(@PathVariable UUID empresaId, Model model) {

        var empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        // resumo
        long totalClientes = clienteRepository.countByEmpresaId(empresaId);
        long totalVisitas = visitaRepository.countByClienteEmpresaId(empresaId);
        long totalResgates = resgateRepository.countByCliente_Empresa_IdAndStatus(empresaId, StatusResgate.UTILIZADO);

        PainelAdminResumo resumo = new PainelAdminResumo(totalClientes, totalVisitas, totalResgates);

        // últimos 10 resgates
        List<UltimoResgateView> ultimosResgates = resgateRepository
                .findTop10ByCliente_Empresa_IdAndStatusOrderByUtilizadoEmDesc(empresaId, StatusResgate.UTILIZADO)
                .stream()
                .map(r -> new UltimoResgateView(
                        r.getCliente().getNome(),
                        r.getRecompensa().getNome(),
                        r.getUtilizadoEm()
                ))
                .toList();

        // clientes + ciclo atual + última visita
        List<ClientePainelView> clientes = clienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId)
                .stream()
                .map(c -> {

                    LocalDateTime ultimoResgate = resgateRepository
                            .findTopByClienteAndStatusOrderByUtilizadoEmDesc(c, StatusResgate.UTILIZADO)
                            .map(r -> r.getUtilizadoEm())
                            .orElse(LocalDateTime.MIN);

                    long cicloAtual = visitaRepository.countByClienteAndRegistradaEmAfter(c, ultimoResgate);

                    LocalDateTime ultimaVisitaEm = visitaRepository
                            .findTopByClienteOrderByRegistradaEmDesc(c)
                            .map(v -> v.getRegistradaEm())
                            .orElse(null);

                    return new ClientePainelView(
                            c.getId(),
                            c.getNome(),
                            c.getTelefone(),
                            cicloAtual,
                            ultimaVisitaEm
                    );
                })
                .toList();

        model.addAttribute("empresa", empresa);
        model.addAttribute("resumo", resumo);
        model.addAttribute("clientes", clientes);
        model.addAttribute("ultimosResgates", ultimosResgates);

        return "admin/painel";
    }
}