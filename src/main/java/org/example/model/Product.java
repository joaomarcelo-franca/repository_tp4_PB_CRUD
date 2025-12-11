package org.example.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private String imagePath;
    private static int sequence = 0;

    public Product(String name, double price, int quantity, String category, String imagePath) {
        sequence++;

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do produto é obrigatório");

        if (price < 0)
            throw new IllegalArgumentException("Preço não pode ser negativo");

        if (quantity < 0)
            throw new IllegalArgumentException("Quantidade inválida");

        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Categoria é obrigatória");

        if (imagePath == null || imagePath.isBlank()) {
            throw new IllegalArgumentException("É obrigatório enviar uma imagem do produto.");
        }

        this.id = sequence;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome inválido!");
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0 ) throw new IllegalArgumentException("Preço inválido!");
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantidade inválida!");
        this.quantity = quantity;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Categoria inválida!");
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public void update(String name, double price, int quantity, String category, String imagePath) {
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(category);

        if (imagePath != null && !imagePath.isBlank()) {
            setImagePath(imagePath);
        }
    }

}
