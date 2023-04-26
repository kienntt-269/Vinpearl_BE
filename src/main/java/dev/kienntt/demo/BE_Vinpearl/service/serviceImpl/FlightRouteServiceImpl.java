package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.FlightRoute;
import dev.kienntt.demo.BE_Vinpearl.repository.FlightRouteRepository;
import dev.kienntt.demo.BE_Vinpearl.service.FlightRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FlightRouteServiceImpl implements FlightRouteService {
    @Autowired
    private FlightRouteRepository flightRouteRepository;

    @Override
    public Iterable findAll() {
        return flightRouteRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return flightRouteRepository.findById(id);
    }

    @Override
    public FlightRoute save(FlightRoute flightRoute) {
        return flightRouteRepository.save(flightRoute);
    }

    @Override
    public void remove(Long id) {
        flightRouteRepository.deleteById(id);
    }
}
