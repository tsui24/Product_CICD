package com.tinder.product_cicd.init;

import com.tinder.product_cicd.product.Product;
import com.tinder.product_cicd.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;

    public DataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            List<Product> samples = List.of(
                    new Product("Basic Chair", "Comfortable wooden chair", new BigDecimal("49.99")),
                    new Product("Standing Desk", "Height-adjustable standing desk", new BigDecimal("249.00")),
                    new Product("Noise-Cancelling Headphones", "Over-ear, long battery life", new BigDecimal("129.50")),
                    new Product("Wireless Mouse", "Ergonomic wireless mouse", new BigDecimal("29.95")),
                    new Product("Mechanical Keyboard", "RGB mechanical keyboard", new BigDecimal("89.99"))
            );
            repository.saveAll(samples);
            System.out.println("Inserted " + samples.size() + " sample products.");
        }
    }
}
