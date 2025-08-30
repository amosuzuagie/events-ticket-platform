package com.mstra.tickets.services;

import com.mstra.tickets.domain.entities.QrCode;
import com.mstra.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);
    byte[] GetQrImageForUserAndTicket(UUID userId, UUID ticketId);
}
