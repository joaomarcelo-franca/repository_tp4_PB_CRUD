package org.example.repository;

import org.example.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public Product create(Product product) {
        products.add(product);
        return product;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Product findById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean update(int id, Product updatedProduct) {
        Product existing = findById(id);
        if (existing == null) return false;

        existing.update(
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getQuantity(),
                updatedProduct.getCategory(),
                updatedProduct.getImagePath()
        );

        return true;
    }


    public boolean delete(int id) {
        Product p = findById(id);
        if (p == null) return false;
        return products.remove(p);
    }
}
