package com.example.demo.presentation.web;


import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.application.BudgetMonth.BudgetMonthResetFlow;
import com.example.demo.application.Transaction.TransactionFlow;
import com.example.demo.application.User.UserFlow;
import com.example.demo.domain.value.TransactionId;
import com.example.demo.domain.value.UserId;
import com.example.demo.presentation.dto.request.AddTransactionRequest;
import com.example.demo.presentation.dto.request.BudgetAllocationRequest;
import com.example.demo.presentation.dto.request.LoginForm;
import com.example.demo.presentation.dto.request.RegisterForm;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommandController {
    private final UserFlow userFlow;
    private final TransactionFlow transactionFlow;
    private final BudgetMonthResetFlow budgetMonthResetFlow;
    public CommandController(UserFlow userFlow,TransactionFlow transactionFlow,BudgetMonthResetFlow budgetMonthResetFlow){
        this.userFlow = userFlow;
        this.transactionFlow = transactionFlow;
        this.budgetMonthResetFlow = budgetMonthResetFlow;
    }

    @PostMapping("/register")
    public String register(
        @ModelAttribute RegisterForm req,
        RedirectAttributes redirect
    ){
        userFlow.register(req.getName(), req.getPassword());
        redirect.addFlashAttribute("message","註冊成功");
        return "redirect:/happyshop/login";
    }

    @PostMapping("/login")
    public String login(
        @ModelAttribute LoginForm form,
        HttpSession session
    ) {
    UserId userId =
            userFlow.login(form.getName(), form.getPassword());

    session.setAttribute("loginUserId", userId);

    return "redirect:/happyshop/result";
    }

    @PostMapping("/select")
    public String submitSelect(
            @SessionAttribute("loginUserId") UserId userId,
            @ModelAttribute BudgetAllocationRequest request
    ) {
        userFlow.configureMonthlyBudget(
                userId,
                request.month(),
                request.toCategoryPercentMap()
        );

            System.out.println("🔥 收到 JSON");
            System.out.println("month = " + request.month());
            System.out.println("percents = " + request.percents());
            System.out.println("raw request = " + request);

        // ⭐ 成功後直接回首頁
        return "redirect:/happyshop/result?month=" + request.month();

    }

    @PostMapping("/transaction/add")
    public String addTransaction(
            @RequestParam YearMonth month,
            @SessionAttribute("loginUserId") UserId userId,
            @ModelAttribute AddTransactionRequest request
    ) {
        transactionFlow.addTransaction(
                userId,
                month,
                request
        );

        return "redirect:/happyshop/result?month=" + month;
    }

    @PostMapping("happyshop/month/reset")
    public String resetMonth(
        @SessionAttribute(value = "loginUserId", required = false) UserId userId,
        HttpSession session,
        @RequestParam String month
    ) {

        if (userId == null) {
            userId = UserId.newId();
            session.setAttribute("loginUserId", userId);
        }
        budgetMonthResetFlow.reset(userId, month);
        return "redirect:/happyshop/select?month=" + month;
    }

    @PostMapping("/transaction/delete")
    public String delete(
            @RequestParam Long transactionId,
            @RequestParam YearMonth month,
            @SessionAttribute("loginUserId") UserId userId
    ) {
        System.out.println("🧨 delete txId = " + transactionId);
        System.out.println("🧨 delete month = " + month);
        System.out.println("🧨 delete userId = " + userId);
        transactionFlow.deleteTransaction(
                userId,
                month,
                TransactionId.of(transactionId)
        );
        return "redirect:/happyshop/transactions?month=" + month;
    }
    
    // CommandController.java
    @PostMapping("/income/update")
    public String updateIncome(
            @RequestParam YearMonth month,
            @RequestParam int income,
            @SessionAttribute("loginUserId") UserId userId
    ) {
        userFlow.updateIncome(userId, month, income);
        return "redirect:/happyshop/result?month=" + month;
    }

    @PostMapping("/user/logout")
    public String logout(HttpSession session, HttpServletResponse response) {

        // 1) 清 session
        session.invalidate();

        // 2) 清 remember-me cookie（名稱要跟你 interceptor 用的一樣）
        Cookie cookie = new Cookie("REMEMBER_ME", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return "redirect:/happyshop/home";
    }


}
