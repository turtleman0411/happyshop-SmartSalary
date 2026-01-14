package com.example.demo.application.User;

import org.springframework.stereotype.Service;
import com.example.demo.domain.model.User;
import com.example.demo.infrastructure.repository.UserRepository;
import com.example.demo.domain.value.Account;
import com.example.demo.domain.value.PasswordHasher;
import com.example.demo.domain.value.UserId;
import com.example.demo.application.exception.AuthenticationFailedException;
import com.example.demo.application.exception.AccountLockedException;

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
