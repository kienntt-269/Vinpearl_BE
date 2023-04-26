package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.Flight;
import dev.kienntt.demo.BE_Vinpearl.repository.FlightRepository;
import dev.kienntt.demo.BE_Vinpearl.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {
    @Autowired
    private FlightRepository flightRepository;

    @Override
    public Iterable findAll() {
        return flightRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return flightRepository.findById(id);
    }

    @Override
    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    @Override
    public void remove(Long id) {
        flightRepository.deleteById(id);
    }
}
