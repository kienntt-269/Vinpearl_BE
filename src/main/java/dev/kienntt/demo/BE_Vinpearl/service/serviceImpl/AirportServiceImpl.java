package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.Airport;
import dev.kienntt.demo.BE_Vinpearl.repository.AirportRepository;
import dev.kienntt.demo.BE_Vinpearl.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirportServiceImpl implements AirportService {
    @Autowired
    private AirportRepository airportRepository;

    @Override
    public Iterable findAll() {
        return null;
    }

    @Override
    public Optional findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Airport save(Airport airport) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}
