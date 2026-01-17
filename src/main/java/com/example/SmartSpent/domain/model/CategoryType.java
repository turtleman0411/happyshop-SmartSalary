package com.example.SmartSpent.domain.model;

public enum CategoryType {

    /* =========================
     * 固定生存成本
     * ========================= */
    RENT("房租"),
    UTILITIES("水電瓦斯"),
    COMMUNICATION("通訊費"),

    /* =========================
     * 日常生活支出
     * ========================= */
    FOOD("飲食"),
    TRAFFIC("交通"),
    DAILY_NECESSITIES("日用品"),

    /* =========================
     * 自我與健康
     * ========================= */
    MEDICAL("醫療"),
    FITNESS("健身"),
    LEARNING("學習成長"),

    /* =========================
     * 娛樂與社交
     * ========================= */
    ENTERTAINMENT("娛樂"),
    SOCIAL("社交"),
    TRAVEL("旅遊"),

    /* =========================
     * ⭐ 儲蓄（先存錢）
     * ========================= */
    SAVING("儲蓄"),

    /* =========================
     * 其他
     * ========================= */
    OTHER("其他");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
