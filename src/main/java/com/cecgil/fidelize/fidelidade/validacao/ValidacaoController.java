package com.cecgil.fidelize.fidelidade.validacao;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgate;
import com.cecgil.fidelize.fidelidade.qrcode.QRCodeResgateService;
import com.cecgil.fidelize.fidelidade.resgate.Resgate;

@Controller
@RequestMapping("/validar")
public class ValidacaoController {

    private final QRCodeResgateService qrService;

    public ValidacaoController(QRCodeResgateService qrService) {
        this.qrService = qrService;
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
        Resgate resgate = qrService.confirmar(token);
        model.addAttribute("cliente", resgate.getCliente().getNome());
        model.addAttribute("recompensa", resgate.getRecompensa().getNome());
        model.addAttribute("data", resgate.getUtilizadoEm());
        return "admin/sucesso";
    }

}
