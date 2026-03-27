package com.cecgil.fidelize.web;

import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeExpiradoException;
import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeInvalidoException;
import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeJaUsadoException;
import com.cecgil.fidelize.fidelidade.resgate.exceptions.ResgateNegadoException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QrCodeInvalidoException.class)
    public String qrInvalido(Model model) {
        model.addAttribute("titulo", "QR Code inválido");
        model.addAttribute("mensagem", "Este QR Code não é válido. Gere um novo resgate e tente novamente.");
        return "error/qr";
    }

    @ExceptionHandler(QrCodeExpiradoException.class)
    public String qrExpirado(Model model) {
        model.addAttribute("titulo", "QR Code expirado");
        model.addAttribute("mensagem", "Este QR Code expirou. Peça para o cliente gerar novamente.");
        return "error/qr";
    }

    @ExceptionHandler(QrCodeJaUsadoException.class)
    public String qrUsado(Model model) {
        model.addAttribute("titulo", "QR Code já utilizado");
        model.addAttribute("mensagem", "Este QR Code já foi usado e não pode ser reutilizado.");
        return "error/qr";
    }

    @ExceptionHandler(ResgateNegadoException.class)
    public String resgateNegado(ResgateNegadoException ex, Model model) {
        model.addAttribute("titulo", "Resgate não disponível");
        model.addAttribute("mensagem", ex.getMessage());
        return "error/qr"; // reutiliza o mesmo template (simples)
    }
}
