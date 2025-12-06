package dev.kienntt.demo.BE_Vinpearl.repository;

import dev.kienntt.demo.BE_Vinpearl.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TourRepository extends PagingAndSortingRepository<Tour, Long> {
//    @Query("SELECT t FROM Tour t WHERE " +
//            "(:startTime is null or t.startTime >= :startTime) and (:endTime is null or t.endTime <= :endTime)")
    @Query("select new dev.kienntt.demo.BE_Vinpearl.domain.dto.TourDto(\n" +
            "    t.id,\n" +
            "    t.code,\n" +
            "    t.name,\n" +
            "    t.leavingTo.name,\n" +
            "    min(th.priceAdult),\n" +
            "    t.lengthStayId,\n" +
            "    MIN(it.path),\n" +
            "    t.numberOfPeople,\n" +
            "    t.remainingOfPeople,\n" +
            "    t.typeOfTourId,\n" +
            "    t.expirationDate,\n" +
            "    t.leavingFrom.name,\n" +
            "    t.suitableId\n" +
            ")\n" +
            "from Tour t\n" +
            "left join TourHotel th on t.id = th.tourId\n" +
            "left join ImageTour it on t.id = it.tourId\n" +
            "where\n" +
            "    (:siteId is null or t.leavingToId = :siteId)\n" +
            "    and (:searchName is null or t.name LIKE CONCAT('%', :searchName, '%'))\n" +
            "    and (:status is null or t.status = :status)\n" +
            "    and (COALESCE(:lengthStayIds, null) is null or t.lengthStayId in :lengthStayIds)\n" +
            "    and (COALESCE(:suitableIds, null) is null or t.suitableId in :suitableIds)\n" +
            "    and (COALESCE(:typeOfTourIds, null) is null or t.typeOfTourId in :typeOfTourIds)\n" +
            "group by\n" +
            "    t.id, t.code, t.name, t.leavingTo.name,\n" +
            "    t.lengthStayId, t.numberOfPeople, t.remainingOfPeople,\n" +
            "    t.typeOfTourId, t.expirationDate, t.leavingFrom.name, t.suitableId\n" +
            "order by t.id desc")
    Page<Tour> searchTourPage(Long siteId, String searchName, Long status, List<Long> lengthStayIds, List<Long> suitableIds, List<Long> typeOfTourIds, Pageable pageable);

    @Query("select new dev.kienntt.demo.BE_Vinpearl.domain.dto.TourDto(t.id, t.code, t.name, t.leavingTo.name, min(th.priceAdult), t.lengthStayId, it.path, t.numberOfPeople, t.remainingOfPeople, t.typeOfTourId, t.expirationDate, t.leavingFrom.name, t.suitableId) from Tour t " +
            "left join TourHotel th on t.id = th.tourId " +
            "left join ImageTour it on t.id = it.tourId " +
            "GROUP BY t.id, t.name")
    List<Tour> getAllTour();


    @Modifying
    @Query("UPDATE Tour t SET t.remainingOfPeople = :remainingOfPeople WHERE t.id = :id")
    void updateRemainingOfTour(Long id, Long remainingOfPeople);
}
