package com.mstra.tickets.mappers;

import com.mstra.tickets.domain.CreateEventRequest;
import com.mstra.tickets.domain.CreateTicketTypeRequest;
import com.mstra.tickets.domain.dtos.CreateEventRequestDto;
import com.mstra.tickets.domain.dtos.CreateEventResponseDto;
import com.mstra.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    CreateTicketTypeRequest fromDto(CreateTicketTypeRequest dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);
}
