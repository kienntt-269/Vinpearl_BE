package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.CustomerFlight;
import dev.kienntt.demo.BE_Vinpearl.repository.CustomerFlightRepository;
import dev.kienntt.demo.BE_Vinpearl.service.CustomerFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerFlightServiceImpl implements CustomerFlightService {
    @Autowired
    private CustomerFlightRepository customerFlightRepository;

    @Override
    public Iterable findAll() {
        return customerFlightRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return customerFlightRepository.findById(id);
    }

    @Override
    public CustomerFlight save(CustomerFlight customerFlight) {
        return customerFlightRepository.save(customerFlight);
    }

    @Override
    public void remove(Long id) {
        customerFlightRepository.deleteById(id);
    }
}
