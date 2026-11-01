package com.example.ecommerce.controller;

import com.example.ecommerce.model.DeliveryBike;
import com.example.ecommerce.service.DeliveryBikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-bikes")
public class DeliveryBikeController {

    private final DeliveryBikeService deliveryBikeService;

    public DeliveryBikeController(DeliveryBikeService deliveryBikeService) {
        this.deliveryBikeService = deliveryBikeService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody DeliveryBike bike) {
        String message = deliveryBikeService.save(bike);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeliveryBike>> getAll() {
        List<DeliveryBike> list = deliveryBikeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<DeliveryBike> optional = deliveryBikeService.getById(id);
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The delivery bike with that id does not exist");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody DeliveryBike bike) {
        String message = deliveryBikeService.update(id, bike);
        if (message.equals("The delivery bike with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = deliveryBikeService.delete(id);
        if (message.equals("The delivery bike with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // C2: JPQL (NOT EXISTS subquery) - GET /api/delivery-bikes/free
    @GetMapping("/free")
    public ResponseEntity<List<DeliveryBike>> getFree() {
        List<DeliveryBike> freeBikes = deliveryBikeService.getFreeDeliveryBikes();
        return ResponseEntity.status(HttpStatus.OK).body(freeBikes);
    }
}
