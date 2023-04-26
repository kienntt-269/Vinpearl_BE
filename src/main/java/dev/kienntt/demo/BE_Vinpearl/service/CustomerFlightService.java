package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.CustomerFlight;

import java.util.Optional;

public interface CustomerFlightService {
    Iterable findAll();

    Optional findById(Long id);

    CustomerFlight save(CustomerFlight customerFlight);

    void remove(Long id);
}
