package com.example.SmartSpent.presentation.web;

import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SmartSpent.application.Transaction.TransactionFlow;
import com.example.SmartSpent.application.result.ResultPageFlow;
import com.example.SmartSpent.application.select.SelectFlow;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.infrastructure.component.MonthThemeResolver;
import com.example.SmartSpent.presentation.dto.request.LoginForm;
import com.example.SmartSpent.presentation.dto.request.RegisterForm;
import com.example.SmartSpent.presentation.dto.view.ResultPageView;
import com.example.SmartSpent.presentation.dto.view.SelectPageView;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/happyshop")
public class PageController {
    private final SelectFlow selectFlow;
    private final ResultPageFlow resultPageFlow;
    private final MonthThemeResolver monthThemeResolver;
    private final TransactionFlow transactionFlow;

    public PageController(
            ResultPageFlow resultPageFlow,
            MonthThemeResolver monthThemeResolver,
            TransactionFlow transactionFlow,
            SelectFlow selectFlow
    ) { 
        this.resultPageFlow = resultPageFlow;
        this.monthThemeResolver = monthThemeResolver;
        this.transactionFlow = transactionFlow;
        this.selectFlow = selectFlow;
    }



    @GetMapping("/home")
    public String home(
            @RequestParam(required = false) YearMonth month,
            HttpServletRequest request
    ) {
        UserId userId = (UserId)request.getAttribute("loginUserId");

        if (userId != null) {
            YearMonth targetMonth = (month != null) ? month : YearMonth.now();
            return "redirect:/happyshop/result?month=" + targetMonth;
        }
        return "page/home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("login", new LoginForm());
        return "page/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("register", new RegisterForm());
        return "page/register";
    }

    @GetMapping("/select")
    public String select(
            @RequestParam(required = false) YearMonth month,
            HttpServletRequest request,
            Model model
    ) {
        UserId userId = (UserId)request.getAttribute("loginUserId");

        YearMonth targetMonth = (month != null) ? month : YearMonth.now();

        SelectPageView view = selectFlow.getPageView(userId, targetMonth);
        String themeClass = monthThemeResolver.resolve(targetMonth);

        model.addAttribute("themeClass", themeClass);
        model.addAttribute("view", view);
        return "page/select";
    }

    @GetMapping("/result")
    public String result(
            @RequestParam(required = false) YearMonth month,
            HttpServletRequest request,
            Model model
    ) {
        UserId userId = (UserId)request.getAttribute("loginUserId");
        YearMonth targetMonth = (month != null) ? month : YearMonth.now();

        ResultPageView result = resultPageFlow.getResultPage(userId, targetMonth);
        String themeClass = monthThemeResolver.resolve(targetMonth);

        model.addAttribute("view", result);
        model.addAttribute("themeClass", themeClass);
        model.addAttribute("loginUserId", userId);
        return "page/result";
    }

   @GetMapping("/transactions")
    public String transactionPage(
            HttpServletRequest request,
            @RequestParam(required = false) YearMonth month,
            Model model
    ) {
        UserId userId = (UserId)request.getAttribute("loginUserId");

        YearMonth targetMonth = (month != null) ? month : YearMonth.now();

        TransactionPageView result =
                transactionFlow.getTransactionPage(userId, targetMonth);

        String themeClass = monthThemeResolver.resolve(targetMonth);

        model.addAttribute("themeClass", themeClass);
        model.addAttribute("view", result);
        return "page/transaction-list";
    }

}
