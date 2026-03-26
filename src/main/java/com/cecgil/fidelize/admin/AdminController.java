package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import com.cecgil.fidelize.fidelidade.visita.VisitaRepository;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final ResgateRepository resgateRepository;
    private final RecompensaRepository recompensaRepository;

    public AdminController(UsuarioRepository usuarioRepository,
                           ClienteRepository clienteRepository,
                           VisitaRepository visitaRepository,
                           ResgateRepository resgateRepository,
                           RecompensaRepository recompensaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.visitaRepository = visitaRepository;
        this.resgateRepository = resgateRepository;
        this.recompensaRepository = recompensaRepository;
    }

    private static final int CLIENTES_POR_PAGINA = 20;

    @GetMapping("/admin/painel")
    public String painel(org.springframework.security.core.Authentication auth,
                         @RequestParam(defaultValue = "0") int pagina,
                         HttpServletRequest request,
                         Model model) {

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

        var paginaClientes = clienteRepository.findByEmpresaIdOrderByNomeAsc(
                empresaId, PageRequest.of(pagina, CLIENTES_POR_PAGINA));

        var clientes = paginaClientes.stream()
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

                    return new ClientePainelView(c.getId(), c.getNome(), c.getTelefone(), cicloAtual, ultimaVisitaEm);
                })
                .toList();

        boolean temRecompensa = !recompensaRepository.findByEmpresaAndAtivaTrue(empresa).isEmpty();

        int porta = request.getServerPort();
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (porta == 80 || porta == 443 ? "" : ":" + porta);
        String linkCliente = baseUrl + "/c/" + empresaId;

        model.addAttribute("empresa", empresa);
        model.addAttribute("resumo", resumo);
        model.addAttribute("clientes", clientes);
        model.addAttribute("ultimosResgates", ultimosResgates);
        model.addAttribute("paginaAtual", paginaClientes.getNumber());
        model.addAttribute("totalPaginas", paginaClientes.getTotalPages());
        model.addAttribute("temRecompensa", temRecompensa);
        model.addAttribute("linkCliente", linkCliente);

        return "admin/painel";
    }
}
