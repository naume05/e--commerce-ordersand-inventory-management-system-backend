package com.example.ecommerce;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.DeliveryBike;
import com.example.ecommerce.model.DeliveryZone;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EcommerceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeliveryRiderRepository deliveryRiderRepository;

    @Autowired
    private DeliveryBikeRepository deliveryBikeRepository;

    @Autowired
    private DeliveryZoneRepository deliveryZoneRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private DeliveryReceiptRepository deliveryReceiptRepository;

    // --- Part A tests ---
    @Test
    @Order(1)
    void testA2_GetAllDeliveryZones() throws Exception {
        mockMvc.perform(get("/api/delivery-zones/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));
    }

    @Test
    @Order(2)
    void testA2_GetZoneNotFound() throws Exception {
        mockMvc.perform(get("/api/delivery-zones/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The delivery zone with that id does not exist"));
    }

    // --- Part B tests ---
    @Test
    @Order(3)
    void testB1_FindCustomersByLastName() throws Exception {
        mockMvc.perform(get("/api/customers/by-last-name").param("lastName", "murungi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Alice")))
                .andExpect(jsonPath("$[1].firstName", is("Naume")));
    }

    @Test
    @Order(4)
    void testB2_CustomerOrdersByStatus() throws Exception {
        mockMvc.perform(get("/api/customer-orders/by-status").param("status", "PLACED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(5)
    void testB3_CustomerOrdersBetweenDates() throws Exception {
        mockMvc.perform(get("/api/customer-orders/between")
                        .param("start", "2026-10-01")
                        .param("end", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // --- Part C tests ---
    @Test
    @Order(6)
    void testC1_DeliveryRidersByDeliveryZone() throws Exception {
        mockMvc.perform(get("/api/delivery-riders/by-delivery-zone").param("name", "gasabo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(7)
    void testC2_FreeDeliveryBikes() throws Exception {
        mockMvc.perform(get("/api/delivery-bikes/free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].bikeNumber", is(4)));
    }

    @Test
    @Order(8)
    void testC3_UnusedDeliveryZones() throws Exception {
        mockMvc.perform(get("/api/delivery-zones/unused"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Bugesera")));
    }

    @Test
    @Order(9)
    void testC4_CustomersOfDeliveryRider() throws Exception {
        var riders = deliveryRiderRepository.findAll();
        UUID riderId = riders.get(0).getId();

        mockMvc.perform(get("/api/customers/of-delivery-rider/" + riderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/api/customers/of-delivery-rider/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The delivery rider with that id does not exist"));
    }

    // --- Part D tests ---
    @Test
    @Order(10)
    void testD1_SafeSavingPastDate() throws Exception {
        String pastOrderJson = """
                {
                    "orderDate": "2020-01-01",
                    "status": "PLACED",
                    "totalAmount": 10000.0,
                    "paymentMethod": "CASH"
                }
                """;

        mockMvc.perform(post("/api/customer-orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pastOrderJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Customer order date cannot be in the past"));
    }

    @Test
    @Order(11)
    void testD2_StatusFlowValidAndInvalid() throws Exception {
        var orders = customerOrderRepository.findByStatusOrderByOrderDateAsc(OrderStatus.PLACED);
        UUID orderId = orders.get(0).getId();

        // Valid: PLACED -> SHIPPED
        mockMvc.perform(patch("/api/customer-orders/" + orderId + "/status").param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer order status updated successfully"));

        // Invalid: SHIPPED -> PLACED
        mockMvc.perform(patch("/api/customer-orders/" + orderId + "/status").param("status", "PLACED"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot change status from SHIPPED to PLACED"));
    }

    @Test
    @Order(12)
    void testD3_AddDeliveryZoneToRider() throws Exception {
        var riders = deliveryRiderRepository.findAll();
        var rider = riders.get(1); // Rider 2 has Nyarugenge
        DeliveryZone newZone = deliveryZoneRepository.save(new DeliveryZone(null, "Musanze"));

        // Add Musanze
        mockMvc.perform(put("/api/delivery-riders/" + rider.getId() + "/delivery-zones/" + newZone.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Delivery zone added to the delivery rider"));

        // Conflict: already has Musanze
        mockMvc.perform(put("/api/delivery-riders/" + rider.getId() + "/delivery-zones/" + newZone.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("The delivery rider already has that delivery zone"));
    }

    @Test
    @Order(13)
    void testD4_MoveDeliveryRiderToAnotherDeliveryBike() throws Exception {
        var riders = deliveryRiderRepository.findAll();
        var rider = riders.get(0); // Rider 1 has bike 1

        // Bike 2 is taken by Rider 2 -> 409
        mockMvc.perform(patch("/api/delivery-riders/" + rider.getId() + "/delivery-bike").param("bikeNumber", "2"))
                .andExpect(status().isConflict())
                .andExpect(content().string("That delivery bike is taken by another delivery rider"));

        // Bike 4 is free -> 200
        mockMvc.perform(patch("/api/delivery-riders/" + rider.getId() + "/delivery-bike").param("bikeNumber", "4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Delivery bike changed successfully"));
    }

    // --- Part E tests ---
    @Test
    @Order(14)
    void testE1_WriteDeliveryReceiptRules() throws Exception {
        var deliveredOrders = customerOrderRepository.findByStatusOrderByOrderDateAsc(OrderStatus.DELIVERED);
        UUID deliveredOrderId = deliveredOrders.get(0).getId();

        String receiptJson = """
                {
                    "receivedBy": "Customer",
                    "remarks": "Left at the reception, signed by guard",
                    "parcels": 2
                }
                """;

        mockMvc.perform(post("/api/delivery-receipts/save")
                        .param("customerOrderId", deliveredOrderId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(receiptJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Delivery receipt saved successfully"));

        // Try for CANCELLED order -> 400
        var cancelledOrders = customerOrderRepository.findByStatusOrderByOrderDateAsc(OrderStatus.CANCELLED);
        UUID cancelledOrderId = cancelledOrders.get(0).getId();

        mockMvc.perform(post("/api/delivery-receipts/save")
                        .param("customerOrderId", cancelledOrderId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(receiptJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Delivery receipts are only for DELIVERED customer orders"));
    }

    @Test
    @Order(15)
    void testE2_DeliveryReceiptHistoryOfCustomer() throws Exception {
        mockMvc.perform(get("/api/delivery-receipts/customer/C-20481"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(16)
    void testE3_DeleteOrderProtection() throws Exception {
        var receipts = deliveryReceiptRepository.findAll();
        UUID orderWithReceiptId = receipts.get(0).getCustomerOrder().getId();

        // Cannot delete order with receipts -> 409
        mockMvc.perform(delete("/api/customer-orders/delete/" + orderWithReceiptId))
                .andExpect(status().isConflict())
                .andExpect(content().string("That customer order has delivery receipts, delete them first"));

        // Delete the receipt -> 200
        mockMvc.perform(delete("/api/delivery-receipts/delete/" + receipts.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Delivery receipt deleted successfully"));
    }

    // --- Part F tests ---
    @Test
    @Order(17)
    void testF1_CustomerOrdersPerStatus() throws Exception {
        mockMvc.perform(get("/api/customer-orders/stats/by-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(18)
    void testF2_DeliveryRiderCancelDay() throws Exception {
        var riders = deliveryRiderRepository.findAll();
        UUID riderId = riders.get(0).getId();

        mockMvc.perform(patch("/api/customer-orders/cancel-day")
                        .param("deliveryRiderId", riderId.toString())
                        .param("date", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("customer orders cancelled")));
    }

    // --- Bonus tests ---
    @Test
    @Order(19)
    void testBonus1_DashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers", notNullValue()))
                .andExpect(jsonPath("$.deliveryRiders", notNullValue()))
                .andExpect(jsonPath("$.freeDeliveryBikes", notNullValue()))
                .andExpect(jsonPath("$.ordersToday", notNullValue()))
                .andExpect(jsonPath("$.placed", notNullValue()))
                .andExpect(jsonPath("$.deliveryReceipts", notNullValue()));
    }

    @Test
    @Order(20)
    void testBonus2_CustomerOrdersPage() throws Exception {
        mockMvc.perform(get("/api/customer-orders/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "orderDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages", notNullValue()))
                .andExpect(jsonPath("$.totalElements", notNullValue()))
                .andExpect(jsonPath("$.content", hasSize(5)));
    }
}
