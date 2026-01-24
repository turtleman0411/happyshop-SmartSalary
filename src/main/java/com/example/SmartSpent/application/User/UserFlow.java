package com.example.SmartSpent.application.User;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.domain.value.UserId;

@Service
public class UserFlow {

    private final UserRegisterService registerService;
    private final LoginService loginService;

    public UserFlow(
            UserRegisterService registerService,
            LoginService loginService
    ) {
        this.registerService = registerService;
        this.loginService = loginService;
    }

    /* =========================
     * 對 Controller 的入口
     * ========================= */

    public UserId register(String rawAccount, String password) {
        return registerService.register(rawAccount, password);
    }

    /**
     * 帳密登入（純登入）
     */
    public UserId login(String account, String password) {
        return loginService.login(account, password);
    }
}
