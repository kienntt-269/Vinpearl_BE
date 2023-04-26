package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.model.Airport;
import dev.kienntt.demo.BE_Vinpearl.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/airport")
public class AirportController {
    @Autowired
    private AirportService airportService;

    LocalDateTime localDateTime = LocalDateTime.now();

    @PostMapping("/create")
    public ResponseMessage create(@RequestBody Airport airport) {
        airport.setCreatedDate(localDateTime.toString());
        airport.setCreatedBy(airport.getCreator());
        airportService.save(airport);
        return new ResponseMessage(200, "Tạo sân bay thành công", "", null);
    }

    @GetMapping("/findAll")
    public ResponseMessage findAll() {
        return new ResponseMessage(200, "Success", airportService.findAll(), null);
    }

    @GetMapping("/detail")
    public ResponseMessage getSite(@PathVariable Long id) {
        Optional<Airport> siteOptional = airportService.findById(id);
        return siteOptional.map(site -> new ResponseMessage(200, "Success", site, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @PutMapping("/updateSite")
    public ResponseMessage updateSite(@PathVariable Long id, @RequestBody Airport airport) {
        Optional<Airport> airportOptional = airportService.findById(id);
        return airportOptional.map(site1 -> {
            airport.setId(site1.getId());
            airport.setCreatedDate(localDateTime.toString());
            airport.setCreatedBy(airport.getCreator());
            airportService.save(airport);
            return new ResponseMessage(200, "Success", "", null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @GetMapping("/deleteSite")
    public ResponseMessage deleteSite(@PathVariable Long id) {
        Optional<Airport> airportOptional = airportService.findById(id);
        return airportOptional.map(site -> {
            airportService.remove(id);
            return new ResponseMessage(200, "Success", null, null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }
}
