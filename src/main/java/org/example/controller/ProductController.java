package org.example.controller;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import org.example.model.Product;
import org.example.service.FileService;
import org.example.service.ProductService;
import org.example.view.ProductView;

import java.util.Map;

public class ProductController {

    private final ProductService service;
    private final FileService fileService;

    public ProductController(Javalin app, ProductService service, FileService fileService) {
        this.service = service;
        this.fileService = fileService;
        registerRoutes(app);
    }

    private void registerRoutes(Javalin app) {

        // LISTAR PRODUTOS
        app.get("/products", ctx -> {
            ctx.html(ProductView.renderList(service.findAll()));
        });

        // FORMULÁRIO DE NOVO PRODUTO
        app.get("/products/new", ctx -> {
            ctx.html(ProductView.renderForm(Map.of()));
        });

        // SALVAR NOVO PRODUTO
        app.post("/products/save", ctx -> {
            try {
                String name = ctx.formParam("name");
                double price = Double.parseDouble(ctx.formParam("price"));
                int quantity = Integer.parseInt(ctx.formParam("quantity"));
                String category = ctx.formParam("category");

                UploadedFile uploaded = ctx.uploadedFile("image");

                if (uploaded == null || uploaded.filename() == null || uploaded.filename().isBlank()) {
                    ctx.status(400).result("Erro: É obrigatório enviar uma imagem do produto.");
                    return;
                }

                String imagePath = fileService.saveImage(uploaded);

                service.create(name, price, quantity, category, imagePath);

                ctx.redirect("/products");

            } catch (Exception e) {
                ctx.status(400).result("Erro ao salvar: " + e.getMessage());
            }
        });

        // FORMULÁRIO DE EDIÇÃO
        app.get("/products/edit/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product p = service.findById(id);

            ctx.html(ProductView.renderForm(Map.of(
                    "id", p.getId(),
                    "name", p.getName(),
                    "price", p.getPrice(),
                    "quantity", p.getQuantity(),
                    "category", p.getCategory(),
                    "imagePath", p.getImagePath()
            )));
        });

        // ATUALIZAR PRODUTO
        app.post("/products/update/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                String name = ctx.formParam("name");
                double price = Double.parseDouble(ctx.formParam("price"));
                int quantity = Integer.parseInt(ctx.formParam("quantity"));
                String category = ctx.formParam("category");

                UploadedFile uploaded = ctx.uploadedFile("image");

                String imagePath;
                if (uploaded != null && uploaded.filename() != null && !uploaded.filename().isBlank()) {
                    imagePath = fileService.saveImage(uploaded);
                } else {
                    imagePath = service.findById(id).getImagePath();
                }

                service.update(id, name, price, quantity, category, imagePath);

                ctx.redirect("/products");

            } catch (Exception e) {
                ctx.status(400).result("Erro ao atualizar: " + e.getMessage());
            }
        });

        // DELETAR PRODUTO
        app.post("/products/delete/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            service.delete(id);
            ctx.redirect("/products");
        });
    }

    // ---------------- MÉTODO PARA SALVAR IMAGEM ----------------
//    private String saveImage(UploadedFile file) {
//        if (file == null || file.filename() == null || file.filename().isBlank()) {
//            return null;
//        }
//
//        try {
//            String uploadDir = "src/main/resources/public/uploads/";
//            File dir = new File(uploadDir);
//            if (!dir.exists()) dir.mkdirs();
//
//            String fileName = System.currentTimeMillis() + "_" + file.filename();
//            String filePath = uploadDir + fileName;
//
//            try (InputStream is = file.content();
//                 FileOutputStream fos = new FileOutputStream(filePath)) {
//
//                byte[] buffer = new byte[1024];
//                int bytes;
//                while ((bytes = is.read(buffer)) != -1) {
//                    fos.write(buffer, 0, bytes);
//                }
//            }
//
//            return "src/main/resources/public/uploads/" + fileName;
////            return "/uploads/" + fileName;
//
//        } catch (Exception e) {
//            System.out.println("Erro ao salvar imagem: " + e.getMessage());
//            return null;
//        }
//    }
}
