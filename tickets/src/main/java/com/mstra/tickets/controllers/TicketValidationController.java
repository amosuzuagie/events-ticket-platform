package com.mstra.tickets.controllers;

import com.mstra.tickets.domain.dtos.TicketValidationRequestDto;
import com.mstra.tickets.domain.dtos.TicketValidationResponseDto;
import com.mstra.tickets.domain.entities.TicketValidation;
import com.mstra.tickets.domain.entities.TicketValidationMethod;
import com.mstra.tickets.mappers.TicketValidationMapper;
import com.mstra.tickets.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket-validation")
public class TicketValidationController {
    private final TicketValidationMapper ticketValidationMapper;
    private final TicketValidationService ticketValidationService;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto validationRequest
    ) {
        TicketValidationMethod method = validationRequest.getMethod();
        TicketValidation ticketValidation;
        if (TicketValidationMethod.MANUAL.equals(method)) {
            ticketValidation = ticketValidationService.validateTicketManually(validationRequest.getId());
        } else {
            ticketValidation = ticketValidationService.validateTicketByQrCode(validationRequest.getId());
        }

        return ResponseEntity.ok(ticketValidationMapper.toTicketValidationResponseDto(ticketValidation));
    }
}
