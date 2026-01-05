package com.cecgil.fidelize.fidelidade.resgate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cecgil.fidelize.cliente.Cliente;
import com.cecgil.fidelize.cliente.ClienteRepository;
import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgate;
import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgateService;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;

import java.util.UUID;

@Controller
@RequestMapping("/resgate")
public class ResgateController {

    private final ClienteRepository clienteRepository;
    private final RecompensaRepository recompensaRepository;
    private final ResgateRepository resgateRepository;
    private final QRCodeResgateService qrService;

    public ResgateController(ClienteRepository clienteRepository,
                             RecompensaRepository recompensaRepository,
                             ResgateRepository resgateRepository,
                             QRCodeResgateService qrService) {
        this.clienteRepository = clienteRepository;
        this.recompensaRepository = recompensaRepository;
        this.resgateRepository = resgateRepository;
        this.qrService = qrService;
    }

    @GetMapping("/{clienteId}/{recompensaId}")
    public String solicitar(@PathVariable UUID clienteId,
                             @PathVariable UUID recompensaId,
                             Model model) {

        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();
        Recompensa recompensa = recompensaRepository.findById(recompensaId).orElseThrow();

        Resgate resgate = new Resgate();
        resgate.setCliente(cliente);
        resgate.setRecompensa(recompensa);
        resgateRepository.save(resgate);

        QRCodeResgate qr = qrService.gerar(resgate);

        model.addAttribute("token", qr.getToken());
        return "cliente/qrcode";
    }
}
