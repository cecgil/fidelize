package com.cecgil.fidelize.fidelidade.qrcode;

import org.springframework.stereotype.Service;

import com.cecgil.fidelize.fidelidade.resgate.Resgate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QRCodeResgateService {

    private final QRCodeResgateRepository repository;

    public QRCodeResgateService(QRCodeResgateRepository repository) {
        this.repository = repository;
    }

    public QRCodeResgate gerar(Resgate resgate) {
        QRCodeResgate qr = new QRCodeResgate();
        qr.setResgate(resgate);
        qr.setToken(UUID.randomUUID().toString());
        qr.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        qr.setUsado(false);
        return repository.save(qr);
    }

    public QRCodeResgate validar(String token) {
        QRCodeResgate qr = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("QR Code inválido"));

        if (qr.isUsado() || qr.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("QR Code expirado ou já utilizado");
        }

        return qr;
    }
}
