package org.example.view;

import org.example.model.Product;
import java.util.List;
import java.util.Map;

public class ProductView {

    // ------------------------------- LISTAGEM -------------------------------
    public static String renderList(List<Product> products) {
        StringBuilder html = new StringBuilder("""
            <!DOCTYPE html>
            <html lang="pt">
            <head>
                <meta charset="UTF-8">
                <title>Estoque de Produtos</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="container mt-5">
                <h1>Estoque de Produtos</h1>
                <a href="/products/new" class="btn btn-primary mb-3">Adicionar Novo Produto</a>
                <table class="table table-striped align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Imagem</th>
                            <th>Nome</th>
                            <th>Preço</th>
                            <th>Quantidade</th>
                            <th>Categoria</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

        for (Product p : products) {
            html.append(String.format("""
                <tr>
                    <td>%d</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>R$ %.2f</td>
                    <td>%d</td>
                    <td>%s</td>
                    <td>
                        <a href="/products/edit/%d" class="btn btn-sm btn-warning">Editar</a>
                        <form action="/products/delete/%d" method="post" style="display:inline;">
                            <button type="submit" class="btn btn-sm btn-danger">Deletar</button>
                        </form>
                    </td>
                </tr>
                """,
                    p.getId(),
                    p.getImagePath() != null ? "<img src='" + p.getImagePath() + "' width='70' height='70' style='object-fit: cover; border-radius:8px;'>" : "",
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getCategory(),
                    p.getId(),
                    p.getId()
            ));
        }

        html.append("""
                    </tbody>
                </table>
            </body>
            </html>
            """);

        return html.toString();
    }

    // ------------------------------- FORMULÁRIO -------------------------------
    public static String renderForm(Map<String, Object> model) {

        Object id = model.get("id");
        String action = id != null ? "/products/update/" + id : "/products/save"; // CORRIGIDO
        String title = id != null ? "Editar Produto" : "Novo Produto";

        String name = (String) model.getOrDefault("name", "");
        String price = String.valueOf(model.getOrDefault("price", ""));
        String quantity = String.valueOf(model.getOrDefault("quantity", ""));
        String category = (String) model.getOrDefault("category", "");
        String imagePath = (String) model.getOrDefault("imagePath", "");

        return String.format("""
            <!DOCTYPE html>
            <html lang="pt">
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="container mt-5">
                <h1>%s</h1>

                <form action="%s" method="post" enctype="multipart/form-data">
                    
                    <div class="mb-3">
                        <label for="name" class="form-label">Nome do Produto</label>
                        <input type="text" class="form-control" id="name" name="name" value="%s" required>
                    </div>

                    <div class="mb-3">
                        <label for="price" class="form-label">Preço</label>
                        <input type="number" step="0.01" class="form-control" id="price" name="price" value="%s" required>
                    </div>

                    <div class="mb-3">
                        <label for="quantity" class="form-label">Quantidade</label>
                        <input type="number" class="form-control" id="quantity" name="quantity" value="%s" required>
                    </div>

                    <div class="mb-3">
                        <label for="category" class="form-label">Categoria</label>
                        <input type="text" class="form-control" id="category" name="category" value="%s" required>
                    </div>

                    <div class="mb-3">
                        <label for="image" class="form-label">Imagem do Produto</label>
                        <input type="file" class="form-control" id="image" name="image">

                        %s
                    </div>

                    <button type="submit" class="btn btn-success">Salvar</button>
                    <a href="/products" class="btn btn-secondary">Cancelar</a>

                </form>
            </body>
            </html>
            """,
                title,
                title,
                action,
                name,
                price,
                quantity,
                category,
                imagePath != null && !imagePath.isBlank()
                        ? "<p class='mt-2'>Imagem atual:</p><img src='" + imagePath + "' width='120' style='border-radius:8px;'>"
                        : ""
        );
    }
}
