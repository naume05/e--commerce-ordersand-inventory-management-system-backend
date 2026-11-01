package com.example.ecommerce.repository;

import com.example.ecommerce.model.CustomerOrder;
import com.example.ecommerce.model.DeliveryReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryReceiptRepository extends JpaRepository<DeliveryReceipt, UUID> {

    @Query("SELECT r FROM DeliveryReceipt r WHERE r.customerOrder.customer.customerCode = :customerCode ORDER BY r.signedDate DESC")
    List<DeliveryReceipt> findByCustomerCode(@Param("customerCode") String customerCode);

    boolean existsByCustomerOrder(CustomerOrder customerOrder);
}
