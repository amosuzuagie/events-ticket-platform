package com.mstra.tickets.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mstra.tickets.domain.entities.QrCode;
import com.mstra.tickets.domain.entities.QrCodeStatusEnum;
import com.mstra.tickets.domain.entities.Ticket;
import com.mstra.tickets.exception.QrCodeGenerationException;
import com.mstra.tickets.repositories.QrCodeRepository;
import com.mstra.tickets.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private static final int QR_HEIGHT = 300;
    private static final int QR_WIDTH = 300;


    private final QRCodeWriter qrCodeWriter;
    private final QrCodeRepository qrCodeRepository;

    @Override
    public QrCode generateQrCode(Ticket ticket) {
        try{
            UUID uuid = UUID.randomUUID();
            String qrcodeImage = generateQrCodeImage(uuid);
            QrCode qrCode = new QrCode();
            qrCode.setId(uuid);
            qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
            qrCode.setValue(qrcodeImage);
            qrCode.setTicket(ticket);

            qrCodeRepository.saveAndFlush(qrCode);
        } catch (WriterException | IOException ex) {
            throw new QrCodeGenerationException("Failed to generate QR Code", ex);
        }

        return null;
    }

    private String generateQrCodeImage(UUID uuid) throws WriterException, IOException {
        // Creating bit matrix
        BitMatrix bitMatrix = qrCodeWriter.encode(
                uuid.toString(),
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );
        // Turning bit matrix into a buffered image
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrCodeImage, "PNG", outputStream);
            byte[] imageByte = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageByte);
        }
    }
}
