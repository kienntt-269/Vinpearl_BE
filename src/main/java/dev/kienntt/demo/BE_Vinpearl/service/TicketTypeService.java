package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.TicketType;

import java.util.Optional;

public interface TicketTypeService {
    Iterable findAll();

    Optional findById(Long id);

    TicketType save(TicketType ticketType);

    void remove(Long id);
}
