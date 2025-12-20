package com.cecgil.fidelize.fidelidade.qrcode;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.cecgil.fidelize.fidelidade.resgate.Resgate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResgate {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    private Resgate resgate;

    private String token;

    private LocalDateTime expiraEm;

    private boolean usado = false;
}
