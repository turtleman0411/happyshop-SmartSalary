package com.example.demo.presentation.web;


import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.demo.application.query.SelectPageQueryService;
import com.example.demo.application.query.TransactionPageQueryService;
import com.example.demo.application.query.ResultPageQueryService;
import com.example.demo.domain.value.UserId;
import com.example.demo.presentation.dto.request.LoginForm;
import com.example.demo.presentation.dto.request.RegisterForm;
import com.example.demo.presentation.dto.view.ResultPageView;
import com.example.demo.presentation.dto.view.SelectPageView;
import com.example.demo.presentation.dto.view.TransactionPageView;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/happyshop")
public class PageController {
    private final SelectPageQueryService selectPageQueryService;
    private final ResultPageQueryService resultPageQueryService;
    private final TransactionPageQueryService transactionPageQueryService;
    
    public PageController( 
        SelectPageQueryService selectPageQueryService,
        ResultPageQueryService resultPageQueryService,
        TransactionPageQueryService transactionPageQueryService
        
    ){
        this.selectPageQueryService = selectPageQueryService;
        this.resultPageQueryService = resultPageQueryService;
        this.transactionPageQueryService = transactionPageQueryService;
        
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

       
        ResultPageView view =
                resultPageQueryService.getResultPage(
                        userId,
                        targetMonth
                );

        model.addAttribute("view", view);
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
        model.addAttribute("error", "請重新進入系統");
        return "redirect:/happyshop/home";
    }

    TransactionPageView view =
            transactionPageQueryService.getTransactionPage(
                    userId,
                    month,
                    category
            );

    model.addAttribute("view", view);
    return "page/transaction-list";
}



}

