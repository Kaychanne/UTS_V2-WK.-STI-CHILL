package com.mycompany.mavenproject3;

public class User {
    private String idCustomer;

    private String username;


    public User(String idCustomer, String username) {
        this.idCustomer = idCustomer;
   
        this.username = username;
      
    }

    public String getIdCustomer() { return idCustomer; }
   
    public String getUsername() { return username; }
   

    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }
 
    public void setUsername(String username) { this.username = username; }
    
}
