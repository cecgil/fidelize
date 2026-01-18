package com.cecgil.fidelize.config;

import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.empresa.Segmento;
import com.cecgil.fidelize.usuario.Usuario;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            PasswordEncoder encoder
    ) {
        return args -> {

            System.out.println("✅ DataInitializer executando...");

            // 1) garante empresa
            Empresa empresa = empresaRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Empresa e = new Empresa();
                        e.setNome("Empresa Demo");
                        e.setSegmento(Segmento.BARBEARIA);
                        e.setAtiva(true);
                        e.setFidelidadeAtiva(true);
                        e.setIntervaloMinimoHoras(24);
                        e.setVisitasParaRecompensa(10);
                        System.out.println("✅ Criando Empresa Demo...");
                        return empresaRepository.save(e);
                    });

            // 2) garante admin
            boolean existeAdmin = usuarioRepository.findByUsername("admin").isPresent();
            if (!existeAdmin) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setSenha(encoder.encode("123456"));
                admin.setEmpresa(empresa);
                admin.setAtivo(true);

                usuarioRepository.save(admin);
                System.out.println("✅ Usuário admin criado (senha 123456).");
            } else {
                System.out.println("ℹ️ Usuário admin já existe.");
            }
        };
    }
}
