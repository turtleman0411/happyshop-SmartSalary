package com.example.demo.presentation.dto.request;

public class LoginForm {

    private String username;
    private String password;

    public LoginForm() {}

    public String getName() {
        return username;
    }
    public void setName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
