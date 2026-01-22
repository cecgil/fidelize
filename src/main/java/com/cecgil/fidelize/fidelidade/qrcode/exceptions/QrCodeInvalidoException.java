package com.cecgil.fidelize.fidelidade.qrcode.exceptions;

public class QrCodeInvalidoException extends RuntimeException {
    public QrCodeInvalidoException() {
        super("QR Code inválido");
    }
}
