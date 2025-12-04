package com.laazua.lib.enums


enum CredentialsType {
    // 密码认证
    PASSWD('passwd'),
    // 密钥认证
    SECRETKEY('secretKey'),

    final String value

    CredentialsType(String value) {
        this.value = value
    }

    // fromValue 方法将字符转化为 CredentialsType 类型
    // 具体使用参考 entry 中调用方式
    static CredentialsType fromValue(String value) {
        return values().find { it.value == value }
    }
}