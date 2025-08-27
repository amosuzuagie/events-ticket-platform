package com.mstra.tickets.services.impl;

import com.mstra.tickets.domain.CreateEventRequest;
import com.mstra.tickets.domain.entities.Event;
import com.mstra.tickets.domain.entities.TicketType;
import com.mstra.tickets.domain.entities.User;
import com.mstra.tickets.exception.UserNotFoundException;
import com.mstra.tickets.repositories.EventRepository;
import com.mstra.tickets.repositories.UserRepository;
import com.mstra.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest request) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found.", organizerId)));

        Event event = new Event();

        List<TicketType> ticketTypes = request.getTicketTypes().stream().map(
                ticketType ->{
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketType.getName());
                    ticketTypeToCreate.setPrice(ticketType.getPrice());
                    ticketTypeToCreate.setDescription(ticketType.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ticketTypeToCreate.setEvent(event);
                    return ticketTypeToCreate;
                }
        ).toList();


        event.setName(request.getName());
        event.setStart(request.getStart());
        event.setEnd(request.getEnd());
        event.setVenue(request.getVenue());
        event.setSaleStart(request.getStart());
        event.setSaleEnd(request.getEnd());
        event.setOrganizer(organizer);
        event.setStatus(request.getStatus());
        event.setTicketTypes(ticketTypes);

        return eventRepository.save(event);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }
}
