package com.example.demo.presentation.dto.request;

public class RegisterForm {

    private String name;
    private String password;
   
    // private Integer salary;

    public RegisterForm() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
   
    // public Integer getSalary() {
    //     return salary;
    // }

    // public void setSalary(Integer salary) {
    //     this.salary = salary;
    // }
}
