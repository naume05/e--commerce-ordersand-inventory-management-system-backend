package com.example.ecommerce.service;

import com.example.ecommerce.model.DeliveryBike;
import com.example.ecommerce.model.DeliveryRider;
import com.example.ecommerce.model.DeliveryZone;
import com.example.ecommerce.repository.DeliveryBikeRepository;
import com.example.ecommerce.repository.DeliveryRiderRepository;
import com.example.ecommerce.repository.DeliveryZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryRiderService {

    private final DeliveryRiderRepository deliveryRiderRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryBikeRepository deliveryBikeRepository;

    public DeliveryRiderService(DeliveryRiderRepository deliveryRiderRepository,
                                DeliveryZoneRepository deliveryZoneRepository,
                                DeliveryBikeRepository deliveryBikeRepository) {
        this.deliveryRiderRepository = deliveryRiderRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.deliveryBikeRepository = deliveryBikeRepository;
    }

    public String save(DeliveryRider rider) {
        deliveryRiderRepository.save(rider);
        return "Delivery rider saved successfully";
    }

    public List<DeliveryRider> getAll() {
        return deliveryRiderRepository.findAll();
    }

    public Optional<DeliveryRider> getById(UUID id) {
        return deliveryRiderRepository.findById(id);
    }

    public String update(UUID id, DeliveryRider updated) {
        Optional<DeliveryRider> optional = deliveryRiderRepository.findById(id);
        if (optional.isPresent()) {
            DeliveryRider existing = optional.get();
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setPhone(updated.getPhone());
            existing.setDeliveryBike(updated.getDeliveryBike());
            if (updated.getDeliveryZones() != null) {
                existing.setDeliveryZones(updated.getDeliveryZones());
            }
            deliveryRiderRepository.save(existing);
            return "Delivery rider updated successfully";
        }
        return "The delivery rider with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<DeliveryRider> optional = deliveryRiderRepository.findById(id);
        if (optional.isPresent()) {
            deliveryRiderRepository.delete(optional.get());
            return "Delivery rider deleted successfully";
        }
        return "The delivery rider with that id does not exist";
    }

    // C1: JPQL (JOIN on collection, LOWER) - Delivery riders by delivery zone
    public List<DeliveryRider> getRidersByDeliveryZone(String name) {
        return deliveryRiderRepository.findByDeliveryZoneName(name);
    }

    // D3: Add one delivery zone to a delivery rider
    public String addDeliveryZoneToRider(UUID deliveryRiderId, UUID deliveryZoneId) {
        Optional<DeliveryRider> riderOpt = deliveryRiderRepository.findById(deliveryRiderId);
        if (riderOpt.isEmpty()) {
            return "The delivery rider with that id does not exist";
        }

        Optional<DeliveryZone> zoneOpt = deliveryZoneRepository.findById(deliveryZoneId);
        if (zoneOpt.isEmpty()) {
            return "That delivery zone does not exist";
        }

        DeliveryRider rider = riderOpt.get();
        DeliveryZone zone = zoneOpt.get();

        boolean alreadyHas = rider.getDeliveryZones().stream()
                .anyMatch(z -> z.getId().equals(zone.getId()));
        if (alreadyHas) {
            return "The delivery rider already has that delivery zone";
        }

        rider.getDeliveryZones().add(zone);
        deliveryRiderRepository.save(rider);
        return "Delivery zone added to the delivery rider";
    }

    // D4: Move a delivery rider to another delivery bike
    public String changeDeliveryBike(UUID deliveryRiderId, int bikeNumber) {
        Optional<DeliveryRider> riderOpt = deliveryRiderRepository.findById(deliveryRiderId);
        if (riderOpt.isEmpty()) {
            return "The delivery rider with that id does not exist";
        }

        Optional<DeliveryBike> bikeOpt = deliveryBikeRepository.findByBikeNumber(bikeNumber);
        if (bikeOpt.isEmpty()) {
            return "The delivery bike with that bikeNumber does not exist";
        }

        DeliveryBike bike = bikeOpt.get();
        if (deliveryRiderRepository.existsByDeliveryBike(bike)) {
            return "That delivery bike is taken by another delivery rider";
        }

        DeliveryRider rider = riderOpt.get();
        rider.setDeliveryBike(bike);
        deliveryRiderRepository.save(rider);
        return "Delivery bike changed successfully";
    }
}
