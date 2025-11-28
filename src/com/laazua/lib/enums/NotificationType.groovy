package com.laazua.lib.enums

/**
 * 通知类型枚举
 */
enum NotificationType {
    MARKDOWN("markdown"),
    TEXT("text"),
    CARD("card"),
    POST("post")

    final String value

    NotificationType(String value) {
        this.value = value
    }

    static NotificationType fromValue(String value) {
        return values().find { it.value == value }
    }
}