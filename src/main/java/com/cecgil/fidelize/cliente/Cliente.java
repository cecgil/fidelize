package com.cecgil.fidelize.cliente;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import com.cecgil.fidelize.empresa.Empresa;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"telefone", "empresa_id"})
    }
)
public class Cliente {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;

    private String telefone;

    private String email;

    @ManyToOne(optional = false)
    private Empresa empresa;
}