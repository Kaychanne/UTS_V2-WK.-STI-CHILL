package com.mycompany.mavenproject3;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.mavenproject3.customer.Customer;
import com.mycompany.mavenproject3.customer.CustomerService;
import com.mycompany.mavenproject3.product.Product;
import com.mycompany.mavenproject3.product.ProductService;
import com.mycompany.mavenproject3.transaction.Transaction;
import com.mycompany.mavenproject3.transaction.TransactionService;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

public class Server extends Thread {
    public void run() {
        port(4567);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Transaction.class, new TransactionDeserializer())
                .create();

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

            get("/customer", (req, res) -> {
                res.type("application/json");
                return CustomerService.getAllCustomers();
            }, gson::toJson);

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

            post("/customer", (req, res) -> {
                res.type("application/json");
                Customer customer = gson.fromJson(req.body(), Customer.class);
                return CustomerService.addCustomer(customer);
            }, gson::toJson);

            put("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Customer customer = gson.fromJson(req.body(), Customer.class);
                return CustomerService.updateCustomerById(id, customer);
            }, gson::toJson);

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

            get("/transaction", (req, res) -> {
                res.type("application/json");
                return TransactionService.getAllTransactions();
            }, gson::toJson);

            get("/transaction/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Transaction transaction = TransactionService.getTransactionById(id);
                if (transaction == null) {
                    res.status(404);
                    return "Transaction not found";
                }
                return transaction;
            }, gson::toJson);

            post("/transaction", (req, res) -> {
                res.type("application/json");
                System.out.println(req.body());
                Transaction transaction = gson.fromJson(req.body(), Transaction.class);
                return TransactionService.addTransaction(transaction);
            }, gson::toJson);

            put("/transaction/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Transaction transaction = gson.fromJson(req.body(), Transaction.class);
                return TransactionService.updateTransactionById(id, transaction);
            }, gson::toJson);

            delete("/transaction/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                if (TransactionService.deleteTransactionById(id)) {
                    res.status(204);
                    return "";
                } else {
                    res.status(404);
                    return "Transaction not found";
                }
            });
        });
    }
}
