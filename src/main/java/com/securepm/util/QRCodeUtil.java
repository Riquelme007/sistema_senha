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
 * Utilitário para geração de códigos QR em formato PNG.
 */
public class QRCodeUtil {

    /**
     * Gera um código QR codificado em PNG a partir dos dados fornecidos.
     *
     * @param data Conteúdo que será codificado no QR Code.
     * @param width Largura da imagem do QR Code.
     * @param height Altura da imagem do QR Code.
     * @return Array de bytes contendo a imagem PNG do QR Code.
     * @throws Exception Caso ocorra algum erro na geração do QR Code.
     */
    public static byte[] generateQRCodePng(String data, int width, int height) throws Exception {
        // Configurações opcionais para a geração do QR Code, como margem mínima.
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1); // Define margem mínima ao redor do QR Code

        // Gera a matriz de bits que representa o QR Code com os parâmetros informados.
        BitMatrix matrix = new MultiFormatWriter()
                .encode(data, BarcodeFormat.QR_CODE, width, height, hints);

        // Stream para armazenar a imagem PNG em memória.
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        // Converte a matriz de bits em uma imagem PNG e escreve no stream.
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOutputStream);

        // Retorna os bytes da imagem PNG gerada.
        return pngOutputStream.toByteArray();
    }
}
