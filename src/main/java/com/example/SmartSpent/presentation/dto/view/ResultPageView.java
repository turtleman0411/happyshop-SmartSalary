package com.example.SmartSpent.presentation.dto.view;

import java.time.YearMonth;
import java.util.List;

public class ResultPageView {

    // =========================
    // 基本識別
    // =========================
    public YearMonth month;
    public int salary;

    // =========================
    // Month（全月帳務事實）
    // =========================
    public int monthlyBudgetAmount;
    public int monthlySpentAmount;
    public int monthlyRemainingAmount;
    public int usagePercent;

    // 重置分配時使用（非畫面總和）
    public int reallocatableAmount;
    public String monthlyRuleNote;

    // =========================
    // Over Pool（全月限制）
    // =========================
    public int overLimitAmount;
    public int overSpentAmount;
    public int overRemainingAmount;
    public int overPoolUsagePercent;
    public boolean hasOverSpent;
    public String overPoolRuleNote;

    // =========================
    // Category（治理顯示）
    // =========================
    public List<CategorySummaryView> categorySummaryList;

    public boolean hasAllocation() {
        return categorySummaryList != null &&
               categorySummaryList.stream().anyMatch(CategorySummaryView::isAllocated);
    }

    // =================================================
    // CategorySummaryView（小物件，唯一顯示真相）
    // =================================================
    public static class CategorySummaryView {

        // 識別
        private final String categoryName;
        private final String categoryDisplayName;

        // 狀態（唯一真相）
        private final CategoryUsageState state;

        // 是否有分配
        private final boolean allocated;

        // 結構預算（固定）
        private final int baseCategoryBudgetAmount;

        // 治理後預算（顯示用）
        private final int effectiveCategoryBudgetAmount;

        // 被扣減金額
        private final int penaltyAmount;

        // 消費
        private final int categoryCurrentSpent;

        // 即時結果
        private final int availableAmount;
        private final int categoryOverSpentAmount;

        // 百分比
        private final int categoryUsagePercent;
        private final int displayUsagePercent;

        // 狀態標記
        private final boolean penalizedByOthers;
        private final boolean selfOverSpent;

        public CategorySummaryView(
                String categoryName,
                String categoryDisplayName,
                boolean allocated,
                int baseCategoryBudgetAmount,
                int effectiveCategoryBudgetAmount,
                int penaltyAmount,
                int categoryCurrentSpent,
                int availableAmount,
                int categoryOverSpentAmount,
                int categoryUsagePercent,
                int displayUsagePercent,
                boolean penalizedByOthers,
                boolean selfOverSpent,
                CategoryUsageState state
        ) {
            this.categoryName = categoryName;
            this.categoryDisplayName = categoryDisplayName;
            this.allocated = allocated;
            this.baseCategoryBudgetAmount = baseCategoryBudgetAmount;
            this.effectiveCategoryBudgetAmount = effectiveCategoryBudgetAmount;
            this.penaltyAmount = penaltyAmount;
            this.categoryCurrentSpent = categoryCurrentSpent;
            this.availableAmount = availableAmount;
            this.categoryOverSpentAmount = categoryOverSpentAmount;
            this.categoryUsagePercent = categoryUsagePercent;
            this.displayUsagePercent = displayUsagePercent;
            this.penalizedByOthers = penalizedByOthers;
            this.selfOverSpent = selfOverSpent;
            this.state = state;
        }

        // =========================
        // Getters（Thymeleaf）
        // =========================
        public String getCategoryName() { return categoryName; }
        public String getCategoryDisplayName() { return categoryDisplayName; }
        public boolean isAllocated() { return allocated; }
        public CategoryUsageState getState() { return state; }

        public int getBaseCategoryBudgetAmount() { return baseCategoryBudgetAmount; }
        public int getEffectiveCategoryBudgetAmount() { return effectiveCategoryBudgetAmount; }
        public int getPenaltyAmount() { return penaltyAmount; }
        public int getCategoryCurrentSpent() { return categoryCurrentSpent; }
        public int getAvailableAmount() { return availableAmount; }
        public int getCategoryOverSpentAmount() { return categoryOverSpentAmount; }
        public int getCategoryUsagePercent() { return categoryUsagePercent; }
        public int getDisplayUsagePercent() { return displayUsagePercent; }
        public boolean isPenalizedByOthers() { return penalizedByOthers; }
        public boolean isSelfOverSpent() { return selfOverSpent; }

        // =========================
        // UI Helper（只看 state）
        // =========================
        public int getDisplayBudgetAmount() {
            return effectiveCategoryBudgetAmount;
        }

        public String getStateNoteText() {
            return switch (state) {
                case GLOBAL_OVERFLOW -> "🚨 全月已超過上限，暫停新增消費";
                case SELF_OVERSPENT -> "🚨 本分類已超額";
                case NO_AVAILABLE_DUE_TO_POOL -> "⚠️ 因其他分類超額，本分類預算已調整，已無可用";
                case NORMAL -> (penalizedByOthers ? "⚠️ 因其他分類超額，本分類預算已調整" : "");
            };
        }

        public boolean hasStateNote() {
            String t = getStateNoteText();
            return t != null && !t.isBlank();
        }

        public String getProgressBarBgClass() {
            if (state == CategoryUsageState.GLOBAL_OVERFLOW) return "bg-danger";
            if (state == CategoryUsageState.SELF_OVERSPENT) return "bg-danger";
            if (state == CategoryUsageState.NO_AVAILABLE_DUE_TO_POOL) return "bg-secondary";
            if (displayUsagePercent >= 100) return "bg-danger";
            if (displayUsagePercent >= 80) return "bg-warning";
            return "bg-success";
        }

        public boolean isClickableForAddTransaction() {
            return allocated && state != CategoryUsageState.GLOBAL_OVERFLOW;
        }
    }
}
