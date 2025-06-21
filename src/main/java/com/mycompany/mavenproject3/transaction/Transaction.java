package com.mycompany.mavenproject3.transaction;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private String code;
    private String cashier;
    private String customer;
    private LocalDateTime dateTime;
    private double total;
    private TransactionDetailService detailService;

    public Transaction(int id, String code, String cashier, String customer, LocalDateTime dateTime, double total, TransactionDetailService detailService) {
        this.id = id;
        this.code = code;
        this.cashier = cashier;
        this.customer = customer;
        this.dateTime = dateTime;
        this.total = total;
        this.detailService = detailService;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getCashier() { return cashier; }
    public String getCustomer() { return customer; }
    public LocalDateTime getDateTime() { return dateTime; }
    public double getTotal() { return total; }
    public TransactionDetailService getDetailService() { return detailService; }

    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setCashier(String cashier) { this.cashier = cashier; }
    public void setCustomer(String customer) { this.customer = customer; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setTotal(double total) { this.total = total; }
    public void setDetailService(TransactionDetailService detailService) { this.detailService = detailService; }
}
