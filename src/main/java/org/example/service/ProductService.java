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

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome inválido");

        if (price < 0)
            throw new IllegalArgumentException("Preço inválido");

        if (quantity < 0)
            throw new IllegalArgumentException("Quantidade inválida");

        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Categoria inválida");

        if (imagePath == null || imagePath.isBlank()) {
            throw new IllegalArgumentException("É obrigatório enviar uma imagem do produto.");
        }

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

        if (existing == null)
            throw new IllegalArgumentException("ID inválido, produto não encontrado");

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome inválido");

        if (price < 0)
            throw new IllegalArgumentException("Preço inválido");

        if (quantity < 0)
            throw new IllegalArgumentException("Quantidade inválida");

        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Categoria inválida");

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
