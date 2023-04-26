package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRoomRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.SearchExportRequest;
import dev.kienntt.demo.BE_Vinpearl.model.*;
import dev.kienntt.demo.BE_Vinpearl.repository.BookingTourRepository;
import dev.kienntt.demo.BE_Vinpearl.repository.CustomerRepository;
import dev.kienntt.demo.BE_Vinpearl.repository.RoomRepository;
import dev.kienntt.demo.BE_Vinpearl.repository.RoomTypeRepository;
import dev.kienntt.demo.BE_Vinpearl.service.BookingTourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/booking-tour")
public class BookingTourController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BookingTourService bookingTourService;

    @PostMapping("/create")
    public ResponseMessage create(@RequestBody BookingTour bookingTour) {
        bookingTourService.save(bookingTour);
        return new ResponseMessage(200, "Đặt tour thành công", "", null);
    }

    @GetMapping("/findByPaymentCode/{id}")
    public ResponseMessage findByPaymentCode(@PathVariable String id) {
        BookingTour bookingTour = bookingTourService.findByPaymentCode(id);
        return new ResponseMessage(200, "Success", bookingTour, null);
    }

    @PutMapping("/check-payment-tour-ok/{code}")
    public ResponseMessage checkPaymentTourOk(@PathVariable("code") String code, @RequestBody BookingTour bookingTour) {
        bookingTourService.checkPaymentOk(code, bookingTour);
        return new ResponseMessage(200, "OK", "", null);
    }

    @PostMapping("/booking")
    public ResponseMessage createNewBookingtour(@RequestBody BookingTour bookingTour) throws UnsupportedEncodingException {
        BookingTour bookingTour1 = bookingTourService.saveBookingTour(bookingTour);
        return  new ResponseMessage(200, "OK", bookingTour1, null);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<BookingTour>> getBookingByCustomer(@PathVariable Long id) {
        List<BookingTour> list = bookingTourService.findByCustomerId(id);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/detail/{id}")
    public ResponseMessage detailBookingRoom(@PathVariable Long id) {
        Optional<BookingTour> bookingTourOptional = bookingTourService.findById(id);
        return bookingTourOptional.map(bookingTour -> new ResponseMessage(200, "Success", bookingTour, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @GetMapping("/top-tours")
    public ResponseEntity<List<Tour>> getTopTours(@RequestParam int limit) {
        List<Tour> topTours = bookingTourService.getTopTours(limit);
        return ResponseEntity.ok().body(topTours);
    }

    @GetMapping("/top-customers")
    public ResponseMessage getTopCustomers() {
        List<Customer> topCustomers = bookingTourService.getTop5Customer();
        return new ResponseMessage(200, "Success", topCustomers, null);
    }

    @GetMapping("/")
    public ResponseEntity<List<Customer>> getBooking() {
        List<Customer> topCustomers = bookingTourService.getTop5Customer();
        return ResponseEntity.ok().body(topCustomers);
    }

    @GetMapping("/search")
    public ResponseMessage searchBookingTour(@RequestParam(required = false) Long customerId,
                                           @RequestParam(required = false) String code,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                             @RequestParam(required = false) Long status,
                                           Pageable pageable) {
        Page<BookingTour> list = bookingTourService.searchBookingTour(customerId, code, status, startDate, endDate, pageable);
        return new ResponseMessage(200, "Success", list, null);
    }

    public String getRemoteIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
//            ipAddress = request.getLocalAddr();
        }
        return ipAddress;
    }

    @PostMapping("/export")
    public void exportToExcel(@RequestBody SearchExportRequest searchExportRequest, HttpServletResponse response) throws IOException {
        List<BookingTour> bookingTours = bookingTourService.searchExport(searchExportRequest);
//        bookingRoomService.exportToExcel(bookingRooms, response);
        byte[] excelBytes = bookingTourService.exportToExcel(bookingTours, searchExportRequest.getStartDate(), searchExportRequest.getEndDate(), response);
        // Đặt header "Content-Disposition" để tệp trả về được đặt tên và thông báo là một tệp Excel
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=booking-rooms.xls");

        // Ghi dữ liệu tệp Excel vào response
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(excelBytes);
        outputStream.flush();
        outputStream.close();
    }
}
