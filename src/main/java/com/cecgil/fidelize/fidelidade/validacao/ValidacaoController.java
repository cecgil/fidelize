package com.cecgil.fidelize.fidelidade.validacao;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgate;
import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgateService;
import com.cecgil.fidelize.fidelidade.resgate.Resgate;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;

@Controller
@RequestMapping("/validar")
public class ValidacaoController {

    private final QRCodeResgateService qrService;
    private final ResgateRepository resgateRepository;

    public ValidacaoController(QRCodeResgateService qrService,
                               ResgateRepository resgateRepository) {
        this.qrService = qrService;
        this.resgateRepository = resgateRepository;
    }

    @GetMapping("/{token}")
    public String validar(@PathVariable String token, Model model) {

        QRCodeResgate qr = qrService.validar(token);
        model.addAttribute("resgate", qr.getResgate());
        model.addAttribute("token", token);
        return "admin/confirmar";
    }

    @PostMapping("/{token}/confirmar")
    public String confirmar(@PathVariable String token, Model model) {

        QRCodeResgate qr = qrService.validar(token);
        Resgate resgate = qr.getResgate();

        resgate.setStatus(StatusResgate.UTILIZADO);
        resgate.setUtilizadoEm(LocalDateTime.now());
        qr.setUsado(true);

        resgateRepository.save(resgate);

        model.addAttribute("cliente", resgate.getCliente().getNome());
        model.addAttribute("recompensa", resgate.getRecompensa().getNome());
        model.addAttribute("data", resgate.getUtilizadoEm());

        return "admin/sucesso";
    }

}
