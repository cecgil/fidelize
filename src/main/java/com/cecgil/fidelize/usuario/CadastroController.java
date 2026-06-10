package com.cecgil.fidelize.usuario;

import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.empresa.Segmento;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public CadastroController(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder encoder,
            AuthenticationManager authenticationManager
    ) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/cadastro")
    public String formulario(Model model) {
        model.addAttribute("segmentos", Segmento.values());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nomeEmpresa,
            @RequestParam Segmento segmento,
            @RequestParam String username,
            @RequestParam String senha,
            HttpServletRequest request,
            Model model
    ) {

        // Validação de inputs
        String erro = validarCadastro(nomeEmpresa, username, senha);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("segmentos", Segmento.values());
            model.addAttribute("nomeEmpresa", nomeEmpresa);
            model.addAttribute("username", username);
            return "cadastro";
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            model.addAttribute("erro", "Usuário já existe");
            model.addAttribute("segmentos", Segmento.values());
            model.addAttribute("nomeEmpresa", nomeEmpresa);
            model.addAttribute("username", username);
            return "cadastro";
        }

        Empresa empresa = new Empresa();
        empresa.setNome(nomeEmpresa);
        empresa.setSegmento(segmento);
        empresa.setAtiva(true);
        empresaRepository.save(empresa);

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setSenha(encoder.encode(senha));
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        // login automático
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, senha);

        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );

        return "redirect:/admin/home";
    }

    private String validarCadastro(String nomeEmpresa, String username, String senha) {
        if (nomeEmpresa == null || nomeEmpresa.isBlank()) return "Informe o nome da empresa.";
        if (nomeEmpresa.length() < 2) return "Nome da empresa deve ter pelo menos 2 caracteres.";
        if (nomeEmpresa.length() > 100) return "Nome da empresa muito longo.";

        if (username == null || username.isBlank()) return "Informe o nome de usuário.";
        if (username.length() < 3) return "Usuário deve ter pelo menos 3 caracteres.";
        if (username.length() > 50) return "Usuário muito longo.";
        if (!username.matches("^[a-zA-Z0-9._-]+$")) return "Usuário deve conter apenas letras, números, pontos, hífens ou underscores.";

        if (senha == null || senha.length() < 6) return "Senha deve ter pelo menos 6 caracteres.";

        return null;
    }
}
