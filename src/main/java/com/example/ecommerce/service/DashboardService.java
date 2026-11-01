package com.example.ecommerce.service;

import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final DeliveryRiderRepository deliveryRiderRepository;
    private final DeliveryBikeRepository deliveryBikeRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final DeliveryReceiptRepository deliveryReceiptRepository;

    public DashboardService(CustomerRepository customerRepository,
                            DeliveryRiderRepository deliveryRiderRepository,
                            DeliveryBikeRepository deliveryBikeRepository,
                            CustomerOrderRepository customerOrderRepository,
                            DeliveryReceiptRepository deliveryReceiptRepository) {
        this.customerRepository = customerRepository;
        this.deliveryRiderRepository = deliveryRiderRepository;
        this.deliveryBikeRepository = deliveryBikeRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.deliveryReceiptRepository = deliveryReceiptRepository;
    }

    // Bonus 1: Dashboard summary Map<String, Long> (LinkedHashMap to keep the order)
    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("customers", customerRepository.count());
        summary.put("deliveryRiders", deliveryRiderRepository.count());
        summary.put("freeDeliveryBikes", (long) deliveryBikeRepository.findFreeDeliveryBikes().size());
        summary.put("ordersToday", customerOrderRepository.countByOrderDate(LocalDate.now()));
        summary.put("placed", customerOrderRepository.countByStatus(OrderStatus.PLACED));
        summary.put("deliveryReceipts", deliveryReceiptRepository.count());
        return summary;
    }
}
