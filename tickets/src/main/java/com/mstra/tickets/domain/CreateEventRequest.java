package com.mstra.tickets.domain;

import com.mstra.tickets.domain.entities.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {

    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private String salesStart;
    private String salesEnd;
    private EventStatusEnum status;
    private List<CreateTicketTypeRequest> ticketTypes = new ArrayList<>();
}
