package com.example.SmartSpent.presentation.web;


import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import com.example.SmartSpent.application.Transaction.TransactionPageFlow;
import com.example.SmartSpent.application.common.MonthThemeResolver;
import com.example.SmartSpent.application.query.SelectPageQueryService;
import com.example.SmartSpent.application.result.ResultPageFlow;
import com.example.SmartSpent.domain.value.UserId;
import com.example.SmartSpent.presentation.dto.request.LoginForm;
import com.example.SmartSpent.presentation.dto.request.RegisterForm;
import com.example.SmartSpent.presentation.dto.view.ResultPageView;
import com.example.SmartSpent.presentation.dto.view.SelectPageView;
import com.example.SmartSpent.presentation.dto.view.TransactionPageView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/happyshop")
public class PageController {
    private final SelectPageQueryService selectPageQueryService;
    private final TransactionPageFlow transactionPageFlow;
    private final ResultPageFlow resultPageFlow;
    private final MonthThemeResolver monthThemeResolver;


    public PageController(
    SelectPageQueryService selectPageQueryService,
    ResultPageFlow resultPageFlow,
    TransactionPageFlow transactionPageFlow,
    MonthThemeResolver monthThemeResolver
){
    this.selectPageQueryService = selectPageQueryService;
    this.resultPageFlow = resultPageFlow;
    this.transactionPageFlow = transactionPageFlow;
    this.monthThemeResolver = monthThemeResolver;
}

 

    @GetMapping("/home")
    public String home(
            @RequestParam(required = false) YearMonth month,
            HttpServletRequest request
    ) {
        UserId userId = (UserId) request.getAttribute("loginUserId");

        if (userId != null) {
            YearMonth targetMonth = (month != null) ? month : YearMonth.now();
            return "redirect:/happyshop/result?month=" + targetMonth;
        }

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

        YearMonth targetMonth =
            (month != null) ? month : YearMonth.now();

        
        SelectPageView view =
                selectPageQueryService.getSelectPage(userId, targetMonth);
        String themeClass = monthThemeResolver.resolve(targetMonth);
        model.addAttribute("themeClass", themeClass);
        model.addAttribute("view", view);

        return "page/select";
    }

@GetMapping("/result")
public String result(
        @RequestParam(required = false) YearMonth month,
        HttpServletRequest request,
        HttpSession session,
        Model model
) {
    // 🔑 只從 request 取登入者（Interceptor 已處理 session / cookie）
    UserId userId = (UserId) request.getAttribute("loginUserId");

    // 🚧 登入邊界
    if (userId == null) {
        return "redirect:/happyshop/home";
    }

    YearMonth targetMonth =
            (month != null) ? month : YearMonth.now();

    ResultPageView result =
            resultPageFlow.getResultPage(userId, targetMonth);
    String themeClass = monthThemeResolver.resolve(targetMonth);
    model.addAttribute("view", result);
    model.addAttribute("themeClass", themeClass);
    model.addAttribute("loginUserId", userId);
    return "page/result";
}



@GetMapping("/transactions")
public String transactionPage(
        HttpServletRequest request,
        @RequestParam YearMonth month,
        @RequestParam(required = false) String category,
        Model model
) {
    UserId userId = (UserId) request.getAttribute("loginUserId");
    YearMonth targetMonth =
            (month != null) ? month : YearMonth.now();
    if (userId == null) {
        return "redirect:/happyshop/home?month=" + month;
    }

    TransactionPageView result =
            transactionPageFlow.getTransactionPage(
                    userId,
                    month,
                    category
            );
     String themeClass = monthThemeResolver.resolve(targetMonth);
        model.addAttribute("themeClass", themeClass);
    model.addAttribute("view", result);
    
        
    return "page/transaction-list";
}


}

