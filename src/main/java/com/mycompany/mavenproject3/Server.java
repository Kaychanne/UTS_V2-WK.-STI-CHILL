package com.mycompany.mavenproject3;

import com.google.gson.Gson;
import com.mycompany.mavenproject3.customer.Customer;
import com.mycompany.mavenproject3.customer.CustomerService;
import com.mycompany.mavenproject3.product.Product;
import com.mycompany.mavenproject3.product.ProductService;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

public class Server extends Thread {
    public void run() {
        port(4567);
        Gson gson = new Gson();

         path("/api", () -> {
            get("/products", (req, res) -> {
                res.type("application/json");
                return ProductService.getAllProducts();
            }, gson::toJson);

            get("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                Product product = ProductService.getProductById(id);
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                return product;
            }, gson::toJson);

            post("/products", (req, res) -> {
                res.type("application/json");
                Product product = gson.fromJson(req.body(), Product.class);
                Product added = ProductService.addProduct(product);
                res.status(201); // Created
                return added;
            }, gson::toJson);

            put("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                Product product = gson.fromJson(req.body(), Product.class);
                Product updated = ProductService.updateProductById(id, product);
                if (updated == null) {
                    res.status(404);
                    return "Product not found";
                }
                return updated;
            }, gson::toJson);

            delete("/products/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params(":id"));
                boolean success = ProductService.deleteProductById(id);
                if (success) {
                    res.status(204);
                    return "";
                } else {
                    res.status(404);
                    return "Product not found";
                }
            });

            // Get semua customer
            get("/customer", (req, res) -> {
                res.type("application/json");
                return CustomerService.getAllCustomers();
            }, gson::toJson);

            // Get customer by ID
            get("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Customer customer = CustomerService.getCustomerById(id);
                if (customer == null) {
                    res.status(404);
                    return "Customer not found";
                }
                return customer;
            }, gson::toJson);

            // Tambah customer
            post("/customer", (req, res) -> {
                res.type("application/json");
                Customer customer = gson.fromJson(req.body(), Customer.class);
                return CustomerService.addCustomer(customer);
            }, gson::toJson);

            // Update customer
            put("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Customer customer = gson.fromJson(req.body(), Customer.class);
                return CustomerService.updateCustomerById(id, customer);
            }, gson::toJson);

            // Hapus customer
            delete("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                if (CustomerService.deleteCustomerById(id)) {
                    res.status(204);
                    return "";
                } else {
                    res.status(404);
                    return "Customer not found";
                }
            });
        });
    }
}
