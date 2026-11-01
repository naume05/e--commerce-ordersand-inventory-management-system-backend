package com.example.ecommerce.config;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DeliveryZoneRepository deliveryZoneRepository;
    private final DeliveryBikeRepository deliveryBikeRepository;
    private final DeliveryRiderRepository deliveryRiderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final DeliveryReceiptRepository deliveryReceiptRepository;

    public DataInitializer(DeliveryZoneRepository deliveryZoneRepository,
                           DeliveryBikeRepository deliveryBikeRepository,
                           DeliveryRiderRepository deliveryRiderRepository,
                           CustomerRepository customerRepository,
                           CustomerOrderRepository customerOrderRepository,
                           DeliveryReceiptRepository deliveryReceiptRepository) {
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.deliveryBikeRepository = deliveryBikeRepository;
        this.deliveryRiderRepository = deliveryRiderRepository;
        this.customerRepository = customerRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.deliveryReceiptRepository = deliveryReceiptRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return; // Seed data already exists
        }

        // 1. DeliveryZone: 4 (Nyarugenge, Gasabo, Kicukiro, Bugesera). Nobody holds Bugesera.
        DeliveryZone nyarugenge = deliveryZoneRepository.save(new DeliveryZone(null, "Nyarugenge"));
        DeliveryZone gasabo = deliveryZoneRepository.save(new DeliveryZone(null, "Gasabo"));
        DeliveryZone kicukiro = deliveryZoneRepository.save(new DeliveryZone(null, "Kicukiro"));
        DeliveryZone bugesera = deliveryZoneRepository.save(new DeliveryZone(null, "Bugesera"));

        // 2. DeliveryBike: 4 delivery bikes numbered 1, 2, 3, 4. Delivery bike 4 has no delivery rider.
        DeliveryBike bike1 = deliveryBikeRepository.save(new DeliveryBike(null, 1, "RAD 101 A"));
        DeliveryBike bike2 = deliveryBikeRepository.save(new DeliveryBike(null, 2, "RAD 102 B"));
        DeliveryBike bike3 = deliveryBikeRepository.save(new DeliveryBike(null, 3, "RAD 103 C"));
        DeliveryBike bike4 = deliveryBikeRepository.save(new DeliveryBike(null, 4, "RAD 104 D"));

        // 3. DeliveryRider: 3 delivery riders, each with a delivery bike.
        // One holds two delivery zones, one holds exactly one.
        DeliveryRider rider1 = new DeliveryRider(null, "Eric", "Mugisha", "0788111111", bike1, new ArrayList<>(Arrays.asList(gasabo, kicukiro)));
        DeliveryRider rider2 = new DeliveryRider(null, "Jean", "Kamanzi", "0788222222", bike2, new ArrayList<>(Arrays.asList(nyarugenge)));
        DeliveryRider rider3 = new DeliveryRider(null, "Patrick", "Niyonzima", "0788333333", bike3, new ArrayList<>(Arrays.asList(gasabo)));

        rider1 = deliveryRiderRepository.save(rider1);
        rider2 = deliveryRiderRepository.save(rider2);
        rider3 = deliveryRiderRepository.save(rider3);

        // 4. Customer: 5 customers. Two share the lastName "Murungi".
        // customerCode of one is C-20481
        Customer customer1 = customerRepository.save(new Customer(null, "Naume", "Murungi", "C-20481", "naume.murungi@example.com", "0788000001"));
        Customer customer2 = customerRepository.save(new Customer(null, "Alice", "Murungi", "C-20482", "alice.murungi@example.com", "0788000002"));
        Customer customer3 = customerRepository.save(new Customer(null, "David", "Habimana", "C-20483", "david@example.com", "0788000003"));
        Customer customer4 = customerRepository.save(new Customer(null, "Grace", "Uwase", "C-20484", "grace@example.com", "0788000004"));
        Customer customer5 = customerRepository.save(new Customer(null, "Brian", "Manzi", "C-20485", "brian@example.com", "0788000005"));

        // 5. CustomerOrder: 10 customer orders spread over October and November 2026 using all four statuses.
        // One customer has at least 3 customer orders (Customer 1 has 4).
        // The same delivery rider (rider1) has two customer orders on 2026-10-20, one of them CANCELLED.
        // At least 2 customer orders are DELIVERED (order3 and order4).
        CustomerOrder order1 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 20), OrderStatus.SHIPPED, 15000.0, "MOMO", customer1, rider1));
        CustomerOrder order2 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 20), OrderStatus.CANCELLED, 8000.0, "CARD", customer2, rider1));
        CustomerOrder order3 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 15), OrderStatus.DELIVERED, 25000.0, "MOMO", customer1, rider1));
        CustomerOrder order4 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 25), OrderStatus.DELIVERED, 40000.0, "CASH", customer1, rider2));
        CustomerOrder order5 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 5), OrderStatus.PLACED, 12000.0, "MOMO", customer3, rider2));
        CustomerOrder order6 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 10), OrderStatus.PLACED, 18500.0, "CARD", customer4, rider3));
        CustomerOrder order7 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 10, 28), OrderStatus.SHIPPED, 31000.0, "MOMO", customer5, rider3));
        CustomerOrder order8 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 11, 2), OrderStatus.PLACED, 9500.0, "CASH", customer1, rider1));
        CustomerOrder order9 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 11, 5), OrderStatus.PLACED, 22000.0, "MOMO", customer2, rider2));
        CustomerOrder order10 = customerOrderRepository.save(new CustomerOrder(null, LocalDate.of(2026, 11, 10), OrderStatus.SHIPPED, 14000.0, "CARD", customer3, rider3));

        // 6. Delivery Receipts for delivered orders (Part E seed data)
        DeliveryReceipt receipt1 = new DeliveryReceipt(null, "Naume Murungi", "Received in good condition", 1, LocalDate.of(2026, 10, 15), order3);
        DeliveryReceipt receipt2 = new DeliveryReceipt(null, "Naume Murungi", "Left at reception, signed by guard", 2, LocalDate.of(2026, 10, 25), order4);
        deliveryReceiptRepository.save(receipt1);
        deliveryReceiptRepository.save(receipt2);
    }
}
