package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "delivery_rider")
public class DeliveryRider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "delivery_bike_id")
    private DeliveryBike deliveryBike;

    @ManyToMany
    @JoinTable(
            name = "delivery_rider_delivery_zone",
            joinColumns = @JoinColumn(name = "delivery_rider_id"),
            inverseJoinColumns = @JoinColumn(name = "delivery_zone_id")
    )
    private List<DeliveryZone> deliveryZones = new ArrayList<>();

    public DeliveryRider() {
    }

    public DeliveryRider(UUID id, String firstName, String lastName, String phone, DeliveryBike deliveryBike, List<DeliveryZone> deliveryZones) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.deliveryBike = deliveryBike;
        this.deliveryZones = deliveryZones != null ? deliveryZones : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DeliveryBike getDeliveryBike() {
        return deliveryBike;
    }

    public void setDeliveryBike(DeliveryBike deliveryBike) {
        this.deliveryBike = deliveryBike;
    }

    public List<DeliveryZone> getDeliveryZones() {
        return deliveryZones;
    }

    public void setDeliveryZones(List<DeliveryZone> deliveryZones) {
        this.deliveryZones = deliveryZones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryRider that = (DeliveryRider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
