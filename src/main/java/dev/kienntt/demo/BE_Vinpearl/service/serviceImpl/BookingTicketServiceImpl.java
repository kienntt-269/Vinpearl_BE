package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.*;
import dev.kienntt.demo.BE_Vinpearl.repository.BookingTicketRepository;
import dev.kienntt.demo.BE_Vinpearl.repository.SiteRepository;
import dev.kienntt.demo.BE_Vinpearl.service.BookingTicketService;
import dev.kienntt.demo.BE_Vinpearl.service.FlightService;
import dev.kienntt.demo.BE_Vinpearl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.server.HandshakeFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingTicketServiceImpl implements BookingTicketService {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FlightService flightService;
    @Autowired
    private BookingTicketRepository bookingTicketRepository;

    @Override
    public Iterable findAll() {
        return bookingTicketRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return bookingTicketRepository.findById(id);
    }

    @Override
    public BookingTicket save(BookingTicket bookingTicket) {
        return bookingTicketRepository.save(bookingTicket);
    }

    @Override
    public BookingTicket createBooking(Long flightId, Long ticketTypeId, List<Passenger> passengers) {
        // check if the flight and ticket type exist
        Optional<Flight> flight = flightService.findById(flightId);
        Optional<TicketType> ticketType = ticketService.findById(ticketTypeId);
        if (flight.get() == null || ticketType == null) {
            throw new HandshakeFailureException("Flight or ticket type not found");
        }

        // check if the number of passengers does not exceed the available seats
        int numOfPassengers = passengers.size();
        int numOfAvailableSeats = flight.get().getPlane().getTotalSeat();
        if (numOfPassengers > numOfAvailableSeats) {
            throw new HandshakeFailureException("Number of passengers exceeds available seats");
        }

        // calculate the total price of the tickets
        double totalPrice = ticketType.get().getPrice() * numOfPassengers;

        // create ticket objects for each passenger and save to database
//        List<Ticket> tickets = new ArrayList<>();
//        for (Passenger passenger : passengers) {
//            Ticket ticket = new Ticket();
////            ticket.setFlight(flight);
//            ticket.setTicketTypeId(ticketTypeId);
//            ticket.set(passenger);
//            ticket.setPrice(ticketType.getPrice());
//            ticket.setStatus(TicketStatus.PENDING);
//            ticketService.saveTicket(ticket);
//            tickets.add(ticket);
//        }
//
//        // create a booking object and save to database
//        BookingTicket booking = new BookingTicket();
//        booking.setBuyerId(getCurrentUser()); // get the current user from session or token
//        booking.setTickets(tickets);
//        booking.setBookingTime(new Date());
//        booking.setTotalPrice(totalPrice);
//        bookingService.saveBooking(booking);
//        bookingTicketRepository.save(bookingTicket);

        return null;
    }

    @Override
    public void remove(Long id) {
        bookingTicketRepository.deleteById(id);
    }

    @Override
    public Long findAllByMonth(Long startMonth, Long endMonth) {
        return bookingTicketRepository.findAllByMonth(startMonth, endMonth);
    }
}
