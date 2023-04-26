package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.Flight;

import java.util.Optional;

public interface FlightService {
    Iterable findAll();

    Optional findById(Long id);

    Flight save(Flight flight);

    void remove(Long id);
}
