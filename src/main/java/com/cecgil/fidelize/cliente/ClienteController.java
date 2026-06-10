package com.cecgil.fidelize.cliente;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import com.cecgil.fidelize.fidelidade.visita.Visita;
import com.cecgil.fidelize.fidelidade.visita.VisitaRepository;
import com.cecgil.fidelize.verificacao.OtpService;


@Controller
@RequestMapping("/c")
public class ClienteController {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final OtpService otpService;

    public ClienteController(EmpresaRepository empresaRepository,
                             ClienteRepository clienteRepository,
                             VisitaRepository visitaRepository,
                             RecompensaRepository recompensaRepository,
                             ResgateRepository resgateRepository,
                             OtpService otpService) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.visitaRepository = visitaRepository;
        this.recompensaRepository = recompensaRepository;
        this.resgateRepository = resgateRepository;
        this.otpService = otpService;
    }

    // ── Etapa 1: formulário de dados ────────────────────────────────────────

    @GetMapping("/{empresaId}")
    public String telaCliente(@PathVariable UUID empresaId, Model model) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        model.addAttribute("empresa", empresa);
        return "cliente/registro";
    }

    @PostMapping("/{empresaId}/solicitar")
    public String solicitarCodigo(@PathVariable UUID empresaId,
                                   @RequestParam String nome,
                                   @RequestParam String telefone,
                                   @RequestParam String email,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (!empresa.isFidelidadeAtiva()) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("erro", "Programa de fidelidade temporariamente indisponível");
            return "cliente/registro";
        }

        otpService.solicitarCodigo(empresaId, telefone, email);

        redirectAttributes.addAttribute("nome", nome);
        redirectAttributes.addAttribute("telefone", telefone);
        redirectAttributes.addAttribute("email", email);
        return "redirect:/c/" + empresaId + "/verificar";
    }

    // ── Etapa 2: verificação do código ──────────────────────────────────────

    @GetMapping("/{empresaId}/verificar")
    public String telaVerificacao(@PathVariable UUID empresaId,
                                   @RequestParam String nome,
                                   @RequestParam String telefone,
                                   @RequestParam String email,
                                   Model model) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        model.addAttribute("empresa", empresa);
        model.addAttribute("nome", nome);
        model.addAttribute("telefone", telefone);
        model.addAttribute("email", email);
        return "cliente/verificar";
    }

    @PostMapping("/{empresaId}/verificar")
    public String verificarERegistrar(@PathVariable UUID empresaId,
                                       @RequestParam String nome,
                                       @RequestParam String telefone,
                                       @RequestParam String email,
                                       @RequestParam String codigo,
                                       Model model) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (!otpService.validar(empresaId, telefone, codigo)) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("nome", nome);
            model.addAttribute("telefone", telefone);
            model.addAttribute("email", email);
            model.addAttribute("erro", "Código inválido ou expirado. Tente novamente.");
            return "cliente/verificar";
        }

        Cliente cliente = clienteRepository
                .findByTelefoneAndEmpresa(telefone, empresa)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setNome(nome);
                    novo.setTelefone(telefone);
                    novo.setEmail(email);
                    novo.setEmpresa(empresa);
                    return clienteRepository.save(novo);
                });

        if (cliente.getEmail() == null) {
            cliente.setEmail(email);
            clienteRepository.save(cliente);
        }

        LocalDateTime limite = LocalDateTime.now().minusHours(empresa.getIntervaloMinimoHoras());
        boolean jaRegistrou = visitaRepository.existsByClienteAndRegistradaEmAfter(cliente, limite);

        if (jaRegistrou) {
            model.addAttribute("aviso",
                    "Visita já registrada nas últimas " + empresa.getIntervaloMinimoHoras() + " horas. Volte em breve 😉");
        } else {
            visitaRepository.save(new Visita(null, cliente, LocalDateTime.now()));
        }

        long totalVisitas = resgateRepository
                .findTopByClienteAndStatusOrderByUtilizadoEmDesc(cliente, StatusResgate.UTILIZADO)
                .map(r -> visitaRepository.countByClienteAndRegistradaEmAfter(cliente, r.getUtilizadoEm()))
                .orElse(visitaRepository.countByCliente(cliente));

        Recompensa recompensa = recompensaRepository
                .findByEmpresaAndAtivaTrue(empresa)
                .stream().findFirst().orElse(null);

        model.addAttribute("empresa", empresa);
        model.addAttribute("cliente", cliente);
        model.addAttribute("totalVisitas", totalVisitas);
        model.addAttribute("recompensa", recompensa);
        model.addAttribute("podeResgatar",
                recompensa != null && totalVisitas >= empresa.getVisitasParaRecompensa());

        return "cliente/sucesso";
    }
}
