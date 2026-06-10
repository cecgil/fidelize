package com.cecgil.fidelize.verificacao;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private record OtpEntry(String codigo, LocalDateTime expira) {}

    private final ConcurrentHashMap<String, OtpEntry> otps = new ConcurrentHashMap<>();
    private final EmailService emailService;

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    /** Gera código de 6 dígitos, armazena e envia por email. Válido por 5 minutos. */
    public void solicitarCodigo(UUID empresaId, String telefone, String email) {
        String codigo = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        otps.put(chave(empresaId, telefone), new OtpEntry(codigo, LocalDateTime.now().plusMinutes(5)));
        emailService.enviarCodigo(email, codigo);
    }

    /** Valida o código — uso único, remove após validação bem-sucedida. */
    public boolean validar(UUID empresaId, String telefone, String codigo) {
        String chave = chave(empresaId, telefone);
        OtpEntry entry = otps.get(chave);
        if (entry == null) return false;
        if (entry.expira().isBefore(LocalDateTime.now())) {
            otps.remove(chave);
            return false;
        }
        if (!entry.codigo().equals(codigo)) return false;
        otps.remove(chave);
        return true;
    }

    private String chave(UUID empresaId, String telefone) {
        return empresaId + ":" + telefone.replaceAll("\\D", "");
    }
}
