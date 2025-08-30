package com.mstra.tickets.services;

import com.mstra.tickets.domain.entities.QrCode;
import com.mstra.tickets.domain.entities.Ticket;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);
}
