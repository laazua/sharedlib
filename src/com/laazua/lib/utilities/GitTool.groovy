package com.laazua.lib.utilities


class GitTool {
    /**
     * 检出 Git 代码库
     * @param config 配置参数映射
     * @return checkout 步骤配置
     */
    static def checkout(Map config) {
        return {
            checkout([
                $class: 'GitSCM', 
                branches: [[name: "*/${config.branch}"]], 
                doGenerateSubmoduleConfigurations: false, 
                extensions: [[
                    $class: 'CloneOption', 
                    depth: 1, 
                    honorRefspec: true, 
                    noTags: true, 
                    shallow: true
                ]], 
                submoduleCfg: [], 
                userRemoteConfigs: [[
                    credentialsId: config.credentialsId, 
                    url: config.repositoryUrl
                ]]
            ])
        }
    }
    
    /**
     * 快速检出方法（简化参数）
     * @param repositoryUrl 代码库地址
     * @param branch 分支名称
     * @param credentialsId 凭据ID
     */
    static def quickCheckout(String repositoryUrl, String branch, String credentialsId = null) {
        def config = [
            repositoryUrl: repositoryUrl,
            branch: branch,
            credentialsId: credentialsId
        ]
        return checkout(config)
    }
}