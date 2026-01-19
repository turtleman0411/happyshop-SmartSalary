package com.example.SmartSpent.presentation.web;

import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SmartSpent.application.BudgetMonth.BudgetMonthResetFlow;
import com.example.SmartSpent.application.Transaction.TransactionFlow;
import com.example.SmartSpent.application.User.UserFlow;
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
    private final TransactionFlow transactionFlow;
    private final BudgetMonthResetFlow budgetMonthResetFlow;
    private final RememberMeService rememberMeService;

    public CommandController(
            UserFlow userFlow,
            TransactionFlow transactionFlow,
            BudgetMonthResetFlow budgetMonthResetFlow,
            RememberMeService rememberMeService
    ) {
        this.userFlow = userFlow;
        this.transactionFlow = transactionFlow;
        this.budgetMonthResetFlow = budgetMonthResetFlow;
        this.rememberMeService = rememberMeService;
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
        UserId userId = userFlow.login(form.getUsername(), form.getPassword());

        // ✅ 勾選才持久化，不勾就是 Session Cookie（關瀏覽器失效）
        rememberMeService.issue(userId, request, response, form.isRememberMe());

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
            @RequestParam YearMonth month,
            @ModelAttribute UpdateTransactionRequest form,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        transactionFlow.updateTransaction(
                userId, month,
                form.transactionId(),
                form.amount(),
                form.note(),
                form.image()
        );

        return "redirect:/happyshop/transactions?month=" + month;
    }

    @PostMapping("/transaction/delete")
    public String deleteTransaction(
            @RequestParam Long transactionId,
            @RequestParam YearMonth month,
            HttpServletRequest request
    ) {
        UserId userId = resolveLoginUserId(request);
        if (userId == null) return "redirect:/happyshop/login";

        transactionFlow.deleteTransaction(userId, month, TransactionId.of(transactionId));
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

        budgetMonthResetFlow.reset(userId, month);
        return "redirect:/happyshop/select?month=" + month;
    }


@PostMapping("/select")
public String submitSelect(
        @ModelAttribute BudgetAllocationRequest form,
        HttpServletRequest request
) {
    UserId userId = resolveLoginUserId(request);
    if (userId == null) return "redirect:/happyshop/login";

    userFlow.configureMonthlyBudget(
            userId,
            form.month(),
            form.toCategoryPercentMap()
    );

    return "redirect:/happyshop/result?month=" + form.month();
}
}
