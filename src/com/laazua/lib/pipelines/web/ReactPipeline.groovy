package com.laazua.lib.pipelines.web

import com.laazua.lib.pipelines.BasePipeline
import com.laazua.lib.utilities.GitTool
import com.laazua.lib.utilities.NotificationFactory


class ReactPipeline extends BasePipeline {

    ReactPipeline(def script, Map config = [:]) {
        super(script, config)
    }

    @Override
    protected void before() {
        // script.echo "ReactPipeline before: 配置是 ${config}"

        // 更新公共配置
        config = config + parseYamlResource("config/com.${config.sharedlibBranch}.yaml")
        // 更新项目配置
        config = config + parseYamlResource("config/${config.sharedlibType}.${config.sharedlibBranch}.yaml")

        script.echo "GoPipeline 最终配置: ${config}"
    }

    @Override
    protected void stages() {
        script.stage('检出代码') {
            script.sh "echo '检出代码'"
            // GitTool.checkout(script, config)
        }

        script.stage('单元测试') {
            script.sh "echo '单元测试'"
        }

        script.stage('质量检查') {
            script.sh "echo '质量检查'"
        }

        script.stage('制作制品') {
            script.sh "echo '制作制品'"
        }

        script.stage('部署制品') {
            script.sh "echo '部署制品'"
        }
    }

    @Override
    protected void onSuccess() {
        script.echo "ReactPipeline: 成功钩子 — 可以放通知等"
        // 发送企微文本消息
        // NotificationFactory.sendWecomText(script, 
        //     config.webhook, 
        //     "这是一条企业微信文本消息"
        // )
}

    @Override
    protected void onFailure(def err) {
        script.echo "ReactPipeline: 失败钩子 — ${err}"
        // 可以在这里上传构建日志 / 发告警
    }

    @Override
    protected void onAlways() {
        script.echo "ReactPipeline: Always - 清理/归档等"
    }
}
