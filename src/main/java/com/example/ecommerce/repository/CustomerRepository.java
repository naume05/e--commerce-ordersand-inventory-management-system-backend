package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    @Query("SELECT DISTINCT o.customer FROM CustomerOrder o WHERE o.deliveryRider.id = :deliveryRiderId")
    List<Customer> findCustomersByDeliveryRiderId(@Param("deliveryRiderId") UUID deliveryRiderId);

    Optional<Customer> findByCustomerCode(String customerCode);
}
