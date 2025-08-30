package com.mstra.tickets.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListTicketTypeResponseDto {
    private UUID uuid;
    private String name;
    private Double price;
}
