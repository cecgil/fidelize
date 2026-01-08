package com.cecgil.fidelize.empresa;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Segmento segmento;

    private boolean ativa = true;

    @Column(nullable = false)
    private int visitasParaRecompensa = 10;

    @Column(nullable = false)
    private int intervaloMinimoHoras = 24;

    @Column(nullable = false)
    private boolean fidelidadeAtiva = true;
}
