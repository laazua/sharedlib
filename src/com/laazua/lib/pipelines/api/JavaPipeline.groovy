package com.laazua.lib.pipelines.api

import com.laazua.lib.pipelines.BasePipeline


class JavaPipeline extends BasePipeline {

    JavaPipeline(def script, Map config = [:]) {
        super(script, config)
    }

    @Override
    protected void before() {
        // script.echo "JavaPipeline before: 配置是 ${config}"
        // 更新公共配置
        config = config + parseYamlResource("config/com.${config.sharedlibBranch}.yaml")
        // 更新项目配置
        config = config + parseYamlResource("config/${config.sharedlibType}.${config.sharedlibBranch}.yaml")

        script.echo "GoPipeline 最终配置: ${config}"
    }

    @Override
    protected void stages() {
        script.stage('检出代码') {
            script.sh "echo 'Java 检出代码'"
            // GitTool.checkout([
            //     repositoryUrl: config.repository,
            //     branch: config.branch,
            //     credentialsId: config.credentialsId,
            // ])
        }

        script.stage('单元测试') {
            script.sh "echo 'Java 单元测试'"
        }

        script.stage('代码质量') {
            script.sh "echo 'Java 代码质量检查'"
        }

        script.stage('制作制品') {
            script.sh "echo 'Java 制作制品'"
        }

        script.stage('上传制品') {
            script.sh "echo 'Java 上传制品'"
        }

        script.stage('部署制品') {
            script.sh "echo 'Java 部署制品'"
        }
    }

    @Override
    protected void onSuccess() {
        script.echo "JavaPipeline: 成功钩子 — 可以放通知等"
    }

    @Override
    protected void onFailure(def err) {
        script.echo "JavaPipeline: 失败钩子 — ${err}"
        // 可以在这里上传构建日志 / 发告警
    }

    @Override
    protected void onAlways() {
        script.echo "JavaPipeline: Always - 清理/归档等"
    }
}
