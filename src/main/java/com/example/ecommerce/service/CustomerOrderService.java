package com.example.ecommerce.service;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.CustomerOrder;
import com.example.ecommerce.model.DeliveryRider;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.CustomerOrderRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.DeliveryReceiptRepository;
import com.example.ecommerce.repository.DeliveryRiderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryRiderRepository deliveryRiderRepository;
    private final DeliveryReceiptRepository deliveryReceiptRepository;

    public CustomerOrderService(CustomerOrderRepository customerOrderRepository,
                                CustomerRepository customerRepository,
                                DeliveryRiderRepository deliveryRiderRepository,
                                DeliveryReceiptRepository deliveryReceiptRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerRepository = customerRepository;
        this.deliveryRiderRepository = deliveryRiderRepository;
        this.deliveryReceiptRepository = deliveryReceiptRepository;
    }

    // D1 (modify A2 save endpoint)
    public String save(CustomerOrder order) {
        if (order.getOrderDate() != null && order.getOrderDate().isBefore(LocalDate.now())) {
            return "Customer order date cannot be in the past";
        }

        if (order.getDeliveryRider() != null && order.getDeliveryRider().getId() != null) {
            Optional<DeliveryRider> riderOpt = deliveryRiderRepository.findById(order.getDeliveryRider().getId());
            if (riderOpt.isPresent()) {
                DeliveryRider rider = riderOpt.get();
                if (customerOrderRepository.existsByDeliveryRiderAndOrderDateAndStatusNot(
                        rider, order.getOrderDate(), OrderStatus.CANCELLED)) {
                    return "Delivery rider already has an order on that date";
                }
                order.setDeliveryRider(rider);
            }
        }

        if (order.getCustomer() != null && order.getCustomer().getId() != null) {
            customerRepository.findById(order.getCustomer().getId()).ifPresent(order::setCustomer);
        }

        order.setStatus(OrderStatus.PLACED);
        customerOrderRepository.save(order);
        return "Customer order saved successfully";
    }

    public List<CustomerOrder> getAll() {
        return customerOrderRepository.findAll();
    }

    public Optional<CustomerOrder> getById(UUID id) {
        return customerOrderRepository.findById(id);
    }

    // D2 / A2 update: general PUT must no longer change the status
    public String update(UUID id, CustomerOrder updated) {
        Optional<CustomerOrder> optional = customerOrderRepository.findById(id);
        if (optional.isPresent()) {
            CustomerOrder existing = optional.get();
            existing.setOrderDate(updated.getOrderDate());
            existing.setTotalAmount(updated.getTotalAmount());
            existing.setPaymentMethod(updated.getPaymentMethod());

            if (updated.getCustomer() != null && updated.getCustomer().getId() != null) {
                customerRepository.findById(updated.getCustomer().getId()).ifPresent(existing::setCustomer);
            } else if (updated.getCustomer() != null) {
                existing.setCustomer(updated.getCustomer());
            }

            if (updated.getDeliveryRider() != null && updated.getDeliveryRider().getId() != null) {
                deliveryRiderRepository.findById(updated.getDeliveryRider().getId()).ifPresent(existing::setDeliveryRider);
            } else if (updated.getDeliveryRider() != null) {
                existing.setDeliveryRider(updated.getDeliveryRider());
            }

            // Note: Status is kept as existing.getStatus(), not changed by PUT
            customerOrderRepository.save(existing);
            return "Customer order updated successfully";
        }
        return "The customer order with that id does not exist";
    }

    // E3 (modify A2 delete endpoint)
    public String delete(UUID id) {
        Optional<CustomerOrder> optional = customerOrderRepository.findById(id);
        if (optional.isPresent()) {
            CustomerOrder order = optional.get();
            if (deliveryReceiptRepository.existsByCustomerOrder(order)) {
                return "That customer order has delivery receipts, delete them first";
            }
            customerOrderRepository.delete(order);
            return "Customer order deleted successfully";
        }
        return "The customer order with that id does not exist";
    }

    // B2: DERIVED (enum parameter, OrderBy) - Customer orders by status, earliest orderDate first
    public List<CustomerOrder> getOrdersByStatus(OrderStatus status) {
        return customerOrderRepository.findByStatusOrderByOrderDateAsc(status);
    }

    // B3: DERIVED (Between) - Customer orders in a date range, both inclusive, ordered by orderDate
    public List<CustomerOrder> getOrdersBetweenDates(LocalDate start, LocalDate end) {
        return customerOrderRepository.findByOrderDateBetweenOrderByOrderDateAsc(start, end);
    }

    // D2: Status flow
    public String updateStatus(UUID id, OrderStatus newStatus) {
        Optional<CustomerOrder> optional = customerOrderRepository.findById(id);
        if (optional.isEmpty()) {
            return "The customer order with that id does not exist";
        }

        CustomerOrder order = optional.get();
        OrderStatus current = order.getStatus();

        boolean allowed = false;
        if (current == OrderStatus.PLACED && (newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED)) {
            allowed = true;
        } else if (current == OrderStatus.SHIPPED && (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED)) {
            allowed = true;
        }

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        order.setStatus(newStatus);
        customerOrderRepository.save(order);
        return "Customer order status updated successfully";
    }

    // F1: JPQL (GROUP BY, COUNT) - Customer orders per status with count
    public List<Object[]> getOrderStatsByStatus() {
        return customerOrderRepository.countOrdersByStatus();
    }

    // F2: JPQL (UPDATE, @Modifying, @Transactional) - Delivery rider is absent
    @Transactional
    public String cancelOrdersForDeliveryRiderOnDate(UUID deliveryRiderId, LocalDate date) {
        int count = customerOrderRepository.cancelOrdersForDeliveryRiderOnDate(deliveryRiderId, date);
        return count + " customer orders cancelled";
    }

    // Bonus 2: Customer orders page by page
    public Page<CustomerOrder> getOrdersPage(Pageable pageable) {
        return customerOrderRepository.findAll(pageable);
    }
}
