package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRoomRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.SearchExportRequest;
import dev.kienntt.demo.BE_Vinpearl.model.BookingRoom;
import dev.kienntt.demo.BE_Vinpearl.model.BookingTour;
import dev.kienntt.demo.BE_Vinpearl.model.Customer;
import dev.kienntt.demo.BE_Vinpearl.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingTourService {
    Iterable findAll();

    List<BookingTour> getAllBookingTours();

    Optional findById(Long id);

    List<BookingTour> findByCustomerId(Long id);

    BookingTour save(BookingTour bookingTour);

    BookingTour findByPaymentCode(String paymentCode);

    void remove(Long id);

    List<Tour> getTopTours(int limit);

    List<Customer> getTop5Customer();

    Map<String, Long> getBookingTourCountByMonth();

    Page<BookingTour> searchBookingTour(Long customerId, String code, Long status, LocalDate startTime, LocalDate endTime, Pageable pageable);

    BookingTour saveBookingTour(BookingTour bookingTour) throws UnsupportedEncodingException;

    BookingTour checkPaymentOk(String code, BookingTour bookingTourDetails);

    public byte[] exportToExcel(List<BookingTour> bookingTours, LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException;

    List<BookingTour> searchExport(SearchExportRequest searchExportRequest);
}
