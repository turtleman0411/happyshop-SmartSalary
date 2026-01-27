package com.example.SmartSpent.presentation.web;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SmartSpent.application.Transaction.TransactionFlow;
import com.example.SmartSpent.application.User.UserFlow;
import com.example.SmartSpent.application.result.ResultPageFlow;
import com.example.SmartSpent.application.security.RememberMeService;
import com.example.SmartSpent.domain.value.TransactionId;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.request.AddTransactionRequest;
import com.example.SmartSpent.presentation.dto.request.BudgetAllocationRequest;
import com.example.SmartSpent.presentation.dto.request.LoginForm;
import com.example.SmartSpent.presentation.dto.request.RegisterForm;
import com.example.SmartSpent.presentation.dto.request.UpdateTransactionRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/happyshop")
public class CommandController {

    private final UserFlow userFlow;
    private final ResultPageFlow resultPageFlow;
    private final TransactionFlow transactionFlow;
    private final RememberMeService rememberMeService;

    public CommandController(
            UserFlow userFlow,
            TransactionFlow transactionFlow,
            RememberMeService rememberMeService,
            ResultPageFlow resultPageFlow
    ) {
        this.userFlow = userFlow;
        this.transactionFlow = transactionFlow;
        this.rememberMeService = rememberMeService;
        this.resultPageFlow = resultPageFlow;
    }

    private UserId resolveLoginUserId(HttpServletRequest request) {

    UserId userId = (UserId) request.getAttribute("loginUserId");
    if (userId != null) return userId;

    Object sessionUser = request.getSession().getAttribute("loginUserId");
    if (sessionUser instanceof UserId) {
        return (UserId) sessionUser;
    }

    return null;
}

    // =========================
    // Income
    // =========================
    @PostMapping("/income/update")
    public String updateIncome(
            @RequestParam YearMonth month,
            @RequestParam int income,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        resultPageFlow.updateIncome(userId, month, income);

        System.out.println("🔥 HIT CommandController.updateIncome month=" + month + " income=" + income);

        return "redirect:/happyshop/result?month=" + month;
    }



    // =========================
    // Auth
    // =========================

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterForm form,
            RedirectAttributes redirect
    ) {
        userFlow.register(form.getName(), form.getPassword());
        redirect.addFlashAttribute("message", "註冊成功，請登入");
        return "redirect:/happyshop/login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginForm form,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirect
    ) {
        UserId userId = userFlow.login(form.username(), form.password());

        // ✅ 勾選才持久化，不勾就是 Session Cookie（關瀏覽器失效）
        rememberMeService.issue(userId, request, response, form.rememberMe());

        return "redirect:/happyshop/result";
    }

    @PostMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        rememberMeService.clear(request, response);
        return "redirect:/happyshop/home";
    }

    // =========================
    // Transactions
    // =========================

    @PostMapping("/transaction/add")
    public String addTransaction(
            @RequestParam YearMonth month,
            @ModelAttribute AddTransactionRequest req,
            HttpServletRequest request
    ) {
        System.out.println("🔥 HIT CommandController.addTransaction");
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        transactionFlow.addTransaction(userId, month, req);
        return "redirect:/happyshop/result?month=" + month;
    }

    @PostMapping("/transaction/update")
    public String updateTransaction(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @ModelAttribute UpdateTransactionRequest form,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";
        
        transactionFlow.updateTransaction(
                userId,
                month,
                form.transactionId(),
                form.amount(),
                form.note(),
                form.image()
        );

        return "redirect:/happyshop/transactions?month=" + month;
    }


    @PostMapping("/transaction/delete")
    public String deleteTransaction(
            @RequestParam TransactionId transactionId,
            @RequestParam YearMonth month,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        transactionFlow.deleteTransaction(userId, month, transactionId);
        return "redirect:/happyshop/transactions?month=" + month;
    }


    // =========================
    // Month reset
    // =========================

    @PostMapping("/month/reset")
    public String resetMonth(
            @RequestParam String month,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        resultPageFlow.reset(userId, month);
        return "redirect:/happyshop/select?month=" + month;
    }


@PostMapping("/select")
public String submitSelect(
        @ModelAttribute BudgetAllocationRequest form,
        HttpServletRequest request
) {
    UserId userId = resolveLoginUserId(request);
    if (userId == null) return "redirect:/happyshop/login";

    resultPageFlow.configureMonthlyBudget(
            userId,
            form.month(),
            form.toCategoryPercentMap()
    );

    return "redirect:/happyshop/result?month=" + form.month();
}
}
