package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRoomRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.SearchExportRequest;
import dev.kienntt.demo.BE_Vinpearl.model.BookingRoom;
import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.model.BookingTour;
import dev.kienntt.demo.BE_Vinpearl.repository.BookingRoomRepository;
import dev.kienntt.demo.BE_Vinpearl.service.BookingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/booking-room")
public class BookingRoomController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private BookingRoomService bookingRoomService;
    LocalDateTime localDateTime = LocalDateTime.now();

    private final List<SseEmitter> emitterList = new CopyOnWriteArrayList<>();

    @PostMapping("/check-out-reminder/register")
    public SseEmitter registerCheckOutReminder() {
        SseEmitter emitter = new SseEmitter();
        emitterList.add(emitter);
        emitter.onCompletion(() -> emitterList.remove(emitter));
        return emitter;
    }

    @PostMapping("/booking")
    public ResponseMessage createNewBookingRoom(@RequestBody BookingRoomRequest bookingRoomRequest) throws UnsupportedEncodingException {
        BookingRoom bookingRoom = bookingRoomService.saveBookingRoom(bookingRoomRequest);
        return  new ResponseMessage(200, "OK", bookingRoom, null);
    }

    @PutMapping("/check-payment-room-ok/{code}")
    public ResponseMessage checkPaymentRoomOk(@PathVariable("code") String code, @RequestBody BookingRoom bookingRoom) {
        bookingRoomService.checkPaymentOk(code, bookingRoom);
        return new ResponseMessage(200, "OK", "", null);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<BookingRoom>> getBookingByCustomer(@PathVariable Long id) {
        List<BookingRoom> list = bookingRoomService.findByCustomerId(id);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/detail/{id}")
    public ResponseMessage getBookingByCustomer1(@PathVariable Long id) {
        Optional<BookingRoom> bookingRoom = bookingRoomService.findById(id);
        return new ResponseMessage(200, "OK", bookingRoom, null);
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<?> checkoutRoom(@PathVariable Long id) {
        bookingRoomService.checkOutRoom(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/findByPaymentCode/{id}")
    public ResponseMessage findByPaymentCode(@PathVariable String id) {
        BookingRoom bookingRoom = bookingRoomService.findByPaymentCode(id);
        return new ResponseMessage(200, "Success", bookingRoom, null);
    }

    @GetMapping("/search")
    public ResponseMessage searchRoomsPage(@RequestParam(required = false) String code,
                                            @RequestParam(required = false) Long status,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                           @RequestParam(required = false) Long customerId,
                                           Pageable pageable) {

        Page<BookingRoom> list = bookingRoomService.searchBookingRoomsPage(customerId, code, status, startDate, endDate, pageable);
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
        List<BookingRoom> bookingRooms = bookingRoomService.searchExport(searchExportRequest);
//        bookingRoomService.exportToExcel(bookingRooms, response);
        byte[] excelBytes = bookingRoomService.exportToExcel(bookingRooms, searchExportRequest.getStartDate(), searchExportRequest.getEndDate(), response);
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
