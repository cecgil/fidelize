package com.cecgil.fidelize.verificacao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final int MAX_SOLICITACOES = 3;
    private static final int JANELA_MINUTOS = 15;

    private record OtpEntry(String codigo, LocalDateTime expira) {}

    private final ConcurrentHashMap<String, OtpEntry> otps = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<LocalDateTime>> rateLimits = new ConcurrentHashMap<>();
    private final EmailService emailService;

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Verifica se o telefone excedeu o limite de solicitações.
     * @return true se bloqueado (rate limited)
     */
    public boolean isRateLimited(UUID empresaId, String telefone) {
        String chave = chaveRate(empresaId, telefone);
        List<LocalDateTime> tentativas = rateLimits.get(chave);
        if (tentativas == null) return false;

        LocalDateTime limite = LocalDateTime.now().minusMinutes(JANELA_MINUTOS);
        long recentes = tentativas.stream().filter(t -> t.isAfter(limite)).count();
        return recentes >= MAX_SOLICITACOES;
    }

    /** Gera código de 6 dígitos, armazena e envia por email. Válido por 5 minutos. */
    public void solicitarCodigo(UUID empresaId, String telefone, String email) {
        registrarTentativa(empresaId, telefone);

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

    private void registrarTentativa(UUID empresaId, String telefone) {
        String chave = chaveRate(empresaId, telefone);
        LocalDateTime limite = LocalDateTime.now().minusMinutes(JANELA_MINUTOS);

        rateLimits.compute(chave, (k, tentativas) -> {
            if (tentativas == null) tentativas = new ArrayList<>();
            tentativas.removeIf(t -> t.isBefore(limite));
            tentativas.add(LocalDateTime.now());
            return tentativas;
        });
    }

    private String chave(UUID empresaId, String telefone) {
        return empresaId + ":" + telefone.replaceAll("\\D", "");
    }

    private String chaveRate(UUID empresaId, String telefone) {
        return "rate:" + empresaId + ":" + telefone.replaceAll("\\D", "");
    }
}
