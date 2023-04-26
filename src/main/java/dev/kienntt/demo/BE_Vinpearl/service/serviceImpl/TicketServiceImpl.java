package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.Ticket;
import dev.kienntt.demo.BE_Vinpearl.repository.SiteRepository;
import dev.kienntt.demo.BE_Vinpearl.repository.TicketRepository;
import dev.kienntt.demo.BE_Vinpearl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Iterable findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void remove(Long id) {
        ticketRepository.deleteById(id);
    }
}
