package com.cecgil.fidelize.admin;

import com.cecgil.fidelize.empresa.Empresa;
import com.cecgil.fidelize.empresa.EmpresaRepository;
import com.cecgil.fidelize.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/config")
public class EmpresaConfigController {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public EmpresaConfigController(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public String editar(Authentication auth, Model model) {

        var usuario = usuarioRepository
                .findByUsername(auth.getName())
                .orElseThrow();

        model.addAttribute("empresa", usuario.getEmpresa());
        return "admin/config";
    }

    @PostMapping
    public String salvar(@ModelAttribute Empresa empresaForm,
                         Authentication auth) {

        var usuario = usuarioRepository
                .findByUsername(auth.getName())
                .orElseThrow();

        Empresa empresa = usuario.getEmpresa();

        empresa.setVisitasParaRecompensa(empresaForm.getVisitasParaRecompensa());
        empresa.setIntervaloMinimoHoras(empresaForm.getIntervaloMinimoHoras());
        empresa.setFidelidadeAtiva(empresaForm.isFidelidadeAtiva());

        empresaRepository.save(empresa);

        return "redirect:/admin/" + empresa.getId();
    }
}
