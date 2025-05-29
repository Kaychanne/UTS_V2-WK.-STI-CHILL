package com.mycompany.mavenproject3;

public class User {
    private String idCustomer;
    private String orderId;
    private String username;
    private String email;
    private String password;

    public User(String idCustomer, String orderId, String username, String email, String password) {
        this.idCustomer = idCustomer;
        this.orderId = orderId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getIdCustomer() { return idCustomer; }
    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
