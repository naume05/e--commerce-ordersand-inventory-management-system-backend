package com.example.ecommerce.repository;

import com.example.ecommerce.model.DeliveryBike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryBikeRepository extends JpaRepository<DeliveryBike, UUID> {

    // C2: JPQL (NOT EXISTS subquery) - Free delivery bikes sorted by bikeNumber
    @Query("SELECT b FROM DeliveryBike b WHERE NOT EXISTS (SELECT r FROM DeliveryRider r WHERE r.deliveryBike = b) ORDER BY b.bikeNumber ASC")
    List<DeliveryBike> findFreeDeliveryBikes();

    // D4: DERIVED - find bike by bikeNumber
    Optional<DeliveryBike> findByBikeNumber(int bikeNumber);
}
