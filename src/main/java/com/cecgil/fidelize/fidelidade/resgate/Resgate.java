package com.cecgil.fidelize.fidelidade.resgate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.cecgil.fidelize.cliente.Cliente;
import com.cecgil.fidelize.fidelidade.recompensa.Recompensa;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resgate {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Recompensa recompensa;

    @Enumerated(EnumType.STRING)
    private StatusResgate status = StatusResgate.PENDENTE;

    private LocalDateTime criadoEm = LocalDateTime.now();
    private LocalDateTime utilizadoEm;
}
