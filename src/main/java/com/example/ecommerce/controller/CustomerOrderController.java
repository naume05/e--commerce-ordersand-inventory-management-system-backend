package com.example.ecommerce.controller;

import com.example.ecommerce.model.CustomerOrder;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.service.CustomerOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer-orders")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    // A2 & D1: Safe saving
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody CustomerOrder order) {
        String message = customerOrderService.save(order);
        if (message.equals("Customer order date cannot be in the past")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } else if (message.equals("Delivery rider already has an order on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerOrder>> getAll() {
        List<CustomerOrder> list = customerOrderService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<CustomerOrder> optional = customerOrderService.getById(id);
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The customer order with that id does not exist");
        }
    }

    // A2 & D2: PUT update
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody CustomerOrder order) {
        String message = customerOrderService.update(id, order);
        if (message.equals("The customer order with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // A2 & E3: DELETE customer order
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = customerOrderService.delete(id);
        if (message.equals("The customer order with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("That customer order has delivery receipts, delete them first")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // B2: DERIVED (enum parameter, OrderBy) - GET /api/customer-orders/by-status?status=PLACED
    @GetMapping("/by-status")
    public ResponseEntity<List<CustomerOrder>> getByStatus(@RequestParam OrderStatus status) {
        List<CustomerOrder> list = customerOrderService.getOrdersByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    // B3: DERIVED (Between) - GET /api/customer-orders/between?start=2026-10-01&end=2026-10-31
    @GetMapping("/between")
    public ResponseEntity<List<CustomerOrder>> getBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<CustomerOrder> list = customerOrderService.getOrdersBetweenDates(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    // D2: PATCH /api/customer-orders/{id}/status?status=SHIPPED
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        String message = customerOrderService.updateStatus(id, status);
        if (message.equals("The customer order with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.startsWith("Cannot change status from")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }

    // F1: JPQL (GROUP BY, COUNT) - GET /api/customer-orders/stats/by-status
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getStatsByStatus() {
        List<Object[]> stats = customerOrderService.getOrderStatsByStatus();
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }

    // F2: JPQL (UPDATE, @Modifying, @Transactional) - PATCH /api/customer-orders/cancel-day?deliveryRiderId=...&date=2026-10-20
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID deliveryRiderId, @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        String message = customerOrderService.cancelOrdersForDeliveryRiderOnDate(deliveryRiderId, parsedDate);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    // Bonus 2: Pageable - GET /api/customer-orders/page?page=0&size=5&sort=orderDate,desc
    @GetMapping("/page")
    public ResponseEntity<Page<CustomerOrder>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate,desc") String sort) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0];
            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sortObj = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<CustomerOrder> orderPage = customerOrderService.getOrdersPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(orderPage);
    }
}
