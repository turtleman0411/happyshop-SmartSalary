package com.example.SmartSpent.presentation.dto.view;

public enum CategoryUsageState {
    NORMAL,                 // 正常可用
    SELF_OVERSPENT,          // 自己超額
    NO_AVAILABLE_DUE_TO_POOL,// 被別人用掉超額池，自己無法再用
    GLOBAL_OVERFLOW          // 全月已爆（理論上分類頁也要顯示）
}

