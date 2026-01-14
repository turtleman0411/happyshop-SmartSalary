package com.example.demo.presentation.dto.view;

import java.time.YearMonth;
import java.util.List;

/**
 * 📊 Result Page Read Model
 *
 * 世界觀：
 * - Month：帳務事實（薪水 / 已花 / 剩餘）→ 不受治理邏輯影響
 * - Category：預算治理顯示 → 會因超額而被動態調整
 */
public class ResultPageView {

    // =========================
    // 基本識別
    // =========================
    public YearMonth month;
    public int salary; // 本月薪水快照（100%）

    // =========================
    // ⭐ Month（全月帳務事實｜聖域）
    // =========================
    public int monthlyBudgetAmount;    // = salary
    public int monthlySpentAmount;     // 全月所有消費（含歷史）
    public int monthlyRemainingAmount; // salary - monthlySpent
    public int usagePercent;           // floor(monthlySpent * 100 / salary)

    /**
     * 重置分配時可用的帳務基數（僅供分配操作使用）
     * ⚠️ 不等於分類可用總額
     */
    public int reallocatableAmount;

    public String monthlyRuleNote;

    // ========================
    // ⚠️ 超額池（全月唯一限制）
    // ========================
    public int overLimitAmount;         // salary * 20%
    public int overSpentAmount;         // max(0, monthlySpent - salary)
    public int overRemainingAmount;     // max(0, overLimit - overSpent)
    public int overPoolUsagePercent;    // floor(overSpent * 100 / overLimit)
    public boolean hasOverSpent;

    public String overPoolRuleNote;

    // =========================
    // 分類結果（治理顯示層）
    // =========================
    public List<CategorySummaryView> categorySummaryList;

    /**
     * 是否至少有一個分類參與本次分配
     */
    public boolean hasAllocation() {
        return categorySummaryList != null &&
               categorySummaryList.stream().anyMatch(CategorySummaryView::isAllocated);
    }

    /* =================================================
     * 📦 CategorySummaryView
     *
     * 設計語意：
     * - baseBudget：結構性預算（固定，不變）
     * - effectiveBudget：治理後可用上限（只影響字卡）
     * - 超額只影響「未超額的分類」
     * ================================================= */
    public static class CategorySummaryView {

        // ========================
        // 識別
        // ========================
        private final String categoryName;
        private final String categoryDisplayName;

        // 是否有參與本次分配
        private final boolean allocated;

        // ========================
        // 🧱 原始結構預算（固定）
        // ========================
        private final int baseCategoryBudgetAmount;

        // ========================
        // 🔒 治理後制度預算（字卡顯示用）
        // ========================
        private final int effectiveCategoryBudgetAmount;

        // 因他分類超額而被平均扣減的金額
        private final int penaltyAmount;

        // ========================
        // 📉 消費狀態
        // ========================
        private final int categoryCurrentSpent;

        // ========================
        // 📊 即時結果（字卡）
        // ========================
        private final int availableAmount;
        private final int categoryOverSpentAmount;

        // ========================
        // 📈 百分比
        // ========================
        private final int categoryUsagePercent;
        private final int displayUsagePercent;

        // ========================
        // 🚨 狀態標記
        // ========================
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
                boolean selfOverSpent
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
        }

        // ========================
        // Getters（Thymeleaf）
        // ========================

        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryDisplayName() {
            return categoryDisplayName;
        }

        public boolean isAllocated() {
            return allocated;
        }

        public int getBaseCategoryBudgetAmount() {
            return baseCategoryBudgetAmount;
        }

        public int getEffectiveCategoryBudgetAmount() {
            return effectiveCategoryBudgetAmount;
        }

        public int getPenaltyAmount() {
            return penaltyAmount;
        }

        public int getCategoryCurrentSpent() {
            return categoryCurrentSpent;
        }

        public int getAvailableAmount() {
            return availableAmount;
        }

        public int getCategoryOverSpentAmount() {
            return categoryOverSpentAmount;
        }

        public int getCategoryUsagePercent() {
            return categoryUsagePercent;
        }

        public int getDisplayUsagePercent() {
            return displayUsagePercent;
        }

        public boolean isPenalizedByOthers() {
            return penalizedByOthers;
        }

        public boolean isSelfOverSpent() {
            return selfOverSpent;
        }
    }
}
