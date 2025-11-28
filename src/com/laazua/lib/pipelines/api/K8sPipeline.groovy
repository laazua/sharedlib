package com.laazua.lib.pipelines.api

import com.laazua.lib.pipelines.BasePipeline


class K8sPipeline extends BasePipeline {

    K8sPipeline(def script, Map config = [:]) {
        super(script, config)
    }

    @Override
    protected void before() {
        // script.echo "K8sPipeline before: 配置是 ${config}"

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
            // GitTool.checkout([
            //     repositoryUrl: config.repository,
            //     branch: config.branch,
            //     credentialsId: config.credentialsId,
            // ])
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

        script.stage('制作镜像') {
            script.sh "echo '制作镜像'"
        }

        script.stage('上传镜像') {
            script.sh "echo '上传镜像'"
        }

        script.stage('helm打包') {
            script.sh(script: """
                echo 'helm打包'
            """)
        }
        script.stage('部署helm包') {
            script.sh(script: """
                echo '部署helm包'
            """)
        }
    }

    @Override
    protected void onSuccess() {
        script.echo "K8sPipeline: 成功钩子 — 可以放通知等"
    }

    @Override
    protected void onFailure(def err) {
        script.echo "K8sPipeline: 失败钩子 — ${err}"
        // 可以在这里上传构建日志 / 发告警
    }

    @Override
    protected void onAlways() {
        script.echo "K8sPipeline: Always - 清理/归档等"
    }
}
