package com.cecgil.fidelize.verificacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigo(String email, String codigo) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Fidelize — código de verificação");
            msg.setText(
                "Olá!\n\n" +
                "Seu código de verificação é:\n\n" +
                "   " + codigo + "\n\n" +
                "Válido por 5 minutos.\n\n" +
                "Se você não solicitou este código, ignore esta mensagem."
            );
            mailSender.send(msg);
            log.info("Email enviado para {}", email);
        } catch (Exception e) {
            // Fallback para desenvolvimento: exibe o código nos logs
            log.warn(">>> [EMAIL NÃO ENVIADO] Para: {} | Código: {} | Erro: {}", email, codigo, e.getMessage());
        }
    }
}
