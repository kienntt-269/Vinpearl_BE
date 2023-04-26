package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.BookingTicket;
import dev.kienntt.demo.BE_Vinpearl.model.Passenger;

import java.util.List;
import java.util.Optional;

public interface BookingTicketService {
    Iterable findAll();

    Optional findById(Long id);

    BookingTicket save(BookingTicket bookingTicket);

    BookingTicket createBooking(Long flightId, Long ticketTypeId, List<Passenger> passengers);

    void remove(Long id);

    Long findAllByMonth(Long startMonth, Long endMonth);
}
