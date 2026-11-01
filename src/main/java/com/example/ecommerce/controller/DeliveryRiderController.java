package com.example.ecommerce.controller;

import com.example.ecommerce.model.DeliveryRider;
import com.example.ecommerce.service.DeliveryRiderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-riders")
public class DeliveryRiderController {

    private final DeliveryRiderService deliveryRiderService;

    public DeliveryRiderController(DeliveryRiderService deliveryRiderService) {
        this.deliveryRiderService = deliveryRiderService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody DeliveryRider rider) {
        String message = deliveryRiderService.save(rider);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeliveryRider>> getAll() {
        List<DeliveryRider> list = deliveryRiderService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<DeliveryRider> optional = deliveryRiderService.getById(id);
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The delivery rider with that id does not exist");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody DeliveryRider rider) {
        String message = deliveryRiderService.update(id, rider);
        if (message.equals("The delivery rider with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = deliveryRiderService.delete(id);
        if (message.equals("The delivery rider with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // C1: JPQL (JOIN on collection, LOWER) - GET /api/delivery-riders/by-delivery-zone?name=gasabo
    @GetMapping("/by-delivery-zone")
    public ResponseEntity<List<DeliveryRider>> getByDeliveryZone(@RequestParam String name) {
        List<DeliveryRider> riders = deliveryRiderService.getRidersByDeliveryZone(name);
        return ResponseEntity.status(HttpStatus.OK).body(riders);
    }

    // D3: PUT /api/delivery-riders/{deliveryRiderId}/delivery-zones/{deliveryZoneId}
    @PutMapping("/{deliveryRiderId}/delivery-zones/{deliveryZoneId}")
    public ResponseEntity<String> addDeliveryZone(@PathVariable UUID deliveryRiderId,
                                                  @PathVariable UUID deliveryZoneId) {
        String message = deliveryRiderService.addDeliveryZoneToRider(deliveryRiderId, deliveryZoneId);
        if (message.equals("The delivery rider with that id does not exist") ||
                message.equals("That delivery zone does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("The delivery rider already has that delivery zone")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // D4: PATCH /api/delivery-riders/{deliveryRiderId}/delivery-bike?bikeNumber=4
    @PatchMapping("/{deliveryRiderId}/delivery-bike")
    public ResponseEntity<String> changeDeliveryBike(@PathVariable UUID deliveryRiderId,
                                                     @RequestParam int bikeNumber) {
        String message = deliveryRiderService.changeDeliveryBike(deliveryRiderId, bikeNumber);
        if (message.equals("The delivery rider with that id does not exist") ||
                message.equals("The delivery bike with that bikeNumber does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("That delivery bike is taken by another delivery rider")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }
}
