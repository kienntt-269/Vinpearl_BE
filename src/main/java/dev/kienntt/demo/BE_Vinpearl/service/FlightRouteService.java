package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.FlightRoute;

import java.util.Optional;

public interface FlightRouteService {
    Iterable findAll();

    Optional findById(Long id);

    FlightRoute save(FlightRoute flightRoute);

    void remove(Long id);
}
