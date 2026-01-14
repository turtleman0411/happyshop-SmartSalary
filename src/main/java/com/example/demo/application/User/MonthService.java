package com.example.demo.application.User;

import java.time.YearMonth;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.BudgetMonth;
import com.example.demo.domain.model.CategoryType;
import com.example.demo.domain.value.BudgetMonthId;
import com.example.demo.domain.value.IncomeAmount;
import com.example.demo.domain.value.UserId;
import com.example.demo.infrastructure.repository.BudgetMonthRepository;


@Service
@Transactional
 class MonthService {

    private final BudgetMonthRepository repository;

     MonthService(BudgetMonthRepository repository) {
        this.repository = repository;
    }

    /**
     * 設定（或更新）某使用者某月份的預算配置
     *
     * - 對齊 LoginService
     * - 對齊 UserRegisterService
     * - 回傳識別型 VO（BudgetMonthId）
     */
         BudgetMonthId configureMonthlyBudget(
                UserId userId,
                YearMonth month,
                Map<CategoryType, Integer> percents
        ) {
        // 1️⃣ 找或建立 Aggregate Root（唯一定位：user + month）
        BudgetMonth budgetMonth =
                repository
                        .findByUserIdAndMonth(userId, month)
                        .orElseGet(() ->
                                BudgetMonth.create(userId, month,IncomeAmount.of(35_000))
                        );

        // 2️⃣ 交給 Domain（唯一業務入口）
        budgetMonth.configureAllocations(percents);

        // 3️⃣ 儲存 Aggregate Root
        repository.save(budgetMonth);

        // 4️⃣ 回傳識別（一致策略）
        return budgetMonth.getId();
        }

}
