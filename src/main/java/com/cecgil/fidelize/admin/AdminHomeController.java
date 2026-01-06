package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    private final UsuarioRepository usuarioRepository;

    public AdminHomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin/home")
    public String home(Authentication auth) {

        var usuario = usuarioRepository
                .findByUsername(auth.getName())
                .orElseThrow();

        return "redirect:/admin/" + usuario.getEmpresa().getId();
    }
}
