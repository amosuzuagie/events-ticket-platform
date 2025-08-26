package com.mstra.tickets.services;

import com.mstra.tickets.domain.CreateEventRequest;
import com.mstra.tickets.domain.entities.Event;

import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest request);
}
