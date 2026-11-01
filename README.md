# E-Commerce Orders and Inventory Management System

**Student:** Naume Murungi  
**Student ID:** 26334  
**Project:** E-Commerce Orders and Inventory Management System  

---

## Overview

This project is a Spring Boot application built with Spring Data JPA and PostgreSQL. It manages the full lifecycle of retail customer orders, delivery zones, delivery bikes, riders, and delivery receipts.

### Key Entities
- **DeliveryZone:** Delivery zones covered by riders (e.g. Gasabo, Kicukiro, Nyarugenge, Bugesera).
- **DeliveryBike:** Bikes assigned to riders.
- **DeliveryRider:** Riders handling customer deliveries across assigned zones.
- **Customer:** Customers placing orders (identified by a unique customer code).
- **CustomerOrder:** Orders placed by customers with statuses: `PLACED`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
- **DeliveryReceipt:** Delivery records written once an order is delivered.

---

## Setup & Running

### Requirements
- Java 21 or higher
- Apache Maven 3.8+
- PostgreSQL 14+ running on port 5432

### 1. Database Configuration
Create a PostgreSQL database named `ecommerce_db`:
```sql
CREATE DATABASE ecommerce_db;
```

Update your database credentials in `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=admin
```

### 2. Start Application
Run the project using Maven:
```
mvn spring-boot:run
```


## API Endpoints

### CRUD Endpoints
- `POST /api/{entity}/save` - Create a new record (returns 201)
- `GET /api/{entity}/all` - Retrieve all records (returns 200)
- `GET /api/{entity}/{id}` - Get record by ID (returns 200 or 404)
- `PUT /api/{entity}/update/{id}` - Update record by ID (returns 200 or 404)
- `DELETE /api/{entity}/delete/{id}` - Delete record by ID (returns 200, 404, or 409)

Available paths: `delivery-zones`, `delivery-bikes`, `delivery-riders`, `customers`, `customer-orders`.

### Query Endpoints
- `GET /api/customers/by-last-name?lastName=murungi` - Find customers by last name sorted alphabetically.
- `GET /api/customer-orders/by-status?status=PLACED` - Find orders by status sorted by date.
- `GET /api/customer-orders/between?start=2026-10-01&end=2026-10-31` - Orders within date range.
- `GET /api/delivery-riders/by-delivery-zone?name=gasabo` - Find riders assigned to a zone.
- `GET /api/delivery-bikes/free` - List available bikes not currently assigned to riders.
- `GET /api/delivery-zones/unused` - List delivery zones that have no assigned riders.
- `GET /api/customers/of-delivery-rider/{id}` - Customers served by a delivery rider.

### Order Operations & Business Rules
- `POST /api/customer-orders/save` - Create order (validates that date is not in the past and rider is not double-booked).
- `PATCH /api/customer-orders/{id}/status?status=SHIPPED` - Change order status according to allowed transitions.
- `PUT /api/delivery-riders/{riderId}/delivery-zones/{zoneId}` - Add a delivery zone to a rider.
- `PATCH /api/delivery-riders/{riderId}/delivery-bike?bikeNumber=4` - Change rider's assigned bike.
- `POST /api/delivery-receipts/save?customerOrderId={id}` - Record delivery receipt for a delivered order.
- `GET /api/delivery-receipts/customer/{customerCode}` - View receipt history for a customer.
- `DELETE /api/delivery-receipts/delete/{id}` - Delete a delivery receipt.
- `GET /api/customer-orders/stats/by-status` - Summary report of orders grouped by status.
- `PATCH /api/customer-orders/cancel-day?deliveryRiderId={id}&date=2026-10-20` - Cancel all non-delivered orders for a rider on a given day.
- `GET /api/dashboard/summary` - Dashboard summary counts.
- `GET /api/customer-orders/page?page=0&size=5&sort=orderDate,desc` - Paginated customer orders list.

---

## Postman Collection

The Postman collection is exported in the project root:
`e--commerce-ordersand-inventory-management-system-midterm.postman_collection.json`
