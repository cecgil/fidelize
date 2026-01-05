package com.cecgil.fidelize.admin;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.fidelidade.resgate.Resgate;
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

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        List<AdminViewDTO> dados = clienteRepository.findAll()
                .stream()
                .filter(c -> c.getEmpresa().getId().equals(empresaId))
                .map(cliente -> {
                    long visitas = visitaRepository.countByCliente(cliente);

                    Resgate resgate = resgateRepository
                            .findAll()
                            .stream()
                            .filter(r ->
                                    r.getCliente().getId().equals(cliente.getId())
                                    && r.getStatus() == StatusResgate.PENDENTE
                            )
                            .findFirst()
                            .orElse(null);

                    return new AdminViewDTO(
                            cliente.getId(),
                            cliente.getNome(),
                            cliente.getTelefone(),
                            visitas,
                            resgate != null ? resgate.getId() : null,
                            resgate != null ? resgate.getRecompensa().getNome() : null
                    );
                })
                .collect(Collectors.toList());

        model.addAttribute("empresa", empresa);
        model.addAttribute("dados", dados);

        return "admin/painel";
    }
}