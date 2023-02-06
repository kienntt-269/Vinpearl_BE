package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.model.ResponseMessage;
import dev.kienntt.demo.BE_Vinpearl.model.Site;
import dev.kienntt.demo.BE_Vinpearl.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/site")
public class SiteController {
    @Autowired
    private SiteService siteService;

    @PostMapping("/create")
    public ResponseMessage create(@RequestBody Site site) {
        siteService.save(site);
        return new ResponseMessage(200, "Tạo khu vực thành công", "", null);
    }

    @GetMapping("/findAll")
    public ResponseMessage findAll() {
        return new ResponseMessage(200, "Success", siteService.findAll(), null);
    }

    @GetMapping("/detail")
    public ResponseMessage getSite(@PathVariable Long id) {
        Optional<Site> siteOptional = siteService.findById(id);
        return siteOptional.map(site -> new ResponseMessage(200, "Success", site, null))
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @PutMapping("/updateSite")
    public ResponseMessage updateSite(@PathVariable Long id, @RequestBody Site site) {
        Optional<Site> siteOptional = siteService.findById(id);
        return siteOptional.map(site1 -> {
            site.setId(site1.getId());
            siteService.save(site);
            return new ResponseMessage(200, "Success", "", null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }

    @GetMapping("/deleteSite")
    public ResponseMessage deleteSite(@PathVariable Long id) {
        Optional<Site> siteOptional = siteService.findById(id);
        return siteOptional.map(site -> {
            siteService.remove(id);
            return new ResponseMessage(200, "Success", null, null);
        })
                .orElseGet(() -> new ResponseMessage(404, "Error", null, "No result with query"));
    }
}