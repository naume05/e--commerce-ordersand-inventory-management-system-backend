package com.example.ecommerce.service;

import com.example.ecommerce.model.DeliveryBike;
import com.example.ecommerce.repository.DeliveryBikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryBikeService {

    private final DeliveryBikeRepository deliveryBikeRepository;

    public DeliveryBikeService(DeliveryBikeRepository deliveryBikeRepository) {
        this.deliveryBikeRepository = deliveryBikeRepository;
    }

    public String save(DeliveryBike bike) {
        deliveryBikeRepository.save(bike);
        return "Delivery bike saved successfully";
    }

    public List<DeliveryBike> getAll() {
        return deliveryBikeRepository.findAll();
    }

    public Optional<DeliveryBike> getById(UUID id) {
        return deliveryBikeRepository.findById(id);
    }

    public String update(UUID id, DeliveryBike updated) {
        Optional<DeliveryBike> optional = deliveryBikeRepository.findById(id);
        if (optional.isPresent()) {
            DeliveryBike existing = optional.get();
            existing.setBikeNumber(updated.getBikeNumber());
            existing.setPlateNumber(updated.getPlateNumber());
            deliveryBikeRepository.save(existing);
            return "Delivery bike updated successfully";
        }
        return "The delivery bike with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<DeliveryBike> optional = deliveryBikeRepository.findById(id);
        if (optional.isPresent()) {
            deliveryBikeRepository.delete(optional.get());
            return "Delivery bike deleted successfully";
        }
        return "The delivery bike with that id does not exist";
    }

    // C2: JPQL (NOT EXISTS subquery) - Free delivery bikes
    public List<DeliveryBike> getFreeDeliveryBikes() {
        return deliveryBikeRepository.findFreeDeliveryBikes();
    }
}
