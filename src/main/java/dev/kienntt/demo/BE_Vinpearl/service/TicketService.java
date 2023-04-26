package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.Ticket;

import java.util.Optional;

public interface TicketService {
    Iterable findAll();

    Optional findById(Long id);

    Ticket save(Ticket ticket);

    void remove(Long id);
}
