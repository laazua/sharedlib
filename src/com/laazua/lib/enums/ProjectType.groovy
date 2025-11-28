package com.laazua.lib.enums


/**
 * 项目类型枚举
 */
enum ProjectType {
    // API 项目
    API_GO('go'),
    API_JAVA('java'), 
    API_PYTHON('python'),
    API_K8S('k8s'),

    // Web 项目
    WEB_VUE('vue'),
    WEB_REACT('react'),

    final String value

    ProjectType(String value) {
        this.value = value
    }

    // fromValue 方法将字符转化为 ProjectType 类型
    // 具体使用参考 entry 中调用方式
    static ProjectType fromValue(String value) {
        return values().find { it.value == value }
    }
}