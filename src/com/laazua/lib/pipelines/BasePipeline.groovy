package com.laazua.lib.pipelines

abstract class BasePipeline implements Serializable {
    protected def script
    protected Map config = [:]
    protected Map options = [:]

    BasePipeline(def script, Map config = [:]) {
        this.script = script
        this.config = config ?: [:]
        
        this.options = [
            timeout: [
                time: config.optionsTimeout ?: 60, 
                unit: config.optionsTimeoutUnit ?: 'MINUTES'
            ],
            disableConcurrentBuilds: config.optionsDisableConcurrentBuilds ?: true,
            buildDiscarder: [
                numToKeep: config.optionsBuildDiscarderNumToKeep ?: '10'
            ],
            retry: config.optionsRetryNum ?: 0,
            skipDefaultCheckout: config.optionsSkipDefaultCheckout ?: false,
        ]
    }

    final def execute() {
        // 设置流水线选项
        setupPipelineOptions()
        
        // 在 node 环境中执行所有逻辑
        script.node {
            // 直接内联所有逻辑，避免 CPS 转换问题
            def pipelineExecution = {
                try {
                    before()
                    stages()
                    onSuccess()
                } catch (err) {
                    script.echo "流水线抛出异常: ${err}"
                    onFailure(err)
                    throw err
                } finally {
                    try {
                        onAlways()
                    } catch (ignore) {
                        script.echo "onAlways 执行时发生异常: ${ignore}"
                    }
                }
            }
            
            // 处理重试逻辑
            if (options.retry && options.retry > 0) {
                script.retry(options.retry) {
                    // 处理超时逻辑
                    if (options.timeout && options.timeout.time) {
                        script.timeout(time: options.timeout.time, unit: options.timeout.unit) {
                            pipelineExecution()
                        }
                    } else {
                        pipelineExecution()
                    }
                }
            } else {
                // 处理超时逻辑（无重试）
                if (options.timeout && options.timeout.time) {
                    script.timeout(time: options.timeout.time, unit: options.timeout.unit) {
                        pipelineExecution()
                    }
                } else {
                    pipelineExecution()
                }
            }
        }
    }

    protected void setupPipelineOptions() {
        def properties = []

        if (options.disableConcurrentBuilds) {
            properties << script.disableConcurrentBuilds()
        }

        if (options.buildDiscarder && options.buildDiscarder.numToKeep) {
            properties << script.buildDiscarder(
                script.logRotator(
                    numToKeepStr: options.buildDiscarder.numToKeep
                )
            )
        }

        if (options.skipDefaultCheckout) {
            properties << script.skipDefaultCheckout()
        }

        if (properties) {
            script.properties(properties)
        }
    }

    // 子类可选覆盖
    protected void before() {}

    /** 子类通用工具：执行 resources 下的 scripts */
    protected void runResourceScript(String resourcePath, String outputName = null) {
        script.echo "[BasePipeline] 加载资源脚本: ${resourcePath}"

        def scriptText = script.libraryResource(resourcePath)
        outputName = outputName ?: resourcePath.split('/')[-1]

        script.writeFile file: outputName, text: scriptText
        script.sh "chmod +x ${outputName}"
        script.sh "./${outputName}"
    }

    /** 解析 resources 目录内的 YAML */
    protected Map parseYamlResource(String resourcePath) {
        script.echo "[BasePipeline] 加载 YAML 资源: ${resourcePath}"
        def yamlText = script.libraryResource(resourcePath)
        def yamlMap = script.readYaml text: yamlText
        return yamlMap
    }

    /** 解析 resources 目录内的 JSON */
    protected Map parseJSONResource(String resourcePath) {
        script.echo "[BasePipeline] 加载 JSON 资源: ${resourcePath}"
        def jsonText = script.libraryResource(resourcePath)
        def jsonMap = script.readJSON text: jsonText
        return jsonMap
    }

    // 子类必须实现：阶段定义（使用 script.stage(...)）
    protected abstract void stages()

    // 默认钩子，子类可覆盖或扩展
    protected void onSuccess() {
        script.echo "默认 onSuccess: 流水线成功"
    }

    protected void onFailure(def err) {
        script.echo "默认 onFailure: ${err}"
    }

    protected void onAlways() {
        script.echo "默认 onAlways: 清理/通知等"
    }
}