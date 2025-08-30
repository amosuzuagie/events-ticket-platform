package com.mstra.tickets.mappers;

import com.mstra.tickets.domain.dtos.ListTicketResponseDto;
import com.mstra.tickets.domain.dtos.ListTicketTypeResponseDto;
import com.mstra.tickets.domain.entities.Ticket;
import com.mstra.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {
    ListTicketTypeResponseDto toListTicketTypeResponseDto(TicketType ticketType);
    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);
}
