package com.laazua.lib.pipelines.api

import com.laazua.lib.pipelines.BasePipeline
import com.laazua.lib.utilities.GitTool
import com.laazua.lib.enums.CredentialsType


class GoPipeline extends BasePipeline {

    GoPipeline(def script, Map config = [:]) {
        super(script, config)
    }

    @Override
    protected void before() {
        // script.echo "GoPipeline before: 配置是 ${config}"

        // // 调用 shell 脚本示例
        // runResourceScript('scripts/test.sh')
        // runResourceScript('scripts/test.py')

        // // 加载 yaml 配置示例
        // def goYaml = parseYamlResource('config/go.yaml')
        // script.println goYaml.app_name

        // 更新公共配置
        config = config + parseYamlResource("config/com.${config.sharedlibBranch}.yaml")
        // 更新项目配置
        config = config + parseYamlResource("config/${config.sharedlibType}.${config.sharedlibBranch}.yaml")

        script.echo "GoPipeline 最终配置: ${config}"
    }

    @Override
    protected void stages() {
        script.stage('检出代码') {
            script.sh "echo 'Go 检出代码'"
            //GitTool.checkout(script, config)
            def credentials = script.parseCredentials(CredentialsType.PASSWD, config.credentialsId)
            script.sh """
                echo '${credentials.username}'
                echo '${credentials.password}'
            """
        }

        script.stage('单元测试') {
            script.sh "echo 'Go 单元测试'"
        }

        script.stage('代码质量') {
            script.sh "echo 'Go 代码质量检查'"
        }

        script.stage('制作制品') {
            script.sh "echo 'Go 制作制品'"
        }

        script.stage('上传制品') {
            script.sh "echo 'Go 上传制品'"
        }

        script.stage('部署制品') {
            script.sh "echo 'Go 部署制品'"
        }
    }

    @Override
    protected void onSuccess() {
        script.echo "GoPipeline: 成功钩子 — 可以放通知等"
    }

    @Override
    protected void onFailure(def err) {
        script.echo "GoPipeline: 失败钩子 — ${err}"
        // 可以在这里上传构建日志 / 发告警
    }

    @Override
    protected void onAlways() {
        script.echo "GoPipeline: Always - 清理/归档等"
    }
}
