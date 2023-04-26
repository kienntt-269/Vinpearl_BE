package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import dev.kienntt.demo.BE_Vinpearl.model.Plane;
import dev.kienntt.demo.BE_Vinpearl.repository.PlaneRepository;
import dev.kienntt.demo.BE_Vinpearl.service.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaneServiceImpl implements PlaneService {
    @Autowired
    private PlaneRepository planeRepository;

    @Override
    public Iterable findAll() {
        return planeRepository.findAll();
    }

    @Override
    public Optional findById(Long id) {
        return planeRepository.findById(id);
    }

    @Override
    public Plane save(Plane plane) {
        return planeRepository.save(plane);
    }

    @Override
    public void remove(Long id) {
        planeRepository.deleteById(id);
    }
}
