package com.mycompany.mavenproject3;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class ProductService {
    private static List<Product> productList = new ArrayList<>();
    private static List<DataChangeListener> listeners = new ArrayList<>();

    public static List<Product> getAllProducts() {
        return productList;
    }

    public static Product getProductByIndex(int index) {
        return productList.get(index);
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
