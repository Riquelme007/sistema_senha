package com.securepm.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

import java.util.EnumMap;
import java.util.Map;

/**
 * Utilitário para gerar QR Codes (PNG) para TOTP (2FA).
 */
public class QRCodeUtil {

    /**
     * Gera PNG de QR Code a partir de texto (URI otpauth).
     * @param data      texto, ex.: otpauth://totp/…
     * @param width     largura em pixels (ex.: 200)
     * @param height    altura em pixels (ex.: 200)
     * @return array de bytes do PNG gerado
     * @throws Exception se falhar geração
     */
    public static byte[] generateQRCodePng(String data, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = new MultiFormatWriter()
                .encode(data, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
