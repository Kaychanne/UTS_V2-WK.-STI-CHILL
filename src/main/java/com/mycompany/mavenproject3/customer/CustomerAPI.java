package com.mycompany.mavenproject3.customer;

import com.google.gson.Gson;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;

public class CustomerAPI {
public static void main(String[] args) {
        port(4567);
        Gson gson = new Gson();
        CustomerService customerService = new CustomerService();

        path("/api", () -> {
            // Get semua customer
            get("/customer", (req, res) -> {
                res.type("application/json");
                return customerService.getAll();
            }, gson::toJson);

            // Get customer by ID
            get("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Customer customer = customerService.get(id);
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
                return customerService.add(customer);
            }, gson::toJson);

            // Update customer
            put("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                Customer customer = gson.fromJson(req.body(), Customer.class);
                return customerService.update(id, customer);
            }, gson::toJson);

            // Hapus customer
            delete("/customer/:id", (req, res) -> {
                res.type("application/json");
                int id = Integer.parseInt(req.params("id"));
                if (customerService.delete(id)) {
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
