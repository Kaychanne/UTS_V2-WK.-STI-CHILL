package com.mycompany.mavenproject3.product;

import com.google.gson.Gson;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

public class ProductAPI {
    public static void main(String[] args) {
        port(4567);
        Gson gson = new Gson();
        ProductService productService = new ProductService();

         path("/api", () -> {
            get("/products", (req, res) -> {
                res.type("application/json");
                return productService.getAll();
            }, gson::toJson);

            get("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                Product product = productService.get(id);
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                return product;
            }, gson::toJson);

            post("/products", (req, res) -> {
                res.type("application/json");
                Product product = gson.fromJson(req.body(), Product.class);
                Product added = productService.add(product);
                res.status(201); // Created
                return added;
            }, gson::toJson);

            put("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                Product product = gson.fromJson(req.body(), Product.class);
                Product updated = productService.update(id, product);
                if (updated == null) {
                    res.status(404);
                    return "Product not found";
                }
                return updated;
            }, gson::toJson);

            delete("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                boolean success = productService.delete(id);
                if (success) {
                    res.status(204);
                    return "";
                } else {
                    res.status(404);
                    return "Product not found";
                }
            });
        });
    }
}