##### 通知消息使用示例


- **飞书**
    + 文本消息
    ```groovy
    // 简单文本
    NotificationFactory.sendFeishuText(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "这是一条飞书文本消息"
    )

    // 带@功能的文本
    NotificationFactory.sendFeishuText(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "构建完成，请相关同事查看",
        ["user_id_1", "user_id_2"],  // @用户ID
        ["13800138000"],             // @手机号
        "your_secret"
    )
    ```
    + markdown 消息
    ```groovy
    // 简单Markdown
    NotificationFactory.sendFeishuMarkdown(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "构建通知",
        "**项目**: demo-project\n**状态**: ✅ 成功\n**时间**: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
    )

    // 带按钮的Markdown
    NotificationFactory.sendFeishuMarkdown(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "版本发布通知",
        """**版本**: v1.2.0
    **内容**: 
    - 新增用户管理功能
    - 优化系统性能
    - 修复已知问题

    **发布人**: 张三""",
        [
            "查看变更": "https://example.com/changelog",
            "下载地址": "https://example.com/download",
            "文档链接": "https://example.com/docs"
        ],
        "your_secret"
    )
    ```
    + 卡片消息
    ```groovy
    // 简单卡片
    NotificationFactory.sendFeishuCard(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "审批通知",
        "您有一个新的审批请求待处理，请及时审批。",
        [
            "查看详情": "https://example.com/approval/123",
            "立即处理": "https://example.com/approve/123"
        ]
    )

    // 自定义模板卡片
    NotificationFactory.sendFeishuCard(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        "紧急告警",
        "CPU使用率超过90%，请立即处理！",
        [
            "查看监控": "https://example.com/monitor",
            "处理告警": "https://example.com/alert"
        ],
        [
            template: "red",  // 红色模板
            title: "🚨 紧急告警"
        ],
        "your_secret"
    )
    ```
    + 富文本
    ```groovy
    def postContent = [
        [
            [
                tag: "text",
                text: "Jenkins构建通知："
            ],
            [
                tag: "a",
                text: "点击查看详情",
                href: "${env.BUILD_URL}"
            ]
        ],
        [
            [
                tag: "text",
                text: "项目：${env.JOB_NAME}"
            ]
        ],
        [
            [
                tag: "text", 
                text: "状态："
            ],
            [
                tag: "text",
                text: "✅ 成功",
                un_escape: true
            ]
        ]
    ]

    NotificationFactory.sendFeishuPost(script, 
        "https://open.feishu.cn/open-apis/bot/v2/hook/xxx", 
        postContent,
        "your_secret"
    )
    ```


- **钉钉**
    + 文本消息
    ```groovy
    // 简单文本
    NotificationFactory.sendDingtalkText(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "这是一条钉钉文本消息"
    )

    // 带@功能的文本
    NotificationFactory.sendDingtalkText(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "有新的部署任务完成 @13800138000",
        ["13800138000"],  // @手机号
        "your_secret"
    )
    ```
    + markdown 消息
    ```groovy
    // 简单Markdown
    NotificationFactory.sendDingtalkMarkdown(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "部署完成",
        "### 部署报告\n- **项目**: user-service\n- **环境**: production\n- **状态**: ✅ 成功\n- **时间**: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
    )

    // 带按钮的Markdown
    NotificationFactory.sendDingtalkMarkdown(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "数据库备份通知",
        """### 🗄️ 数据库备份完成

    **备份信息**：
    - 数据库：production_db
    - 大小：2.1 GB
    - 耗时：15分30秒
    - 状态：✅ 成功

    **存储位置**：/backup/db/production_20231201.sql""",
        [
            "查看备份": "https://example.com/backup/list",
            "下载备份": "https://example.com/backup/download/123"
        ],
        "your_secret"
    )
    ```
    + 卡片消息
    ```groovy
    // 单个按钮卡片
    NotificationFactory.sendDingtalkCard(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "会议提醒",
        "### 📅 季度总结会议\n**时间**: 今天 14:00-16:00\n**地点**: 第一会议室\n**参会人员**: 全体部门成员\n请准时参加！",
        [
            "查看会议详情": "https://example.com/meeting/456"
        ]
    )

    // 多个按钮卡片
    NotificationFactory.sendDingtalkCard(script, 
        "https://oapi.dingtalk.com/robot/send?access_token=xxx", 
        "请假审批",
        "**申请人**: 李四\n**类型**: 年假\n**时间**: 2023-12-01 至 2023-12-03\n**天数**: 3天\n**事由**: 个人事务",
        [
            "批准": "https://example.com/approve/123",
            "拒绝": "https://example.com/reject/123",
            "查看详情": "https://example.com/leave/123"
        ],
        [
            btnOrientation: "1"  // 按钮垂直排列
        ],
        "your_secret"
    )
    ````


- **企微**
    + 文本消息
    ```groovy
    // 简单文本
    NotificationFactory.sendWecomText(script, 
        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
        "这是一条企业微信文本消息"
    )

    // 带@功能的文本
    NotificationFactory.sendWecomText(script, 
        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
        "代码审查完成，请相关同事查看 @王五 @赵六",
        ["wangwu", "zhaoliu"],  // @用户ID
        ["13800138000"]         // @手机号
    )
    ```
    + markdown 消息
    ```groovy
    // 简单Markdown
    NotificationFactory.sendWecomMarkdown(script, 
        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
        "系统监控报告",
        "### 📊 系统监控报告\n> **服务器**: 10.0.1.100\n> **CPU使用率**: 45%\n> **内存使用率**: 60%\n> **磁盘使用率**: 75%\n> **状态**: 🟢 正常"
    )

    // 带按钮的Markdown
    NotificationFactory.sendWecomMarkdown(script, 
        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
        "代码审查通知",
        """### 🔍 代码审查完成

    **审查信息**：
    - 仓库：frontend/react-app
    - 分支：feature/user-auth
    - 提交者：张三
    - 审查者：李四

    **审查结果**：
    ✅ 通过
    📝 2个建议项

    **建议内容**：
    1. 优化组件性能
    2. 添加错误边界处理""",
        [
            "查看PR": "https://github.com/example/pull/123",
            "查看建议": "https://github.com/example/pull/123/comments"
        ]
    )
    ```
    + 卡片消息
        * 文本卡片
        ```groovy
        // 文本通知卡片
        NotificationFactory.sendWecomTextNoticeCard(script, 
            "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
            "月度报表生成完成",
            "12月份销售报表已生成，总销售额：¥1,234,567，环比增长15.8%。",
            [
                "查看报表": "https://example.com/report/202312",
                "下载Excel": "https://example.com/report/202312/download"
            ],
            [
                emphasisContent: [
                    title: "¥1,234,567",
                    desc: "总销售额"
                ],
                source: [
                    icon_url: "https://example.com/icons/chart.png",
                    desc: "数据报表系统",
                    desc_color: 1
                ],
                horizontalContentList: [
                    [ keyname: "环比增长", value: "15.8%" ],
                    [ keyname: "同比增长", value: "23.4%" ],
                    [ keyname: "完成率", value: "108%", type: 1, url: "https://example.com/target" ]
                ]
            ]
        )
        ```
        * 图文卡片
        ```groovy
        // 图文展示卡片
        NotificationFactory.sendWecomNewsNoticeCard(script, 
            "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
            "新产品发布：智能助手v2.0",
            "全新升级的智能助手，支持多模态交互和个性化推荐，提升用户体验50%",
            [
                "立即体验": "https://example.com/product/ai-assistant",
                "查看文档": "https://example.com/docs/ai-assistant",
                "反馈建议": "https://example.com/feedback"
            ],
            [
                cardImage: [
                    url: "https://example.com/images/ai-assistant-v2.jpg",
                    aspect_ratio: 2.25
                ],
                source: [
                    icon_url: "https://example.com/icons/product.png",
                    desc: "产品发布",
                    desc_color: 2
                ]
            ]
        )
        ```
        * 按钮卡片
        ```groovy
        // 按钮交互卡片
        NotificationFactory.sendWecomButtonInteractionCard(script, 
            "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
            "任务处理",
            "请选择如何处理这个用户反馈：",
            [
                "标记为已处理": "https://example.com/feedback/123/processed",
                "转交技术部": "https://example.com/feedback/123/transfer/tech",
                "转交客服部": "https://example.com/feedback/123/transfer/service",
                "稍后处理": "https://example.com/feedback/123/later"
            ],
            [
                buttonType: 1,  // 跳转按钮
                source: [
                    desc: "工单系统",
                    desc_color: 0
                ]
            ]
        )
        ```
        * 传统文本卡片
        ```groovy
        // 传统文本卡片（简单场景）
        NotificationFactory.sendWecomTextCard(script, 
            "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", 
            "简单通知",
            "您的申请已提交，我们会在24小时内处理。",
            [
                "查看进度": "https://example.com/application/123"
            ]
        )
        ```


- 通用通知
```groovy
// 飞书构建成功
NotificationFactory.sendBuildSuccess(script, 'feishu', 
    "https://open.feishu.cn/open-apis/bot/v2/hook/xxx",
    env.JOB_NAME,
    env.BUILD_URL,
    [secret: 'your_secret']
)

// 钉钉构建成功  
NotificationFactory.sendBuildSuccess(script, 'dingtalk',
    "https://oapi.dingtalk.com/robot/send?access_token=xxx",
    env.JOB_NAME, 
    env.BUILD_URL,
    [secret: 'your_secret']
)

// 企微构建成功
NotificationFactory.sendBuildSuccess(script, 'wecom',
    "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx",
    env.JOB_NAME,
    env.BUILD_URL
)

/**
* NotificationFactory.sendBuildFailure
*/
```