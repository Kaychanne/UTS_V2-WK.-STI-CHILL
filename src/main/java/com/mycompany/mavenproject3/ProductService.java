package com.mycompany.mavenproject3;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class ProductService {
    private static final List<Product> productList = new ArrayList<>();
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static int currentId = 0;

    public static int getCurrentId() {
        return currentId;
    }

    public static int getNextId() {
        return ++currentId;
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

    public static void updateProduct(Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            Product current = productList.get(i);
            if (current.getCode().equals(updatedProduct.getCode())) {
                productList.set(i, updatedProduct);
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

    public static void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
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
}
