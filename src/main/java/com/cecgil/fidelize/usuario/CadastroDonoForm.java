package com.cecgil.fidelize.usuario;

import com.cecgil.fidelize.empresa.Segmento;

public record CadastroDonoForm(
        String nomeEmpresa,
        Segmento segmento,
        String username,
        String senha
) {}