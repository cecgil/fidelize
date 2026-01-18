package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import com.cecgil.fidelize.fidelidade.visita.VisitaRepository;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final ResgateRepository resgateRepository;

    public AdminController(UsuarioRepository usuarioRepository,
                           ClienteRepository clienteRepository,
                           VisitaRepository visitaRepository,
                           ResgateRepository resgateRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.visitaRepository = visitaRepository;
        this.resgateRepository = resgateRepository;
    }

    @GetMapping("/admin/painel")
    public String painel(org.springframework.security.core.Authentication auth, Model model) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();
        var empresaId = empresa.getId();

        long totalClientes = clienteRepository.countByEmpresaId(empresaId);
        long totalVisitas = visitaRepository.countByCliente_Empresa_Id(empresaId);
        long totalResgates = resgateRepository.countByCliente_Empresa_IdAndStatus(empresaId, StatusResgate.UTILIZADO);

        PainelAdminResumo resumo = new PainelAdminResumo(totalClientes, totalVisitas, totalResgates);

        var ultimosResgates = resgateRepository
                .findTop10ByCliente_Empresa_IdAndStatusOrderByUtilizadoEmDesc(empresaId, StatusResgate.UTILIZADO)
                .stream()
                .map(r -> new UltimoResgateView(
                        r.getCliente().getNome(),
                        r.getRecompensa().getNome(),
                        r.getUtilizadoEm()
                ))
                .toList();

        var clientes = clienteRepository.findByEmpresaIdOrderByNomeAsc(empresaId)
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
