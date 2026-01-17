package com.example.SmartSpent.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "category_allocation")
class CategoryAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所屬預算月份（反向關聯） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_month_id", nullable = false)
    private BudgetMonth budgetMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private int percent;

    protected CategoryAllocation() {}

    private CategoryAllocation(CategoryType category, int percent) {
        this.category = category;
        this.percent = percent;
    }

    static CategoryAllocation of(CategoryType category, int percent) {
        return new CategoryAllocation(category, percent);
    }

    void assignTo(BudgetMonth month) {
        this.budgetMonth = month;
    }

    int getPercent() {
        return percent;
    }

    CategoryType getCategory() {
        return category;
    }
}
