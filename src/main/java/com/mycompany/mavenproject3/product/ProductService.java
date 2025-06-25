package com.mycompany.mavenproject3.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class ProductService {
    private static final List<Product> productList = new ArrayList<>();
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static final Map<Long, Product> productMap = new HashMap<>();
    private static int currentId = 0;

    public static int getCurrentId() {
        return currentId;
    }

    public static int getNextId() {
        return ++currentId;
    }

    public static void init() {
        if (productList.isEmpty()) {
            productList.add(new Product(ProductService.getNextId(), "P001", "Americano", "Coffee", 18000, 10));
            productList.add(new Product(ProductService.getNextId(), "P002", "Pandan Latte", "Coffee", 15000, 8));
        }
    }

    public static List<Product> getAllProducts() {
        return productList;
    }

    public static Product getProductByIndex(int index) {
        return productList.get(index);
    }

    public static Product getProductById(int id) {
        int low = 0, high = productList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (productList.get(mid).getId() == id) {
                return productList.get(mid);
            } else if (productList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public static void addProduct(Product product) {
        productList.add(product);
        fireDataChangeListener("add");
    }

    public static void updateProduct(Product Product) {
        for (int i = 0; i < productList.size(); i++) {
            Product current = productList.get(i);
            if (current.getCode().equals(Product.getCode())) {
                productList.set(i, Product);
                fireDataChangeListener("update");
                break;
            }
        }
    }

    public static void deleteProductByIndex(int index) {
        productList.remove(index);
        fireDataChangeListener("delete");
    }

    public static void deleteProductByCode(String code) {
        productList.removeIf(p -> p.getCode().equals(code));
        fireDataChangeListener("delete");
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

    public static List<Product> getAll() {
        return new ArrayList<>(productMap.values());
    }

    public static Product get(long id) {
        return productMap.get(id);
    }

    public static Product add(Product product) {
        productMap.put((long) product.getId(), product); // Gunakan ID dari data lokal
        fireDataChangeListener("add-");
        return product;
    }

    public static Product update(long id, Product Product) {
        Product.setId((int) id);
        productMap.put(id, Product);
        fireDataChangeListener("update-");
        return Product;
    }

    public static boolean delete(long id) {
        Product removed = productMap.remove(id);
        if (removed != null) {
            fireDataChangeListener("delete-");
            return true;
        }
        return false;
    }
}