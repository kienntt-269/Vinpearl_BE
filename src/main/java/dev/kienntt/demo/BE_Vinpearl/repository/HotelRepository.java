package dev.kienntt.demo.BE_Vinpearl.repository;

import dev.kienntt.demo.BE_Vinpearl.domain.dto.HotelDto;
import dev.kienntt.demo.BE_Vinpearl.model.Hotel;
import dev.kienntt.demo.BE_Vinpearl.model.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends PagingAndSortingRepository<Hotel, Long> {

    @Query("select new dev.kienntt.demo.BE_Vinpearl.domain.dto.HotelDto(" +
            "h.id, h.name, h.address, min(s.price), min(ih.path)) " +
            "from Hotel h " +
            "left join RoomType rt on h.id = rt.hotelId " +
            "left join Service s on rt.id = s.roomTypeId " +
            "left join ImageHotel ih on h.id = ih.hotelId " +
            "where (:siteId is null or h.siteId = :siteId) " +
            "and (:name is null or h.name LIKE concat('%',:name,'%')) " +
            "and (:phone is null or h.phone LIKE concat('%',:phone,'%')) " +
            "and (:totalRoom is null or h.totalRoom = :totalRoom) " +
            "group by h.id, h.name, h.address")
    Page<Hotel> getListHotel(Long siteId, String name, Long totalRoom, String phone, Pageable pageable);

    @Query("select h from Hotel h WHERE " +
            "(:siteId is null or h.siteId = :siteId) and " +
            "(:name is null or h.name LIKE CONCAT('%',:name, '%')) and " +
            "(:phone is null or h.phone LIKE CONCAT('%',:phone, '%')) and " +
            "(:totalRoom is null or h.totalRoom = :totalRoom)")
    Page<Hotel> searchHotel(Long siteId, String name, Long totalRoom, String phone, Pageable pageable);

    List<Hotel> findBySiteId(Long siteId);

    @Query("select distinct new dev.kienntt.demo.BE_Vinpearl.domain.dto.HotelDto(h.id, h.name) from Hotel h left join BookingRoom br on h.id = br.hotelId WHERE br.customerId = :customerId and br.paymentStatus = 1 and h.siteId = :siteId")
    List<HotelDto> getListHotelByCustomer(Long customerId, Long siteId);
}
