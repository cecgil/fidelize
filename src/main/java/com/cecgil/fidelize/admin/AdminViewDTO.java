package com.cecgil.fidelize.admin;

import java.util.UUID;

public record AdminViewDTO (
    UUID clienteId,
    String nomeCliente,
    String telefone,
    long totalVisitas,
    UUID resgateId,
    String recompensa
) {}
