package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.Airport;

import java.util.Optional;

public interface AirportService {
    Iterable findAll();

    Optional findById(Long id);

    Airport save(Airport airport);

    void remove(Long id);
}
