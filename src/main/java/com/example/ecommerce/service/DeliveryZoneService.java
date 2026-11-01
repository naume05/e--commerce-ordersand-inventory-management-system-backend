package com.example.ecommerce.service;

import com.example.ecommerce.model.DeliveryZone;
import com.example.ecommerce.repository.DeliveryZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryZoneService {

    private final DeliveryZoneRepository deliveryZoneRepository;

    public DeliveryZoneService(DeliveryZoneRepository deliveryZoneRepository) {
        this.deliveryZoneRepository = deliveryZoneRepository;
    }

    public String save(DeliveryZone zone) {
        deliveryZoneRepository.save(zone);
        return "Delivery zone saved successfully";
    }

    public List<DeliveryZone> getAll() {
        return deliveryZoneRepository.findAll();
    }

    public Optional<DeliveryZone> getById(UUID id) {
        return deliveryZoneRepository.findById(id);
    }

    public String update(UUID id, DeliveryZone updated) {
        Optional<DeliveryZone> optional = deliveryZoneRepository.findById(id);
        if (optional.isPresent()) {
            DeliveryZone existing = optional.get();
            existing.setName(updated.getName());
            deliveryZoneRepository.save(existing);
            return "Delivery zone updated successfully";
        }
        return "The delivery zone with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<DeliveryZone> optional = deliveryZoneRepository.findById(id);
        if (optional.isPresent()) {
            deliveryZoneRepository.delete(optional.get());
            return "Delivery zone deleted successfully";
        }
        return "The delivery zone with that id does not exist";
    }

    // C3: JPQL (IS EMPTY)
    public List<DeliveryZone> getUnusedDeliveryZones() {
        return deliveryZoneRepository.findUnusedDeliveryZones();
    }
}
