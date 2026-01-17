package com.example.SmartSpent.application.query;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.SmartSpent.domain.model.CategoryType;
import com.example.SmartSpent.presentation.dto.view.CategoryCardView;
import com.example.SmartSpent.presentation.dto.view.SelectPageView;

@Service
public class SelectPageQueryService {

    /**
     * Select Page（分類預算設定）
     *
     * 職責：
     * - 提供月份
     * - 提供「分類卡片」初始資料
     * - 不讀 DB（純畫面初始化）
     * - 不建立、不修改任何 Domain 狀態
     */
    public SelectPageView getSelectPage(
            /* UserId 保留參數位置，未來可擴充 user 狀態 */
            Object userId,
            YearMonth month
    ) {

        Map<String, List<CategoryCardView>> groupedCategories =
                buildGroupedCategories();

        return new SelectPageView(
                month,
                groupedCategories,
                "USER"   // 對齊你前端 initCategoryCard(userMode)
        );
    }

    /**
     * ⭐ 分類分組（畫面用）
     * - key：群組標題
     * - value：該群組的分類卡
     */
    private Map<String, List<CategoryCardView>> buildGroupedCategories() {

              Map<String, List<CategoryCardView>> groupedCategories = new LinkedHashMap<>();

                /* =========================
                * 基本生存支出
                * ========================= */
                groupedCategories.put(
                        "基本支出",
                        List.of(
                                toCard(CategoryType.RENT),
                                toCard(CategoryType.UTILITIES),
                                toCard(CategoryType.COMMUNICATION)
                        )
                );

                /* =========================
                * 日常生活
                * ========================= */
                groupedCategories.put(
                        "日常生活",
                        List.of(
                                toCard(CategoryType.FOOD),
                                toCard(CategoryType.TRAFFIC),
                                toCard(CategoryType.DAILY_NECESSITIES)
                        )
                );

                /* =========================
                * 健康與成長
                * ========================= */
                groupedCategories.put(
                        "健康與成長",
                        List.of(
                                toCard(CategoryType.MEDICAL),
                                toCard(CategoryType.FITNESS),
                                toCard(CategoryType.LEARNING)
                        )
                );

                /* =========================
                * 娛樂與社交
                * ========================= */
                groupedCategories.put(
                        "娛樂與社交",
                        List.of(
                                toCard(CategoryType.ENTERTAINMENT),
                                toCard(CategoryType.SOCIAL),
                                toCard(CategoryType.TRAVEL)
                        )
                );

                /* =========================
                * 儲蓄
                * ========================= */
                groupedCategories.put(
                        "儲蓄",
                        List.of(
                                toCard(CategoryType.SAVING)
                        )
                );

                /* =========================
                * 其他
                * ========================= */
                groupedCategories.put(
                        "其他",
                        List.of(
                                toCard(CategoryType.OTHER)
                        )
                );

                return groupedCategories;

    }

    /**
     * ⭐ 單一分類卡（初始值）
     * - percent = 0
     * - editable = true
     */
    private CategoryCardView toCard(CategoryType type) {
        return new CategoryCardView(
                type.name(),
                type.displayName(),
                0   // percent
        );
    }
}
