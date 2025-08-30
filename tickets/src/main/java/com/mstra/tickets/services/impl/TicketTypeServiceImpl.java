package com.mstra.tickets.services.impl;

import com.mstra.tickets.domain.entities.Ticket;
import com.mstra.tickets.domain.entities.TicketStatusEnum;
import com.mstra.tickets.domain.entities.TicketType;
import com.mstra.tickets.domain.entities.User;
import com.mstra.tickets.exception.TicketSoldOutException;
import com.mstra.tickets.exception.TicketTypeNotFoundException;
import com.mstra.tickets.exception.UserNotFoundException;
import com.mstra.tickets.repositories.TicketRepository;
import com.mstra.tickets.repositories.TicketTypeRepository;
import com.mstra.tickets.repositories.UserRepository;
import com.mstra.tickets.services.QrCodeService;
import com.mstra.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID '%s' was not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(
                () -> new TicketTypeNotFoundException(
                        String.format("Ticket type with ID '%s' was not found", ticketTypeId)
                )
        );

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketTypeId);
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTickets + 1 > totalAvailable) throw new TicketSoldOutException();

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }

//    @Override
//    public Page<Ticket> findByPurchaserId(UUID purchaserId, Pageable pageable) {
//        return null;
//    }
}
