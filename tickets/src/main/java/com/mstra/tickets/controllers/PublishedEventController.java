package com.mstra.tickets.controllers;

import com.mstra.tickets.domain.dtos.ListPublishedEventResponseDto;
import com.mstra.tickets.domain.entities.Event;
import com.mstra.tickets.mappers.EventMapper;
import com.mstra.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/published-events")
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q, Pageable pageable
    ) {
        Page<Event> events;

        if (null != q && !q.isEmpty()) {
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(events.map(eventMapper::toListPublishedEventResponseDto));
    }
}
