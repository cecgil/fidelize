package com.cecgil.fidelize.web;

import com.cecgil.fidelize.cliente.Cliente;
import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgateService;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;
import com.cecgil.fidelize.fidelidade.resgate.Resgate;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.ResgateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/resgate")
public class ResgateController {

    private final ClienteRepository clienteRepository;
    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final QRCodeResgateService qrService;
    private final ResgateService resgateService;

    public ResgateController(ClienteRepository clienteRepository,
                             RecompensaRepository recompensaRepository,
                             ResgateRepository resgateRepository,
                             QRCodeResgateService qrService,
                             ResgateService resgateService) {
        this.clienteRepository = clienteRepository;
        this.recompensaRepository = recompensaRepository;
        this.resgateRepository = resgateRepository;
        this.qrService = qrService;
        this.resgateService = resgateService;
    }

    @PostMapping("/{clienteId}/{recompensaId}")
    public String solicitar(@PathVariable UUID clienteId,
                            @PathVariable UUID recompensaId,
                            Model model) {

        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();
        Recompensa recompensa = recompensaRepository.findById(recompensaId).orElseThrow();

        var empresa = cliente.getEmpresa();

        // ✅ valida se pode resgatar
        resgateService.validarPodeResgatar(empresa, cliente, recompensa);

        Resgate resgate = new Resgate();
        resgate.setCliente(cliente);
        resgate.setRecompensa(recompensa);
        resgateRepository.save(resgate);

        var qr = qrService.gerar(resgate);

        model.addAttribute("token", qr.getToken());
        return "cliente/qrcode";
    }
}
