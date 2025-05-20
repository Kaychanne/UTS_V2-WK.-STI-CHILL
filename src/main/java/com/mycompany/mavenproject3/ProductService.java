package com.mycompany.mavenproject3;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static List<Product> productList = new ArrayList<>();

    public static List<Product> getAllProducts() {
        return productList;
    }

    public static void addProduct(Product product) {
        productList.add(product);
    }

    public static void updateProduct(Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            Product current = productList.get(i);
            if (current.getCode().equals(updatedProduct.getCode())) {
                productList.set(i, updatedProduct);
                break;
            }
        }
    }
    
    public static void deleteProductByCode(String code) {
        productList.removeIf(p -> p.getCode().equals(code));
    }
}
