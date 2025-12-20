package com.cecgil.fidelize.fidelidade.visita;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.cecgil.fidelize.cliente.Cliente;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Visita {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    private LocalDateTime registradaEm = LocalDateTime.now();
}
