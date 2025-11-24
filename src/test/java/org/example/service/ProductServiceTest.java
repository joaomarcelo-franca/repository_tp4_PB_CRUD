package org.example.service;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setup() {
        repository = new ProductRepository();
        service = new ProductService(repository);
    }

    @Test
    void testCreateProductValid() {
        Product p = service.create("Produto A", 10.5, 5, "Categoria 1", "/uploads/img.jpg");
        assertNotNull(p);
        assertEquals("Produto A", p.getName());
    }
    @Test
    public void testCreateProductWithoutImageThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                service.create("Produto X", 10.0, 5, "Categoria", null)
        );
    }
    @Test
    void testUpdateProduct() {
        Product p = service.create("Produto C", 15.0, 2, "Cat", "/uploads/img.jpg");
        service.update(p.getId(), "Produto C2", 20.0, 3, "Cat", "/uploads/img2.jpg");

        Product updated = service.findById(p.getId());
        assertEquals("Produto C2", updated.getName());
        assertEquals(20.0, updated.getPrice());
        assertEquals("/uploads/img2.jpg", updated.getImagePath());
    }
    @Test
    public void testDeleteProduct() {
        Product p = service.create("Produto X", 10.0, 5, "Categoria", "/uploads/img.jpg");
        service.delete(p.getId());

        // Aqui usamos assertThrows porque findById lança exceção se não encontrar
        assertThrows(IllegalArgumentException.class, () -> service.findById(p.getId()));
    }

}
