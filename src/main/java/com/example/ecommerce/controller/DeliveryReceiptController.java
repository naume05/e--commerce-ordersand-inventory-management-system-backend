package com.example.ecommerce.controller;

import com.example.ecommerce.model.DeliveryReceipt;
import com.example.ecommerce.service.DeliveryReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-receipts")
public class DeliveryReceiptController {

    private final DeliveryReceiptService deliveryReceiptService;

    public DeliveryReceiptController(DeliveryReceiptService deliveryReceiptService) {
        this.deliveryReceiptService = deliveryReceiptService;
    }

    // E1: POST /api/delivery-receipts/save?customerOrderId=...
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID customerOrderId,
                                       @RequestBody DeliveryReceipt receipt) {
        String message = deliveryReceiptService.save(customerOrderId, receipt);
        if (message.equals("The customer order with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else if (message.equals("Delivery receipts are only for DELIVERED customer orders")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }
    }

    // E2: GET /api/delivery-receipts/customer/{customerCode}
    @GetMapping("/customer/{customerCode}")
    public ResponseEntity<List<DeliveryReceipt>> getByCustomerCode(@PathVariable String customerCode) {
        List<DeliveryReceipt> list = deliveryReceiptService.getReceiptsByCustomerCode(customerCode);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    // E3: DELETE /api/delivery-receipts/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = deliveryReceiptService.delete(id);
        if (message.equals("The delivery receipt with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }
}
