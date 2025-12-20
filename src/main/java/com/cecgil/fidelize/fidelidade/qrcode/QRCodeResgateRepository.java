package com.cecgil.fidelize.fidelidade.qrcode;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QRCodeResgateRepository extends JpaRepository<QRCodeResgate, UUID> {
    Optional<QRCodeResgate> findByToken(String token);
    
}
