package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;
import com.cecgil.fidelize.fidelidade.recompensa.RecompensaRepository;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/recompensas")
public class RecompensaAdminController {

    private final UsuarioRepository usuarioRepository;
    private final RecompensaRepository recompensaRepository;

    public RecompensaAdminController(UsuarioRepository usuarioRepository,
                                     RecompensaRepository recompensaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.recompensaRepository = recompensaRepository;
    }

    @GetMapping
    public String listar(org.springframework.security.core.Authentication auth, Model model) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        model.addAttribute("empresa", empresa);
        model.addAttribute("recompensas", recompensaRepository.findByEmpresaOrderByAtivaDescNomeAsc(empresa));

        return "admin/recompensas/listar";
    }

    @GetMapping("/nova")
    public String nova(org.springframework.security.core.Authentication auth, Model model) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("empresa", usuario.getEmpresa());
        model.addAttribute("recompensa", new Recompensa());

        return "admin/recompensas/form";
    }

    @PostMapping
    public String criar(org.springframework.security.core.Authentication auth,
                        @RequestParam String nome) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        Recompensa r = new Recompensa();
        r.setNome(nome);
        r.setEmpresa(empresa);
        r.setAtiva(true);

        recompensaRepository.save(r);

        return "redirect:/admin/recompensas";
    }

    @GetMapping("/{id}/editar")
    public String editar(org.springframework.security.core.Authentication auth,
                         @PathVariable UUID id,
                         Model model) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        Recompensa recompensa = recompensaRepository.findById(id).orElseThrow();

        if (!recompensa.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/admin/recompensas";
        }

        model.addAttribute("empresa", empresa);
        model.addAttribute("recompensa", recompensa);
        return "admin/recompensas/form";
    }

    @PostMapping("/{id}/editar")
    public String salvarEdicao(org.springframework.security.core.Authentication auth,
                               @PathVariable UUID id,
                               @RequestParam String nome) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        Recompensa recompensa = recompensaRepository.findById(id).orElseThrow();

        if (!recompensa.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/admin/recompensas";
        }

        recompensa.setNome(nome);
        recompensaRepository.save(recompensa);

        return "redirect:/admin/recompensas";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(org.springframework.security.core.Authentication auth,
                         @PathVariable UUID id) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        Recompensa recompensa = recompensaRepository.findById(id).orElseThrow();

        if (!recompensa.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/admin/recompensas";
        }

        recompensa.setAtiva(!recompensa.isAtiva());
        recompensaRepository.save(recompensa);

        return "redirect:/admin/recompensas";
    }

    @PostMapping("/{id}/delete")
    public String deletar(org.springframework.security.core.Authentication auth,
                          @PathVariable UUID id) {

        var usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        var empresa = usuario.getEmpresa();

        Recompensa recompensa = recompensaRepository.findById(id).orElseThrow();

        if (!recompensa.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/admin/recompensas";
        }

        recompensaRepository.delete(recompensa);

        return "redirect:/admin/recompensas";
    }
}
