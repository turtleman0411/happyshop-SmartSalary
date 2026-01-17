package com.example.SmartSpent.application.User;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.exception.AccountAlreadyExistsException;
import com.example.SmartSpent.domain.model.User;
import com.example.SmartSpent.domain.value.Account;
import com.example.SmartSpent.domain.value.Password;
import com.example.SmartSpent.domain.value.PasswordHasher;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.UserRepository;


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
