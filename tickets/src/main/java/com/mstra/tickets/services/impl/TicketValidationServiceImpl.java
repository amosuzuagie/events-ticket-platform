package com.mstra.tickets.services.impl;

import com.mstra.tickets.domain.entities.*;
import com.mstra.tickets.exception.QrCodeNotFoundException;
import com.mstra.tickets.exception.TicketNotFoundException;
import com.mstra.tickets.repositories.QrCodeRepository;
import com.mstra.tickets.repositories.TicketRepository;
import com.mstra.tickets.repositories.TicketValidationRepository;
import com.mstra.tickets.services.TicketValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {
    private final QrCodeRepository qrCodeRepository;
    private final TicketRepository ticketRepository;
    private final TicketValidationRepository validationRepository;


    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(String.format(
                        "QR Code with ID '%s' was not found.", qrCodeId
                )));

        Ticket ticket = qrCode.getTicket();

        return validateTicket(ticket);
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket);
    }

    private TicketValidation validateTicket(Ticket ticket) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(TicketValidationMethod.QR_SCAN);

        //Ensuring ticket is validate once
        TicketValidationStatusEnum validationStatusEnum = ticket.getValidations().stream()
                .filter(v -> TicketValidationStatusEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v-> TicketValidationStatusEnum.INVALID)
                .orElse(TicketValidationStatusEnum.VALID);

        ticketValidation.setStatus(validationStatusEnum);

        return validationRepository.save(ticketValidation);
    }
}
