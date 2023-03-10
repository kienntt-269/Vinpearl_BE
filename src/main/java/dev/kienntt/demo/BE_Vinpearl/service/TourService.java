package dev.kienntt.demo.BE_Vinpearl.service;

import dev.kienntt.demo.BE_Vinpearl.model.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface TourService {
    Iterable findAll();

    Optional findById(Long id);

    Tour save(Tour tour, MultipartFile[] images) throws IOException;

    void deleteTour(Long id);

    Page<Tour> searchTourPage(Long siteId, String searchName, Long status, Long lengthStayId, Long suitableId, Long typeOfTour, Pageable pageable);
}
