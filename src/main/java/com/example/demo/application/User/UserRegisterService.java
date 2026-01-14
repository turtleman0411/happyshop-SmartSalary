package com.example.demo.application.User;

import org.springframework.stereotype.Service;

import com.example.demo.application.exception.AccountAlreadyExistsException;
import com.example.demo.domain.model.User;
import com.example.demo.domain.value.Account;
import com.example.demo.domain.value.Password;
import com.example.demo.domain.value.PasswordHasher;
import com.example.demo.domain.value.UserId;
import com.example.demo.infrastructure.repository.UserRepository;


@Service
 class UserRegisterService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    UserRegisterService(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }
    

     UserId register(String name, String rawPassword) {

        if (userRepository.existsByAccount_Value(name)) {
            throw new AccountAlreadyExistsException();
        }

        Account account = Account.of(name);

        Password password =
                Password.fromRaw(rawPassword, passwordHasher);

        User user = User.create(account, password);

        userRepository.save(user);
        return user.getId();
    }
}
