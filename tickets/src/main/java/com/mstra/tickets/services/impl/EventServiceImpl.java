package com.mstra.tickets.services.impl;

import com.mstra.tickets.domain.CreateEventRequest;
import com.mstra.tickets.domain.UpdateEventRequest;
import com.mstra.tickets.domain.UpdateTicketTypeRequest;
import com.mstra.tickets.domain.entities.Event;
import com.mstra.tickets.domain.entities.EventStatusEnum;
import com.mstra.tickets.domain.entities.TicketType;
import com.mstra.tickets.domain.entities.User;
import com.mstra.tickets.exception.EventNotFoundException;
import com.mstra.tickets.exception.EventUpdateException;
import com.mstra.tickets.exception.TicketTypeNotFoundException;
import com.mstra.tickets.exception.UserNotFoundException;
import com.mstra.tickets.repositories.EventRepository;
import com.mstra.tickets.repositories.UserRepository;
import com.mstra.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
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
        event.setStatus(EventStatusEnum.PUBLISHED);
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

    @Override
    @Transactional // Added because of multiple database calls so the database does not get into inconsistent state.
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if (event.getId() == null) throw new EventUpdateException("Event ID cannot be null.");

        if (!id.equals(event.getId())) throw new EventUpdateException("Cannot update the ID of an event.");

        Event existingEvent = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with ID '%s' does not exist.", id)
                ));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSaleStart(event.getSalesStart());
        existingEvent.setSaleEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeId = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Remove all ticket types not in the request.
        existingEvent.getTicketTypes().removeIf(ticketType ->
                !requestTicketTypeId.contains(existingEvent.getId())
        );

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes()
                .stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if (null == ticketType.getId()) {
                // Create new
                TicketType newTicket = new TicketType();
                newTicket.setName(ticketType.getName());
                newTicket.setPrice(ticketType.getPrice());
                newTicket.setDescription(ticketType.getDescription());
                newTicket.setTotalAvailable(ticketType.getTotalAvailable());
                newTicket.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(newTicket);
            } else if (existingTicketTypesIndex.containsKey(ticketType.getId())) {
                // Updating existing ticket
                TicketType existingTicket = existingTicketTypesIndex.get(ticketType.getId());
                existingTicket.setName(ticketType.getName());
                existingTicket.setPrice(ticketType.getPrice());
                existingTicket.setDescription(ticketType.getDescription());
                existingTicket.setTotalAvailable(ticketType.getTotalAvailable());
            } else {
                throw new TicketTypeNotFoundException(String.format(
                        "Ticket type with ID '%s' does not exist.", ticketType.getId()
                ));
            }
        }
        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }
}
