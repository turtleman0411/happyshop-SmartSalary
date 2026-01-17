package com.example.demo.presentation.web;


import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.demo.application.Transaction.TransactionPageFlow;
import com.example.demo.application.Transaction.TransactionPageFlowResult;
import com.example.demo.application.query.SelectPageQueryService;
import com.example.demo.application.result.ResultPageFlow;
import com.example.demo.application.result.ResultPageFlowResult;
import com.example.demo.domain.value.UserId;
import com.example.demo.presentation.dto.request.LoginForm;
import com.example.demo.presentation.dto.request.RegisterForm;
import com.example.demo.presentation.dto.view.SelectPageView;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/happyshop")
public class PageController {
    private final SelectPageQueryService selectPageQueryService;
    private final TransactionPageFlow transactionPageFlow;
    private final ResultPageFlow resultPageFlow;
    public PageController(
    SelectPageQueryService selectPageQueryService,
    ResultPageFlow resultPageFlow,
    TransactionPageFlow transactionPageFlow
){
    this.selectPageQueryService = selectPageQueryService;
    this.resultPageFlow = resultPageFlow;
    this.transactionPageFlow = transactionPageFlow;
}

 

    @GetMapping("/home")
    public String home() {
        return "page/home";
    }
    @GetMapping("/login")
    public String login(Model model){
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
            @SessionAttribute(value = "loginUserId",required = false) UserId userId,
            HttpSession session,
            Model model
    ) {
        if (userId == null) {
        // 臨時 demo / debug
        userId = UserId.newId();
        session.setAttribute("loginUserId", userId);
    }
        YearMonth targetMonth =
            (month != null) ? month : YearMonth.now();

        
        SelectPageView view =
                selectPageQueryService.getSelectPage(userId, targetMonth);

        
        model.addAttribute("view", view);

        return "page/select";
    }

@GetMapping("/result")
public String result(
        @RequestParam(required = false) YearMonth month,
        @SessionAttribute(value = "loginUserId", required = false) UserId userId,
        HttpSession session,
        Model model
) {
    if (userId == null) {
        userId = UserId.newId(); // demo / debug
        session.setAttribute("loginUserId", userId);
    }

    YearMonth targetMonth =
            (month != null) ? month : YearMonth.now();

    ResultPageFlowResult result =
            resultPageFlow.getResultPage(userId, targetMonth);

    model.addAttribute("view", result.view());
    model.addAttribute("themeClass", result.themeClass());

    return "page/result";
}


@GetMapping("/transactions")
public String transactionPage(
        @SessionAttribute(value = "loginUserId", required = false) UserId userId,
        @RequestParam YearMonth month,
        @RequestParam(required = false) String category,
        Model model
) {
    if (userId == null) {
        return "redirect:/happyshop/home";
    }

    TransactionPageFlowResult result =
            transactionPageFlow.getTransactionPage(
                    userId,
                    month,
                    category
            );

    model.addAttribute("view", result.view());
    model.addAttribute("themeClass", result.themeClass());

    return "page/transaction-list";
}


}

