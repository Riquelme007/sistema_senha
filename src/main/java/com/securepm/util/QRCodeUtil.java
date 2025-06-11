package com.securepm.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Classe utilitária que encapsula a lógica de geração de QR Codes
 * utilizando a biblioteca ZXing ("Zebra Crossing").
 */
public class QRCodeUtil {

    /**
     * Cria uma imagem de QR Code em formato PNG a partir de uma string de dados.
     * A imagem é gerada e mantida inteiramente em memória como um array de bytes.
     *
     * @param data   A informação a ser codificada no QR Code (ex: uma URL, um texto).
     * @param width  A largura desejada para a imagem final em pixels.
     * @param height A altura desejada para a imagem final em pixels.
     * @return Um array de bytes que representa a imagem PNG do QR Code gerado.
     * @throws WriterException Se ocorrer um erro durante a fase de codificação dos dados.
     * @throws IOException Se ocorrer um erro ao escrever a imagem no stream em memória.
     */
    public static byte[] generateQRCodePng(String data, int width, int height) throws WriterException, IOException {

        // 1. Prepara um mapa de configurações para o gerador de QR Code.
        // Aqui, definimos uma margem mínima de 1 módulo (pixel) ao redor do código.
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        // 2. Codifica os dados fornecidos em uma matriz de bits (BitMatrix).
        // A BitMatrix é a representação lógica e abstrata do QR Code.
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(data, BarcodeFormat.QR_CODE, width, height, hints);

        // 3. Prepara um stream de saída em memória para receber os bytes da imagem PNG.
        // Isso evita a necessidade de criar um arquivo temporário em disco.
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        // 4. Utiliza a classe MatrixToImageWriter para converter a BitMatrix em uma imagem
        // no formato PNG e escrevê-la diretamente no nosso stream em memória.
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        // 5. Extrai e retorna o array de bytes da imagem que foi gerada no stream.
        return pngOutputStream.toByteArray();
    }
}