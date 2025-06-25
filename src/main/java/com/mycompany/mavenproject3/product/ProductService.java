package com.mycompany.mavenproject3.product;

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

    public static void init() {
        if (productList.isEmpty()) {
            productList.add(new Product(ProductService.getNextId(), "P001", "Americano", "Coffee", 18000, 10));
            productList.add(new Product(ProductService.getNextId(), "P002", "Pandan Latte", "Coffee", 15000, 8));
        }
    }

    public static List<Product> getAllProducts() {
        return productList;
    }

    public static int getIndexById(int id) {
        int low = 0, high = productList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (productList.get(mid).getId() == id) {
                return mid;
            } else if (productList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    public static Product getProductByIndex(int index) {
        return productList.get(index);
    }

    public static Product getProductById(int id) {
        int index = getIndexById(id);
        if (index != -1) {
            return getProductByIndex(index);
        }
        return null;
    }

    public static Product addProduct(Product product) {
        productList.add(product);
        fireDataChangeListener("add");
        return product;
    }

    public static Product updateProduct(Product Product) {
        return updateProductById(Product.getId(), Product);
    }

    public static Product updateProductById(int id, Product Product) {
        int index = getIndexById(id);
        if (index != -1) {
            productList.set(index, Product);
            fireDataChangeListener("update");
            return Product;
        }
        return null;
    }

    public static boolean deleteProductByIndex(int index) {
        productList.remove(index);
        fireDataChangeListener("delete");
        return true;
    }

    public static boolean deleteProductById(int id) {
        int index = getIndexById(id);
        if (index != -1) {
            return deleteProductByIndex(index);
        }
        return false;
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
}