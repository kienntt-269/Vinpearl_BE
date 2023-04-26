package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.config.VnPayConfig;
import dev.kienntt.demo.BE_Vinpearl.config.VnPayUtils;
import dev.kienntt.demo.BE_Vinpearl.domain.dto.RoomTypeMinDto;
import dev.kienntt.demo.BE_Vinpearl.domain.request.BookingRoomRequest;
import dev.kienntt.demo.BE_Vinpearl.domain.request.SearchExportRequest;
import dev.kienntt.demo.BE_Vinpearl.model.*;
import dev.kienntt.demo.BE_Vinpearl.repository.*;
import dev.kienntt.demo.BE_Vinpearl.service.BookingTourService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingTourServiceImpl implements BookingTourService {
    private static final String PAYMENT_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String QUERY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Autowired
    private VnPayConfig vnPayConfig;

    @Value("${vnpay.tmncode}")
    private String tmnCode;

    @Value("${vnpay.hashsecret}")
    private String hashSecret;
    @Autowired
    private BookingTourRepository bookingTourRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TourRepository tourRepository;

    @Override
    public Iterable findAll() {
        return bookingTourRepository.findAll();
    }

    @Override
    public List<BookingTour> getAllBookingTours() {
        return bookingTourRepository.findAllBookingTour();
    }

    @Override
    public Optional<BookingTour> findById(Long id) {
        return bookingTourRepository.findById(id);
    }

    @Override
    public List<BookingTour> findByCustomerId(Long id) {
        return bookingTourRepository.findByCustomerId(id);
    }

    @Override
    public BookingTour save(BookingTour bookingTour) {
        return bookingTourRepository.save(bookingTour);
    }

    @Override
    public void remove(Long id) {
        bookingTourRepository.deleteById(id);
    }

    public List<Customer> getTop5Customer() {
        LocalDateTime startDate = LocalDateTime.now().withDayOfYear(1); // ngày đầu tiên của năm hiện tại
        LocalDateTime endDate = LocalDateTime.now().withDayOfYear(1).plusYears(1); // ngày cuối cùng của năm hiện tại

        List<BookingTour> bookingTours = bookingTourRepository.findByBookingDateBetween(startDate, endDate);
        Map<Long, Integer> bookingCountMap = new HashMap<>();
        for (BookingTour bookingTour : bookingTours) {
            Long customerId = bookingTour.getCustomerId();
            if (bookingCountMap.containsKey(customerId)) {
                int bookingCount = bookingCountMap.get(customerId);
                bookingCountMap.put(customerId, bookingCount + 1);
            } else {
                bookingCountMap.put(customerId, 1);
            }
        }
        List<Map.Entry<Long, Integer>> sortedBookingCounts = new ArrayList<>(bookingCountMap.entrySet());
        Collections.sort(sortedBookingCounts, (a, b) -> b.getValue().compareTo(a.getValue()));

        List<Customer> top5Customers = new ArrayList<>();
        for (int i = 0; i < 5 && i < sortedBookingCounts.size(); i++) {
            Long customerId = sortedBookingCounts.get(i).getKey();
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null) {
                top5Customers.add(customer);
            }
        }
        return top5Customers;
    }

    public List<Tour> getTopTours(int limit) {
        // truy vấn dữ liệu từ bookingTourRepository
        List<BookingTour> bookingTours = (List<BookingTour>) bookingTourRepository.findAll();

        // tạo một Map để đếm số lần đặt tour cho mỗi tour
        Map<Long, Integer> tourCountMap = new HashMap<>();
        for (BookingTour bookingTour : bookingTours) {
            Long tourId = bookingTour.getTourId();
            if (tourCountMap.containsKey(tourId)) {
                tourCountMap.put(tourId, tourCountMap.get(tourId) + 1);
            } else {
                tourCountMap.put(tourId, 1);
            }
        }

        // sắp xếp Map theo giá trị giảm dần
        List<Map.Entry<Long, Integer>> sortedList = new ArrayList<>(tourCountMap.entrySet());
        sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // trả về danh sách các tour được đặt nhiều nhất
        List<Tour> topTours = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : sortedList) {
            Long tourId = entry.getKey();
//            Tour tour = bookingTours.get(0).getTourId(); // lấy thông tin tour từ bất kỳ bookingTour có chứa tourId
//            topTours.add(tour);
//            if (topTours.size() == limit) {
//                break;
//            }
        }

        return topTours;
    }

    public Map<String, Long> getBookingTourCountByMonth() {
        List<BookingTour> bookingTours = bookingTourRepository.findAllBookingTour();
        Map<String, Long> bookingTourCountByMonth = bookingTours.stream()
                .collect(Collectors.groupingBy(
                        bookingTour -> String.format("Tháng %01d", bookingTour.getPaymentDate().getMonthValue()),
                        Collectors.counting()
                ));
        return bookingTourCountByMonth;
    }

    @Override
    public BookingTour findByPaymentCode(String paymentCode) {
        return bookingTourRepository.findByPaymentCode(paymentCode);
    }

    @Override
    public Page<BookingTour> searchBookingTour(Long customerId, String code, Long stauts, LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        PageRequest page_req = new PageRequest(0, buildingId, Sort.Direction.DESC, "idNode");
//        LocalDateTime startDate = startTime != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime),
//                TimeZone.getDefault().toZoneId()) : null;
//        LocalDateTime endDate = startTime != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime),
//                TimeZone.getDefault().toZoneId()) : null;
        LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return bookingTourRepository.searchBookingTour(customerId, code, stauts, startTime, endTime, pageable);
    }

    @Override
    public BookingTour saveBookingTour(BookingTour bookingTour) throws UnsupportedEncodingException {
        RoomTypeMinDto roomType = roomTypeRepository.findRoomTypeByMinPrice(bookingTour.getHotelId());
        Long roomTypeId = roomType.getId();
        Optional<Tour> tour = tourRepository.findById(bookingTour.getTourId());
        Long customerId = bookingTour.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        List<Room> availableRooms = roomRepository.findRoomTourEmpty(roomTypeId, 0L, tour.get().getStartDate(), tour.get().getEndDate());
//        List<Room> availableRooms = roomRepository.findByRoomGroupType(roomTypeId, 0L);
        if (availableRooms.isEmpty()) {
            throw new RuntimeException("Phòng trong khách sạn đã hết, vui lòng chọn khách sạn khác");
        }

        Room roomRandom =  getRandomAvailableRoom(availableRooms);
        LocalDateTime paymentDate = LocalDateTime.now();

        String paymentCode = "Tour" + UUID.randomUUID().toString().replace("-", "");
        // Tạo mới đối tượng BookingRoom và lưu vào database
        BookingTour bookingTour1 = new BookingTour();
        bookingTour1.setRoomId(roomRandom.getId());
        bookingTour1.setCustomerId(customerId);
        bookingTour1.setHotelId(bookingTour.getHotelId());
        bookingTour1.setTourId(bookingTour.getTourId());
        bookingTour1.setPaymentAmount(bookingTour.getPaymentAmount());
        bookingTour1.setPaymentDate(paymentDate);
        bookingTour1.setDescription(bookingTour.getDescription());
        bookingTour1.setNumberAdult(bookingTour.getNumberAdult());
        bookingTour1.setNumberChildren(bookingTour.getNumberChildren());
        bookingTour1.setPaymentStatus(0L);
        bookingTour1.setPaymentCode(paymentCode);

        BookingTour bookingTour2 = bookingTourRepository.save(bookingTour1);

        bookingTour2.setCode(String.format("VPT-TKGVB%06d", bookingTour1.getId()));
        bookingTourRepository.save(bookingTour2);

        String paymentUrl = createPaymentUrl(bookingTour2);
        if (paymentUrl.isEmpty()) {
            return null;
        }
        bookingTour2.setUrl(paymentUrl);

        return bookingTour2;
    }

    @Override
    @Transactional
    public BookingTour checkPaymentOk(String code, BookingTour bookingTourDetails) {
        // Code to book hotel
        BookingTour bookingTour = bookingTourRepository.findByPaymentCode(code);

        Long roomId = bookingTourDetails.getRoomId();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room ID cannot be null."));

        Long customerId = bookingTourDetails.getCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer ID cannot be null."));

        bookingTour.setPaymentStatus(1L);
        bookingTourRepository.save(bookingTour);
//        if (bookingRoomDetails.getPaymentStatus() == 2) {
        room.getRoomTypes().setRemainingOfRooms(room.getRoomTypes().getRemainingOfRooms() - 1);
        room.setStatus(1L);
        roomRepository.save(room);

        Optional<RoomType> roomType = roomTypeRepository.findById(room.getRoomTypeId());
        roomTypeRepository.updateRemainingOfRooms(room.getRoomTypeId(), roomType.get().getRemainingOfRooms() - 1);

        Optional<Tour> tour = tourRepository.findById(bookingTour.getTourId());
        tourRepository.updateRemainingOfTour(bookingTour.getTourId(), tour.get().getRemainingOfPeople() - bookingTour.getNumberChildren() - bookingTour.getNumberAdult());

//        }

//        bookingRoom.setService(bookingRoomDetails.getService());
        return bookingTourRepository.save(bookingTour);
    }

    public String createPaymentUrl(BookingTour bookingTour) throws UnsupportedEncodingException {
        String vnp_Returnurl = vnPayConfig.getReturnUrl();
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_HashSecret = vnPayConfig.getHashSecret();
        String vnp_Url = vnPayConfig.getUrl();
        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
//        String vnp_TxnRef = VnPayUtils.getRandomNumber(8);
        String vnp_OrderInfo = bookingTour.getDescription();
        long vnp_Amount = bookingTour.getPaymentAmount() * 100;
//        String vnp_IpAddr = bookingRoomRequest.getIp();
        String vnp_IpAddr = "192.168.100.3";
        String vnp_CurrCode = "VND";
        String vnp_Locale = "vn";
        Date date = Date.from(bookingTour.getPaymentDate().atZone(ZoneId.systemDefault()).toInstant());
        String vnp_TxnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", bookingTour.getPaymentCode());
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

    public Room getRandomAvailableRoom(List<Room> availableRooms) {
        if (availableRooms.isEmpty()) {
            // Nếu không có phòng nào còn trống, trả về null.
            return null;
        }

        int randomIndex = new Random().nextInt(availableRooms.size());
        return availableRooms.get(randomIndex);
    }

    @Override
    public List<BookingTour> searchExport(SearchExportRequest searchExportRequest) {
        LocalDateTime startDateTime = searchExportRequest.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = searchExportRequest.getEndDate().atTime(LocalTime.MAX);
        return bookingTourRepository.searchExport(startDateTime, endDateTime, searchExportRequest.getStatus());
    }

    @Override
    public byte[] exportToExcel(List<BookingTour> bookingTours, LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException {
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
        String[] headers = {"Mã đơn hàng", "Tên khách hàng", "Email", "Số điện thoại", "Tên tour", "Loại tour", "Số người lớn", "Số trẻ em", "Ngày bắt đầu", "Ngày kết thúc", "Phòng", "Loại phòng", "Ngày chuyển khoản", "Số tiền", "Trạng thái"};
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
        for (BookingTour bookingTour : bookingTours) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bookingTour.getCode());
            row.createCell(1).setCellValue(bookingTour.getCustomer().getFullName());
            row.createCell(2).setCellValue(bookingTour.getCustomer().getEmail());
            row.createCell(3).setCellValue(bookingTour.getCustomer().getPhone());

            row.createCell(4).setCellValue(bookingTour.getTour().getName());

            if (bookingTour.getTour().getTypeOfTourId() == 1) {
                row.createCell(5).setCellValue("Gói nghỉ dưỡng");
            } else if (bookingTour.getTour().getTypeOfTourId() == 2) {
                row.createCell(5).setCellValue("VinWonders");
            } else if (bookingTour.getTour().getTypeOfTourId() == 3) {
                row.createCell(5).setCellValue("Vận chuyển");
            } else if (bookingTour.getTour().getTypeOfTourId() == 4) {
                row.createCell(5).setCellValue("Vinpearl Golf");
            } else if (bookingTour.getTour().getTypeOfTourId() == 5) {
                row.createCell(5).setCellValue("Ẩm thực");
            } else if (bookingTour.getTour().getTypeOfTourId() == 6) {
                row.createCell(5).setCellValue("Tour");
            } else if (bookingTour.getTour().getTypeOfTourId() == 7) {
                row.createCell(5).setCellValue("Vé tham quan");
            } else if (bookingTour.getTour().getTypeOfTourId() == 8) {
                row.createCell(5).setCellValue("Spa");
            }else  {
                row.createCell(5).setCellValue("");
            }

            row.createCell(6).setCellValue(bookingTour.getNumberAdult());
            row.createCell(7).setCellValue(bookingTour.getNumberChildren());

            DateTimeFormatter formatterStartDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedTourStartDate = bookingTour.getTour().getStartDate().format(formatterStartDate);

            row.createCell(8).setCellValue(formattedTourStartDate);

            DateTimeFormatter formatterEndDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedTourEndDate = bookingTour.getTour().getEndDate().format(formatterEndDate);

            row.createCell(9).setCellValue(formattedTourEndDate);

            row.createCell(10).setCellValue(bookingTour.getRoom().getNumberRoom());
            row.createCell(11).setCellValue(bookingTour.getRoom().getRoomTypes().getName());

            DateTimeFormatter formatterPaymentDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedPaymentDate = bookingTour.getPaymentDate().format(formatterPaymentDate);

            row.createCell(12).setCellValue(formattedPaymentDate);


            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            String formattedPrice = decimalFormat.format(bookingTour.getPaymentAmount());
            String formattedPriceWithDot = formattedPrice.replace(",", ".");
            row.createCell(13).setCellValue(formattedPriceWithDot + " VNĐ");

            if (bookingTour.getPaymentStatus() == 0) {
                row.createCell(14).setCellValue("Đã hủy");
            } else if (bookingTour.getPaymentStatus() == 1) {
                row.createCell(14).setCellValue("Thành công");
            } else if (bookingTour.getPaymentStatus() == 2) {
                row.createCell(14).setCellValue("Thành công");
            } else {
                row.createCell(14).setCellValue("Đã hủy");
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
