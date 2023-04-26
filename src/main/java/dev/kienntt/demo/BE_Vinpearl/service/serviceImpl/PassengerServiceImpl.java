package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.Passenger;
import dev.kienntt.demo.BE_Vinpearl.repository.PassengerRepository;
import dev.kienntt.demo.BE_Vinpearl.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PassengerServiceImpl implements PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;

    @Override
    public Iterable findAll() {
        return passengerRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return passengerRepository.findById(id);
    }

    @Override
    public Passenger save(Passenger customerFlight) {
        return passengerRepository.save(customerFlight);
    }

    @Override
    public void remove(Long id) {
        passengerRepository.deleteById(id);
    }
}
