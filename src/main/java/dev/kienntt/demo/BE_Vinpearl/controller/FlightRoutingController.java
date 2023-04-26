package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.model.FlightRoute;
import dev.kienntt.demo.BE_Vinpearl.base.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.service.FlightRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/flight-routing")
public class FlightRoutingController {
    @Autowired
    private FlightRouteService flightRouteService;

    LocalDateTime localDateTime = LocalDateTime.now();

    @PostMapping("/create")
    public ResponseMessage create(@RequestBody FlightRoute flightRoute) {
        flightRoute.setCreatedDate(localDateTime.toString());
        flightRoute.setCreatedBy(flightRoute.getCreator());
        flightRouteService.save(flightRoute);
        return new ResponseMessage(200, "Tạo sân bay thành công", "", null);
    }

    @GetMapping("/findAll")
    public ResponseMessage findAll() {
        return new ResponseMessage(200, "Success", flightRouteService.findAll(), null);
    }

    @GetMapping("/detail")
    public ResponseMessage getSite(@PathVariable Long id) {
        Optional<FlightRoute> siteOptional = flightRouteService.findById(id);
        return siteOptional.map(site -> new ResponseMessage(200, "Success", site, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @PutMapping("/updateSite")
    public ResponseMessage updateSite(@PathVariable Long id, @RequestBody FlightRoute flightRoute) {
        Optional<FlightRoute> flightRouteOptional = flightRouteService.findById(id);
        return flightRouteOptional.map(site1 -> {
            flightRoute.setId(site1.getId());
            flightRoute.setCreatedDate(localDateTime.toString());
            flightRoute.setCreatedBy(flightRoute.getCreator());
            flightRouteService.save(flightRoute);
            return new ResponseMessage(200, "Success", "", null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @GetMapping("/deleteSite")
    public ResponseMessage deleteSite(@PathVariable Long id) {
        Optional<FlightRoute> flightRouteOptional = flightRouteService.findById(id);
        return flightRouteOptional.map(site -> {
            flightRouteService.remove(id);
            return new ResponseMessage(200, "Success", null, null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }
}
