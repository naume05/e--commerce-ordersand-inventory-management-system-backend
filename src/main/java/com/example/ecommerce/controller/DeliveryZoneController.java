package com.example.ecommerce.controller;

import com.example.ecommerce.model.DeliveryZone;
import com.example.ecommerce.service.DeliveryZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-zones")
public class DeliveryZoneController {

    private final DeliveryZoneService deliveryZoneService;

    public DeliveryZoneController(DeliveryZoneService deliveryZoneService) {
        this.deliveryZoneService = deliveryZoneService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody DeliveryZone zone) {
        String message = deliveryZoneService.save(zone);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeliveryZone>> getAll() {
        List<DeliveryZone> list = deliveryZoneService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<DeliveryZone> optional = deliveryZoneService.getById(id);
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The delivery zone with that id does not exist");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody DeliveryZone zone) {
        String message = deliveryZoneService.update(id, zone);
        if (message.equals("The delivery zone with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = deliveryZoneService.delete(id);
        if (message.equals("The delivery zone with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // C3: JPQL (IS EMPTY) - GET /api/delivery-zones/unused
    @GetMapping("/unused")
    public ResponseEntity<List<DeliveryZone>> getUnused() {
        List<DeliveryZone> unused = deliveryZoneService.getUnusedDeliveryZones();
        return ResponseEntity.status(HttpStatus.OK).body(unused);
    }
}
