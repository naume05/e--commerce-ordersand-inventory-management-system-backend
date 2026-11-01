package com.example.ecommerce.service;

import com.example.ecommerce.model.CustomerOrder;
import com.example.ecommerce.model.DeliveryReceipt;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.CustomerOrderRepository;
import com.example.ecommerce.repository.DeliveryReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryReceiptService {

    private final DeliveryReceiptRepository deliveryReceiptRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public DeliveryReceiptService(DeliveryReceiptRepository deliveryReceiptRepository,
                                  CustomerOrderRepository customerOrderRepository) {
        this.deliveryReceiptRepository = deliveryReceiptRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    // E1: Write a delivery receipt
    public String save(UUID customerOrderId, DeliveryReceipt receipt) {
        Optional<CustomerOrder> orderOpt = customerOrderRepository.findById(customerOrderId);
        if (orderOpt.isEmpty()) {
            return "The customer order with that id does not exist";
        }

        CustomerOrder order = orderOpt.get();
        if (order.getStatus() != OrderStatus.DELIVERED) {
            return "Delivery receipts are only for DELIVERED customer orders";
        }

        receipt.setSignedDate(LocalDate.now());
        receipt.setCustomerOrder(order);
        deliveryReceiptRepository.save(receipt);
        return "Delivery receipt saved successfully";
    }

    // E2: JPQL (path navigation, ORDER BY) - Delivery receipt history of a customer
    public List<DeliveryReceipt> getReceiptsByCustomerCode(String customerCode) {
        return deliveryReceiptRepository.findByCustomerCode(customerCode);
    }

    // E3: Delete delivery receipt
    public String delete(UUID id) {
        Optional<DeliveryReceipt> receiptOpt = deliveryReceiptRepository.findById(id);
        if (receiptOpt.isEmpty()) {
            return "The delivery receipt with that id does not exist";
        }
        deliveryReceiptRepository.delete(receiptOpt.get());
        return "Delivery receipt deleted successfully";
    }
}
