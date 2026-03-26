package com.cecgil.fidelize.fidelidade.qrcode;

import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeExpiradoException;
import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeInvalidoException;
import com.cecgil.fidelize.fidelidade.qrcode.exceptions.QrCodeJaUsadoException;
import com.cecgil.fidelize.fidelidade.resgate.Resgate;
import com.cecgil.fidelize.fidelidade.resgate.ResgateRepository;
import com.cecgil.fidelize.fidelidade.resgate.StatusResgate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class QRCodeResgateService {

    private final QRCodeResgateRepository repository;
    private final ResgateRepository resgateRepository;

    public QRCodeResgateService(QRCodeResgateRepository repository,
                                ResgateRepository resgateRepository) {
        this.repository = repository;
        this.resgateRepository = resgateRepository;
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
                .orElseThrow(QrCodeInvalidoException::new);

        if (qr.isUsado()) {
            throw new QrCodeJaUsadoException();
        }

        if (qr.getExpiraEm() == null || qr.getExpiraEm().isBefore(LocalDateTime.now())) {
            Resgate resgate = qr.getResgate();
            if (resgate.getStatus() == StatusResgate.PENDENTE) {
                resgate.setStatus(StatusResgate.EXPIRADO);
                resgateRepository.save(resgate);
            }
            throw new QrCodeExpiradoException();
        }

        return qr;
    }

    // Retorna o QR válido de um resgate, se existir (não usado e não expirado)
    public Optional<QRCodeResgate> buscarQrValido(Resgate resgate) {
        return repository.findByResgate(resgate)
                .filter(qr -> !qr.isUsado()
                        && qr.getExpiraEm() != null
                        && qr.getExpiraEm().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public Resgate confirmar(String token) {
        QRCodeResgate qr = validar(token);
        Resgate resgate = qr.getResgate();

        resgate.setStatus(StatusResgate.UTILIZADO);
        resgate.setUtilizadoEm(LocalDateTime.now());
        resgateRepository.save(resgate);

        qr.setUsado(true);
        repository.save(qr);

        return resgate;
    }
}
