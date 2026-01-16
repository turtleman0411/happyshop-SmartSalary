package com.example.demo.application.query;

import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.example.demo.presentation.dto.view.CategoryUsageState;
import com.example.demo.domain.model.BudgetMonth;
import com.example.demo.domain.model.CategoryType;
import com.example.demo.domain.value.UserId;
import com.example.demo.infrastructure.repository.BudgetMonthRepository;
import com.example.demo.infrastructure.repository.CategoryAllocationRepository;
import com.example.demo.infrastructure.repository.TransactionRepository;
import com.example.demo.infrastructure.repository.projection.CategoryAllocationRow;
import com.example.demo.infrastructure.repository.projection.CategorySpentRow;
import com.example.demo.presentation.dto.view.ResultPageView;

@Service
public class ResultPageQueryService {

    private final CategoryAllocationRepository allocationRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMonthRepository budgetMonthRepository;
    
    public ResultPageQueryService(
        CategoryAllocationRepository allocationRepository,
        TransactionRepository transactionRepository,
        BudgetMonthRepository budgetMonthRepository
) {
    this.allocationRepository = allocationRepository;
    this.transactionRepository = transactionRepository;
    this.budgetMonthRepository = budgetMonthRepository;
}
    public ResultPageView getResultPage(
            UserId userId,
            YearMonth month
    ) {

        BudgetMonth budgetMonth =
        budgetMonthRepository
                .findByUserIdAndMonth(userId, month)
                .orElse(null);

int income =
        (budgetMonth != null && budgetMonth.income() != null)
                ? budgetMonth.income().value()
                : 0; // 尚未建立 BudgetMonth 時的顯示值


        /* =========================
         * Month Model（全月帳務事實）
         * ========================= */

        int monthlySpentAmount =
                transactionRepository.sumMonthlySpent(
                        userId.value(),
                        month
                );

        int monthlyBudgetAmount = income;

        int monthlyRemainingAmount =
                Math.max(0, monthlyBudgetAmount - monthlySpentAmount);

        int usagePercent =
                monthlyBudgetAmount == 0
                        ? 0
                        : (monthlySpentAmount * 100 / monthlyBudgetAmount);

        /* =========================
         * Over Pool（只在超過薪水才啟動）
         * ========================= */

        int overLimitAmount = income * 20 / 100;
        int overSpentAmount =
                Math.max(0, monthlySpentAmount - monthlyBudgetAmount);

        int overRemainingAmount =
                Math.max(0, overLimitAmount - overSpentAmount);

        int overPoolUsagePercent =
                overLimitAmount == 0
                        ? 0
                        : (overSpentAmount * 100 / overLimitAmount);

        boolean hasOverSpent = overSpentAmount > 0;

        /* =========================
         * Allocation / Spent 資料
         * ========================= */

        List<CategoryAllocationRow> allocations =
                allocationRepository.findCategoryAllocations(
                        userId.value(),
                        month
                );

        Map<CategoryType, CategoryAllocationRow> allocationMap =
                allocations.stream()
                        .collect(Collectors.toMap(
                                CategoryAllocationRow::getCategory,
                                a -> a
                        ));

        List<CategorySpentRow> spentRows =
                transactionRepository.sumCategorySpent(
                        userId.value(),
                        month
                );

        Map<CategoryType, Integer> spentMap = new EnumMap<>(CategoryType.class);
        for (CategorySpentRow row : spentRows) {
            spentMap.put(row.getCategory(), row.getSpentAmount());
        }

        /* =================================================
         * ⭐ 正確模型：分類超額 → 平均平攤給未超額分類
         * ================================================= */

        // ① baseBudget（永遠固定）
        Map<CategoryType, Integer> baseBudgetMap = new EnumMap<>(CategoryType.class);
        for (CategoryType category : CategoryType.values()) {
            CategoryAllocationRow allocation = allocationMap.get(category);
            int percent = allocation != null ? allocation.getPercent() : 0;
            baseBudgetMap.put(category, income * percent / 100);
        }

        // ② 自身超額
        Map<CategoryType, Integer> selfOverMap = new EnumMap<>(CategoryType.class);
        int totalOver = 0;

        for (CategoryType category : CategoryType.values()) {
            int spent = spentMap.getOrDefault(category, 0);
            int baseBudget = baseBudgetMap.get(category);
            int selfOver = Math.max(0, spent - baseBudget);
            selfOverMap.put(category, selfOver);
            totalOver += selfOver;
        }

        // ③ 尚未超額的分類數
long normalCount =
        allocationMap.keySet().stream()
                .filter(category ->
                        selfOverMap.getOrDefault(category, 0) == 0
                )
                .count();



        // ④ 平均平攤
        int sharedPenalty =
                normalCount > 0
                        ? totalOver / (int) normalCount
                        : 0;

        /* =========================
         * Category Summary（最終 View）
         * ========================= */

List<ResultPageView.CategorySummaryView> categorySummaryList =
        List.of(CategoryType.values())
                .stream()
                .map(category -> {

                    CategoryAllocationRow allocation =
                            allocationMap.get(category);

                    boolean allocated = allocation != null;

                    int baseBudget =
                            baseBudgetMap.get(category);

                    int currentSpent =
                            spentMap.getOrDefault(category, 0);

                    boolean selfOverSpent =
                            selfOverMap.get(category) > 0;

                    // ⭐ 只有沒超額的分類才會被扣平攤
                    int effectiveBudget =
                            selfOverSpent
                                    ? baseBudget
                                    : Math.max(0, baseBudget - sharedPenalty);

                 int rawAvailable =
        Math.max(0, effectiveBudget - currentSpent);

// ⭐ 最終可用金額：不能超過全月剩餘
int availableAmount =
        Math.min(rawAvailable, monthlyRemainingAmount);

                    int categoryOverSpent =
                            Math.max(0, currentSpent - baseBudget);

                    int usagePercentCategory =
                            effectiveBudget > 0
                                    ? (currentSpent * 100 / effectiveBudget)
                                    : 0;

                    int displayUsagePercent =
                            Math.min(100, usagePercentCategory);

                    boolean penalizedByOthers =
                            !selfOverSpent && sharedPenalty > 0;

                    /* =========================
                     * ⭐ 狀態裁決（關鍵）
                     * ========================= */

                    CategoryUsageState state;

                    if (hasOverSpent && overRemainingAmount == 0) {
                        // 全月已爆 120%
                        state = CategoryUsageState.GLOBAL_OVERFLOW;

                    } else if (selfOverSpent) {
                        // 自己花超
                        state = CategoryUsageState.SELF_OVERSPENT;

                    } else if (!selfOverSpent && availableAmount == 0 && penalizedByOthers) {
                        // 沒花超，但被別人吃光
                        state = CategoryUsageState.NO_AVAILABLE_DUE_TO_POOL;

                    } else {
                        state = CategoryUsageState.NORMAL;
                    }

                    return new ResultPageView.CategorySummaryView(
                            category.name(),
                            category.displayName(),
                            allocated,
                            baseBudget,
                            effectiveBudget,
                            sharedPenalty,
                            currentSpent,
                            availableAmount,
                            categoryOverSpent,
                            usagePercentCategory,
                            displayUsagePercent,
                            penalizedByOthers,
                            selfOverSpent,
                            state   // ✅ 關鍵補上
                    );
                })
                .toList();


        /* =========================
         * Assemble View
         * ========================= */

        ResultPageView view = new ResultPageView();

        view.month = month;
        view.salary = income;

        view.monthlyBudgetAmount = monthlyBudgetAmount;
        view.monthlySpentAmount = monthlySpentAmount;
        view.monthlyRemainingAmount = monthlyRemainingAmount;
        view.usagePercent = usagePercent;

        view.reallocatableAmount = Math.max(0, income - totalOver);

        view.monthlyRuleNote =
                "分類預算為固定基準，僅當部分分類超額時，超額金額會平均分攤給尚未超額的分類";

        view.overLimitAmount = overLimitAmount;
        view.overSpentAmount = overSpentAmount;
        view.overRemainingAmount = overRemainingAmount;
        view.overPoolUsagePercent = overPoolUsagePercent;
        view.hasOverSpent = hasOverSpent;

        view.overPoolRuleNote =
                "超額池僅在總消費超過薪水時啟動，與分類平攤懲罰機制分離";

        view.categorySummaryList = categorySummaryList;

        return view;
    }
}
