package org.example.service;

import org.example.model.Product;
import org.example.repository.ProductRepository;

import java.util.List;

public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(String name, double price, int quantity, String category, String imagePath) {

        Product product = new Product(name, price, quantity, category, imagePath);

        return repository.create(product);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(int id) {
        Product product = repository.findById(id);

        if (product == null)
            throw new IllegalArgumentException("Produto não encontrado");

        return product;
    }

    public boolean update(int id, String name, double price, int quantity, String category, String imagePath) {

        Product existing = repository.findById(id);

        Product updated = new Product(name, price, quantity, category, imagePath);

        return repository.update(id, updated);
    }

    public boolean delete(int id) {
        Product product = repository.findById(id);

        if (product == null)
            throw new IllegalArgumentException("Produto não encontrado para deletar");

        return repository.delete(id);
    }
}
