package com.cecgil.fidelize.admin;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientePainelView(
        UUID clienteId,
        String nome,
        String telefone,
        long cicloAtualVisitas,
        LocalDateTime ultimaVisitaEm
) {}
