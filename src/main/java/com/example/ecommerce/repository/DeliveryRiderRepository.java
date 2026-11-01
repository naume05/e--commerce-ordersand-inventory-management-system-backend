package com.example.ecommerce.repository;

import com.example.ecommerce.model.DeliveryBike;
import com.example.ecommerce.model.DeliveryRider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryRiderRepository extends JpaRepository<DeliveryRider, UUID> {

    @Query("SELECT r FROM DeliveryRider r JOIN r.deliveryZones z WHERE LOWER(z.name) = LOWER(:name)")
    List<DeliveryRider> findByDeliveryZoneName(@Param("name") String name);

    boolean existsByDeliveryBike(DeliveryBike deliveryBike);
}
