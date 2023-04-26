package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.model.BookingTicket;
import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.model.Passenger;
import dev.kienntt.demo.BE_Vinpearl.service.BookingTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/booking-ticket")
public class BookingTicketController {
    @Autowired
    private BookingTicketService bookingTicketService;

    @PostMapping("/create")
    public ResponseMessage create(@RequestBody BookingTicket bookingTicket) {
        bookingTicketService.save(bookingTicket);
        return new ResponseMessage(200, "Đặt vé thành công", "", null);
    }

//    @DeleteMapping("/ticket/{ticketId}")
//    public ResponseEntity<?> cancelTicket(@PathVariable("ticketId") String ticketId) {
//        // check if the ticket exists
//        Ticket ticket = ticketService.getTicketById(ticketId);
//        if (ticket == null) {
//            throw new ResourceNotFoundException("Ticket not found");
//        }
//
//        // check if the ticket is cancelable
//        if (!ticket.getStatus().equals(TicketStatus.PENDING)) {
//            throw new BadRequestException("Ticket is not cancelable");
//        }
//
//        // update ticket status to canceled
//        ticket.setStatus(TicketStatus.CANCELED);
//        ticketService.saveTicket(ticket);
//
//        // return success message
//        return ResponseEntity.ok(new ApiResponse(true, "Ticket canceled successfully"));
//    }

    @PostMapping("/flight/{flightId}/ticket-type/{ticketTypeId}")
    public ResponseMessage bookFlightTicket(@PathVariable("flightId") long flightId,
                                            @PathVariable("ticketTypeId") long ticketTypeId,
                                            @RequestBody List<Passenger> passengers) {

        bookingTicketService.createBooking(flightId, ticketTypeId, passengers);
        return new ResponseMessage(200, "Đặt vé thành công", "", null);
    }

    @GetMapping("/findAll")
    public ResponseMessage findAll() {
        return new ResponseMessage(200, "Success", bookingTicketService.findAll(), null);
    }

    @GetMapping("/detail")
    public ResponseMessage getSite(@PathVariable Long id) {
        Optional<BookingTicket> siteOptional = bookingTicketService.findById(id);
        return siteOptional.map(site -> new ResponseMessage(200, "Success", site, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @PutMapping("/updateSite")
    public ResponseMessage updateSite(@PathVariable Long id, @RequestBody BookingTicket bookingTicket) {
        Optional<BookingTicket> bookingTicketOptional = bookingTicketService.findById(id);
        return bookingTicketOptional.map(site1 -> {
            bookingTicket.setId(site1.getId());
            bookingTicketService.save(bookingTicket);
            return new ResponseMessage(200, "Success", "", null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @GetMapping("/deleteSite")
    public ResponseMessage deleteSite(@PathVariable Long id) {
        Optional<BookingTicket> bookingTicketOptional = bookingTicketService.findById(id);
        return bookingTicketOptional.map(site -> {
            bookingTicketService.remove(id);
            return new ResponseMessage(200, "Success", null, null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }
}
