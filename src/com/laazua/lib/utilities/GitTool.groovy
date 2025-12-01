package com.laazua.lib.utilities


class GitTool {
    
    static def checkout(def script, Map config) {
        script.checkout([
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
                url: config.repository
            ]]
        ])
    }
    
    static def checkoutWithTag(def script, Map config, String tag) {
        script.checkout([
            $class: 'GitSCM', 
            branches: [[name: tag]], 
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
                url: config.repository
            ]]
        ])
    }
}
