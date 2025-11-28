package com.laazua.lib.utilities

/**
 * 通知服务工厂类 - 静态调用方式
 */
class NotificationFactory {

    /**
     * 创建飞书通知服务
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 飞书webhook地址
     * @param secret 签名密钥（可选）
     * @return NotificationService实例
     */
    static NotificationService createFeishu(Script script, String webhookUrl, String secret = null) {
        return new NotificationService(script).withFeishu(webhookUrl, secret)
    }

    /**
     * 创建钉钉通知服务
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 钉钉webhook地址
     * @param secret 签名密钥（可选）
     * @return NotificationService实例
     */
    static NotificationService createDingtalk(Script script, String webhookUrl, String secret = null) {
        return new NotificationService(script).withDingtalk(webhookUrl, secret)
    }

    /**
     * 创建企业微信通知服务
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企业微信webhook地址
     * @return NotificationService实例
     */
    static NotificationService createWecom(Script script, String webhookUrl) {
        return new NotificationService(script).withWecom(webhookUrl)
    }

    // ========== 飞书快速发送方法 ==========

    /**
     * 快速发送飞书文本消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 飞书webhook地址
     * @param content 消息内容
     * @param mentionedList 用户ID列表（可选）
     * @param mentionedMobileList 手机号列表（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendFeishuText(Script script, String webhookUrl, String content, 
                              List<String> mentionedList = [], List<String> mentionedMobileList = [], 
                              String secret = null) {
        createFeishu(script, webhookUrl, secret).sendText(content, mentionedList, mentionedMobileList)
    }

    /**
     * 快速发送飞书Markdown消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 飞书webhook地址
     * @param title 消息标题
     * @param content Markdown内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendFeishuMarkdown(Script script, String webhookUrl, String title, String content, 
                                  Map<String, String> buttons = [:], String secret = null) {
        createFeishu(script, webhookUrl, secret).sendMarkdown(title, content, buttons)
    }

    /**
     * 快速发送飞书卡片消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 飞书webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendFeishuCard(Script script, String webhookUrl, String title, String content, 
                              Map<String, String> buttons = [:], Map extraFields = [:], String secret = null) {
        createFeishu(script, webhookUrl, secret).sendCard(title, content, buttons, 'interactive', extraFields)
    }

    /**
     * 快速发送飞书富文本消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 飞书webhook地址
     * @param content 富文本内容
     * @param secret 签名密钥（可选）
     */
    static void sendFeishuPost(Script script, String webhookUrl, List<List<Map>> content, String secret = null) {
        createFeishu(script, webhookUrl, secret).sendPost(content)
    }

    // ========== 钉钉快速发送方法 ==========

    /**
     * 快速发送钉钉文本消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 钉钉webhook地址
     * @param content 消息内容
     * @param mentionedList @用户手机号列表（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendDingtalkText(Script script, String webhookUrl, String content, 
                                List<String> mentionedList = [], String secret = null) {
        createDingtalk(script, webhookUrl, secret).sendText(content, mentionedList)
    }

    /**
     * 快速发送钉钉Markdown消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 钉钉webhook地址
     * @param title 消息标题
     * @param content Markdown内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendDingtalkMarkdown(Script script, String webhookUrl, String title, String content, 
                                    Map<String, String> buttons = [:], String secret = null) {
        createDingtalk(script, webhookUrl, secret).sendMarkdown(title, content, buttons)
    }

    /**
     * 快速发送钉钉卡片消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 钉钉webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     * @param secret 签名密钥（可选）
     */
    static void sendDingtalkCard(Script script, String webhookUrl, String title, String content, 
                                Map<String, String> buttons = [:], Map extraFields = [:], String secret = null) {
        createDingtalk(script, webhookUrl, secret).sendCard(title, content, buttons, 'actionCard', extraFields)
    }

    // ========== 企业微信快速发送方法 ==========

    /**
     * 快速发送企微文本消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param content 消息内容
     * @param mentionedList 用户ID列表（可选）
     * @param mentionedMobileList 手机号列表（可选）
     */
    static void sendWecomText(Script script, String webhookUrl, String content, 
                             List<String> mentionedList = [], List<String> mentionedMobileList = []) {
        createWecom(script, webhookUrl).sendText(content, mentionedList, mentionedMobileList)
    }

    /**
     * 快速发送企微Markdown消息
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param title 消息标题
     * @param content Markdown内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     */
    static void sendWecomMarkdown(Script script, String webhookUrl, String title, String content, 
                                 Map<String, String> buttons = [:]) {
        createWecom(script, webhookUrl).sendMarkdown(title, content, buttons)
    }

    /**
     * 快速发送企微文本通知卡片
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     */
    static void sendWecomTextNoticeCard(Script script, String webhookUrl, String title, String content, 
                                       Map<String, String> buttons = [:], Map extraFields = [:]) {
        createWecom(script, webhookUrl).sendCard(title, content, buttons, 'text_notice', extraFields)
    }

    /**
     * 快速发送企微图文卡片
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     */
    static void sendWecomNewsNoticeCard(Script script, String webhookUrl, String title, String content, 
                                       Map<String, String> buttons = [:], Map extraFields = [:]) {
        createWecom(script, webhookUrl).sendCard(title, content, buttons, 'news_notice', extraFields)
    }

    /**
     * 快速发送企微按钮交互卡片
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     */
    static void sendWecomButtonInteractionCard(Script script, String webhookUrl, String title, String content, 
                                              Map<String, String> buttons = [:], Map extraFields = [:]) {
        createWecom(script, webhookUrl).sendCard(title, content, buttons, 'button_interaction', extraFields)
    }

    /**
     * 快速发送企微传统文本卡片
     * @param script Jenkins pipeline脚本对象
     * @param webhookUrl 企微webhook地址
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]（可选）
     * @param extraFields 额外字段，用于平台特定配置（可选）
     */
    static void sendWecomTextCard(Script script, String webhookUrl, String title, String content, 
                                 Map<String, String> buttons = [:], Map extraFields = [:]) {
        createWecom(script, webhookUrl).sendCard(title, content, buttons, 'textcard', extraFields)
    }

    // ========== 通用快速发送方法 ==========

    /**
     * 快速发送构建成功通知（多平台）
     * @param script Jenkins pipeline脚本对象
     * @param platform 平台类型 'feishu', 'dingtalk', 'wecom'
     * @param webhookUrl webhook地址
     * @param projectName 项目名称
     * @param buildUrl 构建链接
     * @param extraParams 额外参数（secret等）
     */
    static void sendBuildSuccess(Script script, String platform, String webhookUrl, 
                                String projectName, String buildUrl, Map extraParams = [:]) {
        String title = "✅ 构建成功 - ${projectName}"
        String content = """
            **项目**: ${projectName}
            **状态**: ✅ 成功
            **构建编号**: ${script.env.BUILD_NUMBER}
            **构建链接**: [点击查看](${buildUrl})
            **时间**: ${new Date().format('yyyy-MM-dd HH:mm:ss')}
        """.stripIndent().trim()

        switch (platform) {
            case 'feishu':
                sendFeishuMarkdown(script, webhookUrl, title, content, 
                                  ['查看详情': buildUrl], extraParams.secret)
                break
            case 'dingtalk':
                sendDingtalkMarkdown(script, webhookUrl, title, content, 
                                    ['查看详情': buildUrl], extraParams.secret)
                break
            case 'wecom':
                sendWecomTextNoticeCard(script, webhookUrl, title, content, 
                                       ['查看详情': buildUrl],
                                       [emphasisContent: [title: "SUCCESS", desc: "构建状态"]])
                break
        }
    }

    /**
     * 快速发送构建失败通知（多平台）
     * @param script Jenkins pipeline脚本对象
     * @param platform 平台类型 'feishu', 'dingtalk', 'wecom'
     * @param webhookUrl webhook地址
     * @param projectName 项目名称
     * @param buildUrl 构建链接
     * @param errorMessage 错误信息
     * @param extraParams 额外参数（secret等）
     */
    static void sendBuildFailure(Script script, String platform, String webhookUrl, 
                                String projectName, String buildUrl, String errorMessage, 
                                Map extraParams = [:]) {
        String title = "❌ 构建失败 - ${projectName}"
        String content = """
            **项目**: ${projectName}
            **状态**: ❌ 失败
            **构建编号**: ${script.env.BUILD_NUMBER}
            **错误信息**: ${errorMessage}
            **构建链接**: [点击查看](${buildUrl})
            **时间**: ${new Date().format('yyyy-MM-dd HH:mm:ss')}
        """.stripIndent().trim()

        switch (platform) {
            case 'feishu':
                sendFeishuMarkdown(script, webhookUrl, title, content, 
                                  ['查看详情': buildUrl], extraParams.secret)
                break
            case 'dingtalk':
                sendDingtalkMarkdown(script, webhookUrl, title, content, 
                                    ['查看详情': buildUrl], extraParams.secret)
                break
            case 'wecom':
                sendWecomTextNoticeCard(script, webhookUrl, title, content, 
                                       ['查看详情': buildUrl],
                                       [emphasisContent: [title: "FAILED", desc: "构建状态"]])
                break
        }
    }
}