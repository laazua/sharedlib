package com.laazua.lib.utilities

import com.laazua.lib.enums.NotificationType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * 消息通知服务类
 * 支持飞书、钉钉、企微的通知发送
 */
class NotificationService {
    private Script script
    private String webhookUrl
    private String platform // 'feishu', 'dingtalk', 'wecom'
    private String secret   // 签名密钥（可选）

    NotificationService(Script script) {
        this.script = script
    }

    /**
     * 设置飞书配置
     */
    NotificationService withFeishu(String webhookUrl, String secret = null) {
        this.webhookUrl = webhookUrl
        this.platform = 'feishu'
        this.secret = secret
        return this
    }

    /**
     * 设置钉钉配置
     */
    NotificationService withDingtalk(String webhookUrl, String secret = null) {
        this.webhookUrl = webhookUrl
        this.platform = 'dingtalk'
        this.secret = secret
        return this
    }

    /**
     * 设置企业微信配置
     */
    NotificationService withWecom(String webhookUrl) {
        this.webhookUrl = webhookUrl
        this.platform = 'wecom'
        return this
    }

    /**
     * 发送文本消息
     */
    void sendText(String content, List<String> mentionedList = [], List<String> mentionedMobileList = []) {
        switch (platform) {
            case 'feishu':
                sendFeishuText(content, mentionedList, mentionedMobileList)
                break
            case 'dingtalk':
                sendDingtalkText(content, mentionedList)
                break
            case 'wecom':
                sendWecomText(content, mentionedList, mentionedMobileList)
                break
            default:
                script.error("Unsupported platform: ${platform}")
        }
    }

    /**
     * 发送Markdown消息
     */
    void sendMarkdown(String title, String content, Map<String, String> buttons = [:]) {
        switch (platform) {
            case 'feishu':
                sendFeishuMarkdown(title, content, buttons)
                break
            case 'dingtalk':
                sendDingtalkMarkdown(title, content, buttons)
                break
            case 'wecom':
                sendWecomMarkdown(title, content, buttons)
                break
            default:
                script.error("Unsupported platform: ${platform}")
        }
    }

    /**
     * 发送卡片消息
     * @param title 卡片标题
     * @param content 卡片内容
     * @param buttons 按钮映射 [按钮文字: 跳转链接]
     * @param cardType 卡片类型，企业微信专用：'text_notice'|'news_notice'|'button_interaction'
     * @param extraFields 额外字段，用于平台特定配置
     */
    void sendCard(String title, String content, Map<String, String> buttons = [:], 
                  String cardType = 'text_notice', Map extraFields = [:]) {
        switch (platform) {
            case 'feishu':
                sendFeishuCard(title, content, buttons, extraFields)
                break
            case 'dingtalk':
                sendDingtalkCard(title, content, buttons, extraFields)
                break
            case 'wecom':
                sendWecomCard(title, content, buttons, cardType, extraFields)
                break
            default:
                script.error("Unsupported platform: ${platform}")
        }
    }

    /**
     * 发送富文本消息（飞书post类型）
     */
    void sendPost(List<List<Map>> content) {
        if (platform != 'feishu') {
            script.error("Post type is only supported for Feishu")
            return
        }
        sendFeishuPost(content)
    }

    // ========== 飞书相关方法 ==========

    private void sendFeishuText(String content, List<String> mentionedList, List<String> mentionedMobileList) {
        Map payload = [
            msg_type: "text",
            content: [
                text: content
            ]
        ]

        // 添加@功能
        if (mentionedList || mentionedMobileList) {
            payload.content.text += "\n"
            mentionedList?.each { userId ->
                payload.content.text += "<at user_id=\"${userId}\"></at>"
            }
            mentionedMobileList?.each { mobile ->
                payload.content.text += "<at mobile=\"${mobile}\"></at>"
            }
        }

        sendRequest(payload)
    }

    private void sendFeishuMarkdown(String title, String content, Map<String, String> buttons = [:]) {
        List elements = [
            [
                tag: "markdown",
                content: content
            ]
        ]

        // 添加按钮
        if (buttons) {
            buttons.each { text, url ->
                elements << [
                    tag: "button",
                    text: [
                        tag: "plain_text",
                        content: text
                    ],
                    type: "primary",
                    url: url
                ]
            }
        }

        Map payload = [
            msg_type: "interactive",
            card: [
                header: [
                    title: [
                        tag: "plain_text",
                        content: title
                    ],
                    template: "blue"
                ],
                elements: elements
            ]
        ]

        sendRequest(payload)
    }

    private void sendFeishuCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        String template = extraFields.template ?: "wathet"
        
        Map payload = [
            msg_type: "interactive",
            card: [
                header: [
                    title: [
                        tag: "plain_text",
                        content: title
                    ],
                    template: template
                ],
                elements: [
                    [
                        tag: "div",
                        text: [
                            tag: "lark_md",
                            content: content
                        ]
                    ]
                ]
            ]
        ]

        // 添加按钮
        if (buttons) {
            List buttonElements = buttons.collect { text, url ->
                [
                    tag: "button",
                    text: [
                        tag: "plain_text",
                        content: text
                    ],
                    type: "primary",
                    url: url
                ]
            }
            payload.card.elements.addAll(buttonElements)
        }

        sendRequest(payload)
    }

    private void sendFeishuPost(List<List<Map>> content) {
        Map payload = [
            msg_type: "post",
            content: [
                post: [
                    zh_cn: [
                        title: "Jenkins构建通知",
                        content: content
                    ]
                ]
            ]
        ]

        sendRequest(payload)
    }

    // ========== 钉钉相关方法 ==========

    private void sendDingtalkText(String content, List<String> mentionedList) {
        Map payload = [
            msgtype: "text",
            text: [
                content: content
            ]
        ]

        if (mentionedList) {
            payload.at = [
                atMobiles: mentionedList,
                isAtAll: false
            ]
        }

        sendRequest(payload)
    }

    private void sendDingtalkMarkdown(String title, String content, Map<String, String> buttons = [:]) {
        // 如果有按钮，在markdown内容后面添加链接
        String fullContent = content
        if (buttons) {
            buttons.each { text, url ->
                fullContent += "\n\n [${text}](${url})"
            }
        }

        Map payload = [
            msgtype: "markdown",
            markdown: [
                title: title,
                text: fullContent
            ]
        ]

        sendRequest(payload)
    }

    private void sendDingtalkCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        String btnOrientation = extraFields.btnOrientation ?: "0"
        
        if (buttons.size() == 1) {
            // 单个按钮使用独立跳转ActionCard
            Map payload = [
                msgtype: "actionCard",
                actionCard: [
                    title: title,
                    text: content,
                    singleTitle: buttons.keySet().first(),
                    singleURL: buttons.values().first(),
                    btnOrientation: btnOrientation
                ]
            ]
            sendRequest(payload)
        } else {
            // 多个按钮使用BTNs ActionCard
            List<Map> btns = buttons.collect { text, url ->
                [
                    title: text,
                    actionURL: url
                ]
            }

            Map payload = [
                msgtype: "actionCard",
                actionCard: [
                    title: title,
                    text: content,
                    btns: btns,
                    btnOrientation: btnOrientation
                ]
            ]
            sendRequest(payload)
        }
    }

    // ========== 企业微信相关方法 ==========

    private void sendWecomText(String content, List<String> mentionedList, List<String> mentionedMobileList) {
        Map payload = [
            msgtype: "text",
            text: [
                content: content,
                mentioned_list: mentionedList,
                mentioned_mobile_list: mentionedMobileList
            ]
        ]

        sendRequest(payload)
    }

    private void sendWecomMarkdown(String title, String content, Map<String, String> buttons = [:]) {
        String fullContent = "### ${title}\n${content}"
        
        // 添加按钮链接
        if (buttons) {
            buttons.each { text, url ->
                fullContent += "\n\n [${text}](${url})"
            }
        }

        Map payload = [
            msgtype: "markdown",
            markdown: [
                content: fullContent
            ]
        ]

        sendRequest(payload)
    }

    private void sendWecomCard(String title, String content, Map<String, String> buttons, 
                              String cardType, Map extraFields) {
        switch (cardType) {
            case 'text_notice':
                sendWecomTextNoticeCard(title, content, buttons, extraFields)
                break
            case 'news_notice':
                sendWecomNewsNoticeCard(title, content, buttons, extraFields)
                break
            case 'button_interaction':
                sendWecomButtonInteractionCard(title, content, buttons, extraFields)
                break
            case 'textcard':
            default:
                sendWecomTextCard(title, content, buttons, extraFields)
        }
    }

    /**
     * 企业微信文本卡片（传统）
     */
    private void sendWecomTextCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        String description = content.length() > 50 ? content.substring(0, 47) + "..." : content
        String btnText = buttons.keySet().first() ?: "详情"
        String btnUrl = buttons.values().first() ?: "https://example.com"
        
        Map payload = [
            msgtype: "textcard",
            textcard: [
                title: title,
                description: description,
                url: btnUrl,
                btntxt: btnText
            ]
        ]

        sendRequest(payload)
    }

    /**
     * 企业微信文本通知模板卡片
     */
    private void sendWecomTextNoticeCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        Map card = [
            card_type: "text_notice",
            source: extraFields.source ?: [
                icon_url: "https://example.com/icon.png",
                desc: "Jenkins通知",
                desc_color: 1
            ],
            main_title: [
                title: title,
                desc: extraFields.subTitle ?: ""
            ]
        ]

        // 添加突出内容
        if (extraFields.emphasisContent) {
            card.emphasis_content = [
                title: extraFields.emphasisContent.title ?: " ",
                desc: extraFields.emphasisContent.desc ?: " "
            ]
        }

        // 添加引用区域
        if (extraFields.quoteArea) {
            card.quote_area = [
                type: 1,
                url: extraFields.quoteArea.url,
                title: extraFields.quoteArea.title ?: " ",
                quote_text: content
            ]
        } else {
            card.sub_title_text = content.length() > 100 ? content.substring(0, 97) + "..." : content
        }

        // 添加水平内容列表
        if (extraFields.horizontalContentList) {
            card.horizontal_content_list = extraFields.horizontalContentList
        }

        // 添加跳转列表
        List jumpList = []
        buttons.each { text, url ->
            jumpList << [
                type: 1,
                title: text,
                url: url
            ]
        }
        if (jumpList) {
            card.jump_list = jumpList
        }

        // 卡片整体跳转
        if (buttons.size() == 1) {
            card.card_action = [
                type: 1,
                url: buttons.values().first()
            ]
        }

        Map payload = [
            msgtype: "template_card",
            template_card: card
        ]

        sendRequest(payload)
    }

    /**
     * 企业微信图文展示模板卡片
     */
    private void sendWecomNewsNoticeCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        Map card = [
            card_type: "news_notice",
            source: extraFields.source ?: [
                icon_url: "https://example.com/icon.png",
                desc: "Jenkins通知",
                desc_color: 1
            ],
            main_title: [
                title: title,
                desc: extraFields.subTitle ?: ""
            ],
            card_image: extraFields.cardImage ?: [
                url: "https://example.com/image.jpg",
                aspect_ratio: 2.25
            ]
        ]

        if (content) {
            card.image_text_area = [
                type: 1,
                url: buttons.values().first() ?: "https://example.com",
                title: content.length() > 20 ? content.substring(0, 17) + "..." : content,
                desc: content
            ]
        }

        // 添加跳转列表
        List jumpList = []
        buttons.each { text, url ->
            jumpList << [
                type: 1,
                title: text,
                url: url
            ]
        }
        if (jumpList) {
            card.jump_list = jumpList
        }

        Map payload = [
            msgtype: "template_card",
            template_card: card
        ]

        sendRequest(payload)
    }

    /**
     * 企业微信按钮交互型模板卡片
     */
    private void sendWecomButtonInteractionCard(String title, String content, Map<String, String> buttons, Map extraFields) {
        Map card = [
            card_type: "button_interaction",
            source: extraFields.source ?: [
                icon_url: "https://example.com/icon.png",
                desc: "Jenkins通知",
                desc_color: 1
            ],
            main_title: [
                title: title,
                desc: extraFields.subTitle ?: ""
            ],
            sub_title_text: content
        ]

        // 添加按钮
        List buttonList = []
        buttons.eachWithIndex { text, url, index ->
            buttonList << [
                text: text,
                key: "key_${index}",
                type: extraFields.buttonType ?: 1, // 1: 跳转按钮, 2: 回调按钮
                url: url
            ]
        }
        card.button_selection = [
            question_key: "question_key_1",
            title: "操作选项",
            button_list: buttonList
        ]

        Map payload = [
            msgtype: "template_card",
            template_card: card
        ]

        sendRequest(payload)
    }

    // ========== 通用请求方法 ==========

    private void sendRequest(Map payload) {
        try {
            // 添加签名（如果需要）
            String finalUrl = webhookUrl
            if (secret && platform in ['feishu', 'dingtalk']) {
                finalUrl = addSignToUrl(webhookUrl, secret)
            }

            String jsonPayload = JsonOutput.toJson(payload)
            
            script.echo "Sending notification to ${platform}: ${jsonPayload}"

            // 使用Jenkins的httpRequest发送请求
            def response = script.httpRequest(
                url: finalUrl,
                httpMode: 'POST',
                contentType: 'APPLICATION_JSON',
                requestBody: jsonPayload,
                consoleLogResponseBody: false
            )

            if (response.status >= 200 && response.status < 300) {
                script.echo "Notification sent successfully to ${platform}"
            } else {
                script.echo "Failed to send notification: ${response.status} - ${response.content}"
            }

        } catch (Exception e) {
            script.echo "Error sending notification to ${platform}: ${e.message}"
        }
    }

    private String addSignToUrl(String url, String secret) {
        if (!secret) return url

        long timestamp = System.currentTimeMillis() / 1000
        String stringToSign = "${timestamp}\n${secret}"
        
        // 使用HMAC-SHA256签名
        String sign = script.sh(
            script: """
                echo -n '${stringToSign}' | openssl dgst -sha256 -hmac '${secret}' -binary | base64
            """,
            returnStdout: true
        ).trim()

        return "${url}&timestamp=${timestamp}&sign=${URLEncoder.encode(sign, 'UTF-8')}"
    }
}