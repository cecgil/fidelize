package com.cecgil.fidelize.config;

import com.cecgil.fidelize.empresa.EmpresaRepository;
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

            if (usuarioRepository.count() > 0) return;

            var empresa = empresaRepository.findAll().stream().findFirst().orElse(null);
            if (empresa == null) return;

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setSenha(encoder.encode("123456"));
            admin.setEmpresa(empresa);
            admin.setAtivo(true);

            usuarioRepository.save(admin);
        };
    }
}
