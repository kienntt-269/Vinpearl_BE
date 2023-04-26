package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.Passenger;

import java.util.Optional;

public interface PassengerService {
    Iterable findAll();

    Optional findById(Long id);

    Passenger save(Passenger passenger);

    void remove(Long id);
}
