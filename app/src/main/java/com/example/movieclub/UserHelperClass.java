package com.example.movieclub;

public class UserHelperClass {
   String phone;
    String name, username, email, password;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String username,  String phone,String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;

        this.password = password;
        this.phone = phone;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
