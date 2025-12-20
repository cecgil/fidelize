package com.cecgil.fidelize.fidelidade.recompensa;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import com.cecgil.fidelize.empresa.Empresa;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    private int custoVisitas;

    private boolean ativa = true;

    @ManyToOne(optional = false)
    private Empresa empresa;
}