package com.example.SmartSpent.application.User;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.application.exception.AccountLockedException;
import com.example.SmartSpent.application.exception.AuthenticationFailedException;
import com.example.SmartSpent.domain.model.User;
import com.example.SmartSpent.domain.value.Account;
import com.example.SmartSpent.domain.value.PasswordHasher;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.repository.UserRepository;

@Service

    class LoginService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordEncoder;

    LoginService(
            UserRepository userRepository,
            PasswordHasher passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    UserId login(String rawAccount, String rawPassword) {

        
        Account account = Account.of(rawAccount);

        User user = userRepository.findByAccount(account)
                .orElseThrow(() ->
                        new AuthenticationFailedException("帳號錯誤")
                );

        if (user.isLocked()) {
            throw new AccountLockedException("帳號已鎖定");
        }

        if (!user.matchesPassword(rawPassword, passwordEncoder)) {

            user.recordLoginFailure();
            userRepository.save(user);
            if (user.isLocked()) {
                throw new AccountLockedException("帳號已鎖定");
            }

            throw new AuthenticationFailedException(
                    "密碼錯誤",
                    user.remainAttempts()
            );
        }

        user.resetLoginFailure();
        userRepository.save(user);
        
        return user.getId();
    }
}
