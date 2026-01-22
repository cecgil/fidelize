package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.usuario.Role;
import com.cecgil.fidelize.usuario.Usuario;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioAdminController(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {

        var admin = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresaId = admin.getEmpresa().getId();

        model.addAttribute("usuarios",
                usuarioRepository.findByEmpresaIdOrderByUsernameAsc(empresaId));

        return "admin/usuarios/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/usuarios/form";
    }

    @PostMapping
    public String criar(Authentication auth,
                        @RequestParam String username,
                        @RequestParam String senha,
                        @RequestParam Role role) {

        var admin = usuarioRepository.findByUsername(auth.getName()).orElseThrow();

        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/admin/usuarios?erro";
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setSenha(encoder.encode(senha));
        u.setEmpresa(admin.getEmpresa());
        u.setAtivo(true);
        u.setRole(role);

        usuarioRepository.save(u);

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(Authentication auth, @PathVariable java.util.UUID id) {

        var admin = usuarioRepository.findByUsername(auth.getName()).orElseThrow();

        Usuario u = usuarioRepository.findById(id).orElseThrow();

        // segurança: só mexe em usuário da mesma empresa
        if (!u.getEmpresa().getId().equals(admin.getEmpresa().getId())) {
            return "redirect:/admin/usuarios";
        }

        u.setAtivo(!u.isAtivo());
        usuarioRepository.save(u);

        return "redirect:/admin/usuarios";
    }
}
