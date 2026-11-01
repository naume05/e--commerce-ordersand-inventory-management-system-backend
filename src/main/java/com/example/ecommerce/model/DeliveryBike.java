package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "delivery_bike")
public class DeliveryBike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int bikeNumber;

    private String plateNumber;

    public DeliveryBike() {
    }

    public DeliveryBike(UUID id, int bikeNumber, String plateNumber) {
        this.id = id;
        this.bikeNumber = bikeNumber;
        this.plateNumber = plateNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(int bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryBike that = (DeliveryBike) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
