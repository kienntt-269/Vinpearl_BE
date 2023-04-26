package dev.kienntt.demo.BE_Vinpearl.repository;

import dev.kienntt.demo.BE_Vinpearl.model.BookingTicket;
import dev.kienntt.demo.BE_Vinpearl.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingTicketRepository extends JpaRepository<BookingTicket, Long> {
    @Query("SELECT COUNT(b.id) FROM BookingTicket b WHERE b.paymentDate >= :startMonth and b.paymentDate <= :endMonth")
    Long findAllByMonth(Long startMonth, Long endMonth);

//    List<BookingTicket> findByFlightId(Long flightId);
//
//    List<BookingTicket> findByBuyerId(Long buyerId);
}
