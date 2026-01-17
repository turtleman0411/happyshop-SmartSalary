package com.example.SmartSpent.domain.value;


public interface PasswordHasher {

    String hash(String raw);

    boolean matches(String raw, String hash);
}
