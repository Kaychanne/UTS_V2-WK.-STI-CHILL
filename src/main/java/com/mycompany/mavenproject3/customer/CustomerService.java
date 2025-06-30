package com.mycompany.mavenproject3.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.auth.DBConnection;
import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class CustomerService {
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static final String TABLE_NAME = "customer";

    public static List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Customer getCustomerById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Customer addCustomer(Customer customer) {
        String query = "INSERT INTO " + TABLE_NAME + " (code, name) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getCode());
            stmt.setString(2, customer.getName());
            stmt.executeUpdate();

            fireDataChangeListener("add");
            return customer;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Customer updateCustomer(Customer customer, int id) {
        String query = "UPDATE " + TABLE_NAME + " SET code = ?, name = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getCode());
            stmt.setString(2, customer.getName());
            stmt.setInt(3, id);
            stmt.executeUpdate();

            fireDataChangeListener("update");
            return customer;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteCustomerById(int id) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) fireDataChangeListener("delete");
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DataChangeListener addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private static void fireDataChangeListener(String operation) {
        DataChangeEvent event = new DataChangeEvent(operation);
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(event);
        }
    }

    public static void init() {
        // Optional: seed dummy data
    }

    public static Customer getCustomerByCode(String code) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE code = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
