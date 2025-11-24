package org.example;

import io.javalin.Javalin;
import org.example.controller.ProductController;
import org.example.service.ProductService;
import org.example.repository.ProductRepository;

public class Main {
    public static void main(String[] args) {

        ProductRepository repository = new ProductRepository();
        ProductService service = new ProductService(repository);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", io.javalin.http.staticfiles.Location.CLASSPATH);
        });




        new ProductController(app, service);

        app.start(7000);

        System.out.println("Servidor rodando: http://localhost:7000/products");
    }
}
