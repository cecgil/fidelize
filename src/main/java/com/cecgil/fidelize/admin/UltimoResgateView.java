package com.cecgil.fidelize.admin;

import java.time.LocalDateTime;

public record UltimoResgateView(
        String clienteNome,
        String recompensaNome,
        LocalDateTime utilizadoEm
) {}
