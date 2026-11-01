package com.example.ecommerce.service;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.DeliveryRiderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final DeliveryRiderRepository deliveryRiderRepository;

    public CustomerService(CustomerRepository customerRepository,
                           DeliveryRiderRepository deliveryRiderRepository) {
        this.customerRepository = customerRepository;
        this.deliveryRiderRepository = deliveryRiderRepository;
    }

    public String save(Customer customer) {
        customerRepository.save(customer);
        return "Customer saved successfully";
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getById(UUID id) {
        return customerRepository.findById(id);
    }

    public String update(UUID id, Customer updated) {
        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isPresent()) {
            Customer existing = optional.get();
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setCustomerCode(updated.getCustomerCode());
            existing.setEmail(updated.getEmail());
            existing.setPhone(updated.getPhone());
            customerRepository.save(existing);
            return "Customer updated successfully";
        }
        return "The customer with that id does not exist";
    }

    public String delete(UUID id) {
        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isPresent()) {
            customerRepository.delete(optional.get());
            return "Customer deleted successfully";
        }
        return "The customer with that id does not exist";
    }

    // B1: DERIVED (IgnoreCase, OrderBy) - Find customers by lastName
    public List<Customer> getCustomersByLastName(String lastName) {
        return customerRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // C4: JPQL (DISTINCT, path navigation) - Customers of a delivery rider
    public Optional<List<Customer>> getCustomersOfDeliveryRider(UUID deliveryRiderId) {
        if (!deliveryRiderRepository.existsById(deliveryRiderId)) {
            return Optional.empty();
        }
        List<Customer> customers = customerRepository.findCustomersByDeliveryRiderId(deliveryRiderId);
        return Optional.of(customers);
    }
}
