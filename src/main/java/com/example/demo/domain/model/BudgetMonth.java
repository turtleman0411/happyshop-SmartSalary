package com.example.demo.domain.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.demo.domain.converter.YearMonthAttributeConverter;
import com.example.demo.domain.value.BudgetMonthId;
import com.example.demo.domain.value.IncomeAmount;
import com.example.demo.domain.value.TransactionDeletion;
import com.example.demo.domain.value.TransactionId;
import com.example.demo.domain.value.UserId;

import jakarta.persistence.*;

@Entity
@Table(
    name = "budget_month",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "month"}
        )
    }
)
public class BudgetMonth {

    /* ========== Identity ========== */
    @EmbeddedId
    private BudgetMonthId id;

    /* ========== Ownership ========== */
    @Embedded
    private UserId userId;

    /* ========== Period ========== */
    @Convert(converter = YearMonthAttributeConverter.class)
    @Column(nullable = false)
    private YearMonth month;

    @Embedded
    private IncomeAmount income;

    /* ========== Allocations ========== */
    @OneToMany(
        mappedBy = "budgetMonth",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CategoryAllocation> allocations = new ArrayList<>();


    @OneToMany(
    mappedBy = "budgetMonth",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    private List<Transaction> transactions = new ArrayList<>();

    /* ========== JPA only ========== */
    protected BudgetMonth() {}

    /* ========== Factory ========== */
    private BudgetMonth(
            UserId userId,
            YearMonth month,
            IncomeAmount income
    ) {
        this.id = BudgetMonthId.newId();
        this.userId = userId;
        this.month = month;
        this.income = income;
    }

    public static BudgetMonth create(
            UserId userId,
            YearMonth month,
            IncomeAmount income
    ) {
        return new BudgetMonth(userId, month,income);
    }



    /* ========== Domain Behaviors ========== */

    public void updateIncome(IncomeAmount newIncome) {
    if (this.income.equals(newIncome)) {
        return;
    }
    this.income = newIncome;
}


    /** 設定分類比例（唯一入口） */
    public void configureAllocations(
            Map<CategoryType, Integer> percents
    ) {
        int total = percents.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (total != 100) {
            throw new IllegalArgumentException("分類比例總和必須為 100%");
        }

        allocations.clear();

        for (var entry : percents.entrySet()) {
            CategoryAllocation allocation =
                    CategoryAllocation.of(
                            entry.getKey(),
                            entry.getValue()
                    );
            allocation.assignTo(this);
            allocations.add(allocation);
        }
    }

    /* ========== Transaction ========== */  
    public void resetAllocations() {
        this.allocations.clear();
    }
    /* ========== Transaction ========== */

    public void addTransaction(
        CategoryType category,
        int amount,
        LocalDate date,
        String note
) {
    Transaction tx = Transaction.create(
            category,
            amount,
            date,
            note
    );
    tx.assignTo(this);
    transactions.add(tx);
}

public TransactionId getLastTransactionId() {
    if (transactions.isEmpty()) {
        throw new IllegalStateException("目前尚無交易");
    }
    return transactions.get(transactions.size() - 1).getId();
}



public void attachTransactionImage(
        TransactionId transactionId,
        String imagePath
) {
    Transaction tx = transactions.stream()
        .filter(t -> t.getId().equals(transactionId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("交易不存在"));

    tx.attachImage(imagePath);
}



    public TransactionDeletion deleteTransaction(TransactionId transactionId) {

    Transaction tx = transactions.stream()
        .filter(t -> t.getId().equals(transactionId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("交易不存在"));
    
    String ImagePath = tx.getImagePath(); 

    transactions.remove(tx);

    return new TransactionDeletion(
            transactionId,
            ImagePath
    );
}




    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }


    /* ========== Read-only ========== */

    public BudgetMonthId getId() {
        return id;
    }

    public YearMonth getMonth() {
        return month;
    }

    public List<CategoryAllocation> getAllocations() {
        return List.copyOf(allocations);
    }

    public IncomeAmount income() {
    return income;
}
}












