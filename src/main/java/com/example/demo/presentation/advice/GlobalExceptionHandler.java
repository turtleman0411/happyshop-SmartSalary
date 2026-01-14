package com.example.demo.presentation.advice;

import com.example.demo.presentation.dto.request.LoginForm;
import com.example.demo.presentation.dto.request.RegisterForm;
import com.example.demo.presentation.dto.response.ErrorResponse;


import jakarta.servlet.http.HttpServletRequest;
import com.example.demo.application.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;



@ControllerAdvice
public class GlobalExceptionHandler {

    /* ===== VO 驗證失敗（格式錯誤） ===== */
        @ExceptionHandler(IllegalArgumentException.class)
        public Object handleIllegalArgument(
                IllegalArgumentException ex,
                HttpServletRequest request,
                Model model
        ) {
        // 👉 API（Accept: application/json）
        if (request.getHeader("Accept") != null
                && request.getHeader("Accept").contains("application/json")) {

                ErrorResponse error = new ErrorResponse(
                        "INVALID_INPUT",
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error);
        }

        // 👉 MVC（回頁面）
        model.addAttribute("error", ex.getMessage());
        return "page/home";
        }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ModelAndView handleAccountAlreadyExists(AccountAlreadyExistsException ex) {

        ModelAndView mv = new ModelAndView("page/register"); // 你的 register.html 路徑
        mv.addObject("error", "帳號已存在");
        mv.addObject("register", new RegisterForm()); // ✅ th:object="${register}" 需要
        return mv;
    }



        @ExceptionHandler(PasswordInvalidException.class)
        public Object PasswordInvalidException(
                Model model,
                PasswordInvalidException ex
        ){
                model.addAttribute("error", ex);
                model.addAttribute("register", new RegisterForm());
                return "page/register";
        }



    /* ===== DB 唯一性衝突（帳號已存在） ===== */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public Object handleDuplicateKey(
                DataIntegrityViolationException ex,
                HttpServletRequest request,
                Model model
        ) {
        // 👉 API
        if (request.getHeader("Accept") != null
                && request.getHeader("Accept").contains("application/json")) {

                ErrorResponse error = new ErrorResponse(
                        "ACCOUNT_ALREADY_EXISTS",
                        "帳號已存在",
                        HttpStatus.CONFLICT.value()
                );

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(error);
        }

    // 👉 MVC
    model.addAttribute("error", "帳號已存在");
    return "page/register"; // 或你原本的頁面
}


    /* ===== 未預期錯誤（保底） ===== */
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleUnexpected(
//             Exception ex
//     ) {
//         ErrorResponse error = new ErrorResponse(
//                 "INTERNAL_ERROR",
//                 "系統發生錯誤，請稍後再試",
//                 HttpStatus.INTERNAL_SERVER_ERROR.value()
//         );

//         return ResponseEntity
//                 .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(error);
//     }


    @ExceptionHandler(AuthenticationFailedException.class)
    public String handleAuthFailed(
            AuthenticationFailedException ex,
            Model model
    ) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("remainAttempts", ex.getRemainingAttempts());
        model.addAttribute("locked", false);
        model.addAttribute("login", new LoginForm());
        return "page/login";
    }

    @ExceptionHandler(AccountLockedException.class)
    public String handleLocked(
            AccountLockedException ex,
            Model model
    ) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("locked", true);
        model.addAttribute("login", new LoginForm());
        return "page/login";
    }
}
