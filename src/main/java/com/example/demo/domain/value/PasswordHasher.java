package com.example.demo.domain.value;


public interface PasswordHasher {

    String hash(String raw);

    boolean matches(String raw, String hash);
}
