package com.example.ecommerce.repository;

import com.example.ecommerce.model.CustomerOrder;
import com.example.ecommerce.model.DeliveryRider;
import com.example.ecommerce.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {

    // B2: DERIVED (enum parameter, OrderBy) - Customer orders by status, earliest orderDate first
    List<CustomerOrder> findByStatusOrderByOrderDateAsc(OrderStatus status);

    // B3: DERIVED (Between) - Customer orders in a date range, both inclusive, ordered by orderDate
    List<CustomerOrder> findByOrderDateBetweenOrderByOrderDateAsc(LocalDate start, LocalDate end);

    // D1: DERIVED (existsBy, And, Not) - Check if delivery rider already has an order on that date not cancelled
    boolean existsByDeliveryRiderAndOrderDateAndStatusNot(DeliveryRider deliveryRider, LocalDate orderDate, OrderStatus status);

    // F1: JPQL (GROUP BY, COUNT) - Customer orders per status with count
    @Query("SELECT o.status, COUNT(o) FROM CustomerOrder o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    // F2: JPQL (UPDATE, @Modifying) - Bulk cancel orders for rider on date except DELIVERED
    @Modifying
    @Query("UPDATE CustomerOrder o SET o.status = com.example.ecommerce.model.OrderStatus.CANCELLED WHERE o.deliveryRider.id = :deliveryRiderId AND o.orderDate = :date AND o.status != com.example.ecommerce.model.OrderStatus.DELIVERED")
    int cancelOrdersForDeliveryRiderOnDate(@Param("deliveryRiderId") UUID deliveryRiderId, @Param("date") LocalDate date);

    // Bonus: DERIVED countBy methods for Dashboard
    long countByOrderDate(LocalDate orderDate);
    long countByStatus(OrderStatus status);
}
