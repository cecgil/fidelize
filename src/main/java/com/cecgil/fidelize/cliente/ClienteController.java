package com.cecgil.fidelize.cliente;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;
import com.cecgil.fidelize.fidelidade.visita.Visita;
import com.cecgil.fidelize.fidelidade.visita.VisitaRepository;


@Controller
@RequestMapping("/c")
public class ClienteController {

    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final RecompensaRepository recompensaRepository;

    public ClienteController(EmpresaRepository empresaRepository,
                             ClienteRepository clienteRepository,
                             VisitaRepository visitaRepository,
                             RecompensaRepository recompensaRepository) {
        this.empresaRepository = empresaRepository;
        this.clienteRepository = clienteRepository;
        this.visitaRepository = visitaRepository;
        this.recompensaRepository = recompensaRepository;
    }

    @GetMapping("/{empresaId}")
    public String telaCliente(@PathVariable UUID empresaId, Model model) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        model.addAttribute("empresa", empresa);
        return "cliente/registro";
    }

    @PostMapping("/{empresaId}")
    public String registrarVisita(@PathVariable UUID empresaId,
                                @RequestParam String nome,
                                @RequestParam String telefone,
                                Model model) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Cliente cliente = clienteRepository
                .findByTelefoneAndEmpresa(telefone, empresa)
                .orElseGet(() -> {
                    Cliente novo = new Cliente();
                    novo.setNome(nome);
                    novo.setTelefone(telefone);
                    novo.setEmpresa(empresa);
                    return clienteRepository.save(novo);
                });

        visitaRepository.save(new Visita(null, cliente, null));

        long totalVisitas = visitaRepository.countByCliente(cliente);

        // busca UMA recompensa ativa (MVP)
        Recompensa recompensa = recompensaRepository
                .findByEmpresaAndAtivaTrue(empresa)
                .stream()
                .findFirst()
                .orElse(null);

        model.addAttribute("empresa", empresa);
        model.addAttribute("cliente", cliente);
        model.addAttribute("totalVisitas", totalVisitas);
        model.addAttribute("recompensa", recompensa);

        boolean podeResgatar = recompensa != null && totalVisitas >= recompensa.getCustoVisitas();
        model.addAttribute("podeResgatar", podeResgatar);

        return "cliente/sucesso";
    }
}
