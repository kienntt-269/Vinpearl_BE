package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.config.VnPayConfig;
import dev.kienntt.demo.BE_Vinpearl.config.VnPayUtils;
import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRoomRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.SearchExportRequest;
import dev.kienntt.demo.BE_Vinpearl.model.*;
import dev.kienntt.demo.BE_Vinpearl.repository.*;
import dev.kienntt.demo.BE_Vinpearl.service.BookingRoomService;
import dev.kienntt.demo.BE_Vinpearl.service.EmailService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingRoomService {

    private static final String PAYMENT_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String QUERY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Autowired
    private VnPayConfig vnPayConfig;

    @Value("${vnpay.tmncode}")
    private String tmnCode;

    @Value("${vnpay.hashsecret}")
    private String hashSecret;

    @Autowired
    private BookingRoomRepository bookingRoomRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

//    @Override
//    public List<BookingRoom> findAll() {
//        return bookingRoomRepository.findAll();
//    }

    LocalDateTime localDateTime = LocalDateTime.now();

    @Override
    public List<BookingRoom> findAll() {
        return (List<BookingRoom>) bookingRoomRepository.findAll();
    }

    @Override
    public Optional<BookingRoom> findById(Long id) {
        return bookingRoomRepository.findById(id);
    }

    @Override
    public List<BookingRoom> findByCustomerId(Long id) {
        return bookingRoomRepository.findByCustomerId(id);
    }

    @Override
    public BookingRoom findByPaymentCode(String paymentCode) {
        return bookingRoomRepository.findByPaymentCode(paymentCode);
    }

    @Override
    public BookingRoom save(BookingRoomRequest bookingRoomRequest) throws UnsupportedEncodingException {
        Long hotelId = bookingRoomRequest.getHotelId();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

        Long roomTypeId = bookingRoomRequest.getRoomTypeId();
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng"));

        Long customerId = bookingRoomRequest.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra số phòng còn lại trong loại phòng
        if (roomType.getNumberOfRooms() <= 0) {
            new RuntimeException("Loại phòng này đã hết phòng");
        }

        LocalDateTime dateCheckIn =
                Instant.ofEpochMilli(bookingRoomRequest.getCheckIn()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime dateCheckOut =
                Instant.ofEpochMilli(bookingRoomRequest.getCheckOut()).atZone(ZoneId.systemDefault()).toLocalDateTime();

//        List<Room> availableRooms = roomRepository.findByRoomTypeId(roomTypeId, 0L);
        List<Room> availableRooms = roomRepository.findRoomEmpty(roomTypeId, dateCheckIn, dateCheckOut);
        if (availableRooms.isEmpty()) {
            new RuntimeException("Không có phòng trống");
        }

        Room roomRandom =  getRandomAvailableRoom(availableRooms);

        // Tạo mới đối tượng BookingRoom và lưu vào database
        BookingRoom bookingRoom1 = new BookingRoom();
        bookingRoom1.setRoomId(roomRandom.getId());
        bookingRoom1.setCustomerId(customerId);
        bookingRoom1.setHotelId(hotelId);
        bookingRoom1.setCheckIn(dateCheckIn);
        bookingRoom1.setCheckOut(dateCheckOut);
        bookingRoom1.setServiceId(bookingRoomRequest.getServiceId());

        BookingRoom bookingRoom = bookingRoomRepository.save(bookingRoom1);

//        // Giảm số phòng còn lại trong loại phòng
//        roomType.setRemainingOfRooms(roomType.getRemainingOfRooms() - 1);
//        roomTypeRepository.save(roomType);
//        roomRandom.setStatus(1);
//        roomRepository.save(roomRandom);

//        // Send confirmation email
//        String to = customer.getEmail();
//        String subject = "Booking Confirmation";
//        String text = "Dear customer, your booking has been confirmed. Thank you for choosing our hotel.";
//        try {
//            emailService.sendEmail(to, subject, text);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Error sending email");
//        }

        return bookingRoomRepository.save(bookingRoom);
    }

    @Override
    public BookingRoom update(Long bookingRoomId, BookingRoom bookingRoomDetails) {
        // Code to book hotel
        BookingRoom bookingRoom = bookingRoomRepository.findById(bookingRoomId)
                .orElseThrow(() -> new RuntimeException("Booking ID cannot be null."));

        Long roomId = bookingRoomDetails.getRoomId();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room ID cannot be null."));

        Long customerId = bookingRoomDetails.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer ID cannot be null."));
        LocalDateTime localDateTime = LocalDateTime.now();

        bookingRoom.setCheckIn(bookingRoomDetails.getCheckIn());
        bookingRoom.setCheckOut(bookingRoomDetails.getCheckOut());
        bookingRoom.setPaymentDate(localDateTime);
        bookingRoom.setPaymentAmount(bookingRoomDetails.getPaymentAmount());
        bookingRoom.setPaymentStatus(bookingRoomDetails.getPaymentStatus());
        bookingRoom.setNumberAdult(bookingRoomDetails.getNumberAdult());
        bookingRoom.setNumberChildren(bookingRoomDetails.getNumberChildren());
        bookingRoom.setDescription(bookingRoomDetails.getDescription());
        bookingRoom.setPerNight(bookingRoomDetails.getPerNight());
        bookingRoom.setRoom(room);
        bookingRoom.setCustomer(customer);
        bookingRoom.setService(bookingRoomDetails.getService());

        return bookingRoomRepository.save(bookingRoom);
    }

    @Override
    public void deleteBookingRoom(Long id) {
        try{
            bookingRoomRepository.deleteById(id);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Page<BookingRoom> searchBookingRoomsPage(Long customerId, String code, Long status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        LocalDateTime startDate = startTime != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime),
//                        TimeZone.getDefault().toZoneId()) : null;
//        LocalDateTime endDate = startTime != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime),
//                TimeZone.getDefault().toZoneId()) : null;
        LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return bookingRoomRepository.searchBookingRoomsPage(customerId, code, status, startTime, endTime, pageable);
    }

    @Override
    public Long findAllByMonth(Long startMonth, Long endMonth) {
        return bookingRoomRepository.findAllByMonth(startMonth, endMonth);
    }

    @Override
    public BookingRoom checkOutRoom(Long id) {
        BookingRoom bookingRoom = bookingRoomRepository.findById(id).orElse(null);
        if (bookingRoom == null) {
            throw new RuntimeException("Không tìm thấy đặt phòng có id này");
        }

        // Kiểm tra phòng đã được đặt hay chưa
        Long roomId = bookingRoom.getRoomId();
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.get().getStatus() == 0) {
            throw new RuntimeException("Phòng đã được đặt");
        }

        // Kiểm tra số lượng khách hợp lệ
        Long numberOfGuests = bookingRoom.getNumberAdult();

        // Tính toán số tiền phải thanh toán
        bookingRoomRepository.save(bookingRoom);
        room.get().setStatus(0L);
        roomRepository.save(room.get());

        System.out.println("Thanh toán thành công. Tổng số tiền phải thanh toán là " + bookingRoom.getPaymentAmount() + " đồng.");

        return null;
    }

//    private Long calculateTotalPrice(BookingRoom bookingRoom) {
//        // Tính tổng giá tiền của đặt phòng dựa trên giá tiền của loại phòng và số ngày đặt phòng
//        Long roomPriceVnd = bookingRoom.getRoom().getRoomTypes().getPriceVnd();
//        Long roomPriceUsd = bookingRoom.getRoom().getRoomTypes().getPriceUsd();
//
//        LocalDateTime localDT = LocalDateTime.of(2017, 02, 11, 8, 45, 50);
//        long localDTInMilli = localDT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        Long numberOfDays = (dateCheckOut - dateCheckIn) / (24 * 60 * 60 * 1000);
//        return roomPrice * numberOfDays;
//    }
//
//    private String generateInvoice(BookingRoom bookingRoom) {
//        // Tạo thông tin hóa đơn thanh toán và trả về dưới dạng chuỗi
//        StringBuilder invoice = new StringBuilder();
//        invoice.append("Room number: ").append(bookingRoom.getRoom().getRoomNumber()).append("\n");
//        invoice.append("Checkin date: ").append(bookingRoom.getCheckinDate()).append("\n");
//        invoice.append("Checkout date: ").append(bookingRoom.getCheckoutDate()).append("\n");
//        invoice.append("Total price: ").append(bookingRoom.getTotalPrice()).append("\n");
//        return invoice.toString();
//    }

    public Map<String, Long> getBookingRoomCountByMonth() {
        List<BookingRoom> bookingRooms = bookingRoomRepository.findAllBookingRoom();
        Map<String, Long> bookingTourCountByMonth = bookingRooms.stream()
                .collect(Collectors.groupingBy(
                        bookingRoom -> String.format("Tháng %01d", bookingRoom.getPaymentDate().getMonthValue()),
                        Collectors.counting()
                ));
        return bookingTourCountByMonth;
    }

    public Room getRandomAvailableRoom(List<Room> availableRooms) {
        if (availableRooms.isEmpty()) {
            // Nếu không có phòng nào còn trống, trả về null.
            return null;
        }

        int randomIndex = new Random().nextInt(availableRooms.size());
        return availableRooms.get(randomIndex);
    }

    public String createPaymentUrl(BookingRoomRequest bookingRoomRequest) throws UnsupportedEncodingException {
        String vnp_Returnurl = vnPayConfig.getReturnUrl();
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_HashSecret = vnPayConfig.getHashSecret();
        String vnp_Url = vnPayConfig.getUrl();
        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
//        String vnp_TxnRef = VnPayUtils.getRandomNumber(8);
        String vnp_OrderInfo = bookingRoomRequest.getDescription();
        long vnp_Amount = bookingRoomRequest.getPaymentAmount() * 100;
//        String vnp_IpAddr = bookingRoomRequest.getIp();
        String vnp_IpAddr = "192.168.100.3";
        String vnp_CurrCode = "VND";
        String vnp_Locale = "vn";
//        String vnp_TxnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Date date = Date.from(bookingRoomRequest.getPaymentDate().atZone(ZoneId.systemDefault()).toInstant());
        String vnp_TxnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", bookingRoomRequest.getPaymentCode());
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_TxnTime);

        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();

        String vnp_SecureHash = VnPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_Url + "?" + queryUrl;

        if (paymentUrl.isEmpty()) {

        }
        return paymentUrl;
    }

    @Override
    public BookingRoom saveBookingRoom(BookingRoomRequest bookingRoomRequest) throws UnsupportedEncodingException {
        Long roomTypeId = bookingRoomRequest.getRoomTypeId();
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("Room Type ID cannot be null."));

        Long customerId = bookingRoomRequest.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer ID cannot be null."));

        // Kiểm tra số phòng còn lại trong loại phòng
//        if (roomType.getRemainingOfRooms() <= 0) {
//            throw  new RuntimeException("No room available");
//        }

        LocalDateTime dateCheckIn =
                Instant.ofEpochMilli(bookingRoomRequest.getCheckIn()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime dateCheckOut =
                Instant.ofEpochMilli(bookingRoomRequest.getCheckOut()).atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<Room> availableRooms = roomRepository.findRoomEmpty(roomTypeId, dateCheckIn, dateCheckOut);

        if (availableRooms.isEmpty()) {
            throw new RuntimeException("Không còn phòng trống");
        }

        Room roomRandom =  getRandomAvailableRoom(availableRooms);

        String paymentCode = "Hotel" + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime paymentDate = LocalDateTime.now();
        // Tạo mới đối tượng BookingRoom và lưu vào database
        BookingRoom bookingRoom1 = new BookingRoom();
        bookingRoom1.setRoomId(roomRandom.getId());
        bookingRoom1.setCustomerId(customerId);
        bookingRoom1.setHotelId(bookingRoomRequest.getHotelId());
        bookingRoom1.setCheckIn(dateCheckIn);
        bookingRoom1.setCheckOut(dateCheckOut);
        bookingRoom1.setDescription(bookingRoomRequest.getDescription());
        bookingRoom1.setPaymentAmount(bookingRoomRequest.getPaymentAmount());
//        bookingRoom1.setRoomId(bookingRoomRequest.getRoomTypeId());
        bookingRoom1.setServiceId(bookingRoomRequest.getServiceId());
        bookingRoom1.setNumberAdult(bookingRoomRequest.getNumberAdult());
        bookingRoom1.setNumberChildren(bookingRoomRequest.getNumberChildren());
        bookingRoom1.setPaymentCode(paymentCode);
        bookingRoom1.setPaymentStatus(0L);

        BookingRoom bookingRoom = bookingRoomRepository.save(bookingRoom1);

        bookingRoom.setCode(String.format("VPT-NHHYU%06d", bookingRoom.getId()));
        bookingRoom.setPaymentDate(paymentDate);
        bookingRoomRepository.save(bookingRoom);

        bookingRoomRequest.setPaymentCode(paymentCode);
        bookingRoomRequest.setPaymentDate(paymentDate);
        String paymentUrl = createPaymentUrl(bookingRoomRequest);
        if (paymentUrl.isEmpty()) {
            return null;
        }
        bookingRoom.setUrl(paymentUrl);

        return bookingRoom;
    }

    @Override
    @Transactional
    public BookingRoom checkPaymentOk(String code, BookingRoom bookingRoomDetails) {
        // Code to book hotel
        BookingRoom bookingRoom = bookingRoomRepository.findByPaymentCode(code);

        Long roomId = bookingRoomDetails.getRoomId();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room ID cannot be null."));

        Long customerId = bookingRoomDetails.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer ID cannot be null."));

        bookingRoom.setPaymentStatus(1L);
        bookingRoomRepository.save(bookingRoom);
//        if (bookingRoomDetails.getPaymentStatus() == 2) {
            room.getRoomTypes().setRemainingOfRooms(room.getRoomTypes().getRemainingOfRooms() - 1);
            room.setStatus(1L);
            roomRepository.save(room);

            Optional<RoomType> roomType = roomTypeRepository.findById(room.getRoomTypeId());
            roomTypeRepository.updateRemainingOfRooms(room.getRoomTypeId(), roomType.get().getRemainingOfRooms() - 1);
//        }

//        bookingRoom.setService(bookingRoomDetails.getService());
        return bookingRoomRepository.save(bookingRoom);
    }

    @Override
    public List<BookingRoom> searchExport(SearchExportRequest searchExportRequest) {
        LocalDateTime startDateTime = searchExportRequest.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = searchExportRequest.getEndDate().atTime(LocalTime.MAX);
        return bookingRoomRepository.searchExport(startDateTime, endDateTime, searchExportRequest.getStatus());
    }

    @Override
    public byte[] exportToExcel(List<BookingRoom> bookingRooms, LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException {
        // Set the headers for the response
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=booking_report_" + LocalDate.now() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Booking Report");

        int firstMovedIndex = 0;
        int lastMovedIndex = 5;
        int moveRowCount = 1;
        sheet.shiftRows(firstMovedIndex, lastMovedIndex, moveRowCount, true, false);

        // Create the header row
        String[] headers = {"Mã đơn hàng", "Tên khách hàng", "Email", "Số điện thoại", "Ngày check-in", "Ngày check-out", "Phòng", "Loại phòng", "Loại dịch vụ", "Ngày chuyển khoản", "Số tiền", "Trạng thái"};
        Row headerRow = sheet.createRow(6);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        // Tạo tiêu đề báo cáo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String startDateStr = formatter.format(startDate);
        String endDateStr = formatter.format(endDate);

        String title = "Báo cáo từ ngày " + startDateStr + " tới ngày " + endDateStr;
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(getTitleStyle(workbook));

        // Merge rows 2 to 6
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 4, 0, headers.length - 1);
        sheet.addMergedRegion(mergedRegion);

        // Tạo đối tượng font và set độ lớn font chữ và kiểu in nghiêng cho startDateStr
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)24);
        font.setItalic(true);
        font.setBold(true);

// Canh giữa tiêu đề báo cáo
        CellStyle titleStyle = getTitleStyle(workbook);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        titleStyle.setFont(font);

// Add the booking data to the sheet
        int rowNum = 8;
        for (BookingRoom bookingRoom : bookingRooms) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bookingRoom.getCode());
            row.createCell(1).setCellValue(bookingRoom.getCustomer().getFullName());
            row.createCell(2).setCellValue(bookingRoom.getCustomer().getEmail());
            row.createCell(3).setCellValue(bookingRoom.getCustomer().getPhone());

            DateTimeFormatter formatterCheckIn = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedCheckIn = bookingRoom.getCheckIn().format(formatterCheckIn);

            row.createCell(4).setCellValue(formattedCheckIn);

            DateTimeFormatter formatterCheckOut = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedCheckOut = bookingRoom.getCheckOut().format(formatterCheckOut);

            row.createCell(5).setCellValue(formattedCheckOut);

            row.createCell(6).setCellValue(bookingRoom.getRoom().getNumberRoom());
            row.createCell(7).setCellValue(bookingRoom.getRoom().getRoomTypes().getName());
            row.createCell(8).setCellValue(bookingRoom.getService().getName());

            DateTimeFormatter formatterPaymentDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedPaymentDate = bookingRoom.getPaymentDate().format(formatterPaymentDate);

            row.createCell(9).setCellValue(formattedPaymentDate);


            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            String formattedPrice = decimalFormat.format(bookingRoom.getPaymentAmount());
            String formattedPriceWithDot = formattedPrice.replace(",", ".");
            row.createCell(10).setCellValue(formattedPriceWithDot + " VNĐ");

            if (bookingRoom.getPaymentStatus() == 0) {
                row.createCell(11).setCellValue("Đã hủy");
            } else if (bookingRoom.getPaymentStatus() == 1) {
                row.createCell(11).setCellValue("Thành công");
            } else if (bookingRoom.getPaymentStatus() == 2) {
                row.createCell(11).setCellValue("Thành công");
            } else {
                row.createCell(11).setCellValue("Đã hủy");
            }

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setShrinkToFit(true);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                cell.setCellStyle(style);
                sheet.autoSizeColumn(i);
            }
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] bytes = baos.toByteArray();
        workbook.close();
        baos.close();

        return bytes;
    }

    private CellStyle getTitleStyle(Workbook workbook) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setWrapText(true); // Cho phép ô văn bản xuống dòng
        return titleStyle;
    }

}
