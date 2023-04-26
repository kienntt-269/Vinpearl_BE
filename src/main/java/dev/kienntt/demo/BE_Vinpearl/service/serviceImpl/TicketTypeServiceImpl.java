package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.TicketType;
import dev.kienntt.demo.BE_Vinpearl.repository.TicketTypeRepository;
import dev.kienntt.demo.BE_Vinpearl.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {
    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Override
    public Iterable findAll() {
        return ticketTypeRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return ticketTypeRepository.findById(id);
    }

    @Override
    public TicketType save(TicketType ticketType) {
        return ticketTypeRepository.save(ticketType);
    }

    @Override
    public void remove(Long id) {
        ticketTypeRepository.deleteById(id);
    }
}
