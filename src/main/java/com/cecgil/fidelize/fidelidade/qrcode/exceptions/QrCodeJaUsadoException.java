package com.cecgil.fidelize.fidelidade.qrcode.exceptions;

public class QrCodeJaUsadoException extends RuntimeException {
    public QrCodeJaUsadoException() {
        super("QR Code já utilizado");
    }
}
