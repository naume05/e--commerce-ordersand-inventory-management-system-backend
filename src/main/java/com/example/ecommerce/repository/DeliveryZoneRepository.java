package com.example.ecommerce.repository;

import com.example.ecommerce.model.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, UUID> {

    @Query("SELECT z FROM DeliveryZone z WHERE z.riders IS EMPTY")
    List<DeliveryZone> findUnusedDeliveryZones();
}
