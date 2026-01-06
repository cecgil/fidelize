package com.cecgil.fidelize.usuario;

import com.cecgil.fidelize.empresa.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String senha;

    @ManyToOne(optional = false)
    private Empresa empresa;

    private boolean ativo = true;
}
