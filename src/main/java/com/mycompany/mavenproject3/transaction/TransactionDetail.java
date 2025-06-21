package com.mycompany.mavenproject3.transaction;

public class TransactionDetail {
    private int id;
    private int productId;
    private int qty;
    private double total;

    public TransactionDetail(int id, int productId, int qty, double total) {
        this.id = id;
        this.productId = productId;
        this.qty = qty;
        this.total = total;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQty() { return qty; }
    public double getTotal() { return total; }

    public void setId(int id) { this.id = id; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQty(int qty) { this.qty = qty; }
    public void setTotal(double total) { this.total = total; }
}
