package com.cecgil.fidelize.fidelidade.qrcode.exceptions;

public class QrCodeExpiradoException extends RuntimeException {
    public QrCodeExpiradoException() {
        super("QR Code expirado");
    }
}
