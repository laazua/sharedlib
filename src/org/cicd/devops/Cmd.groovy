package org.cicd.devops


import org.cicd.utils.Utils

/*
* 获取git提交信息
*/
def GetGitMsg(){
  commitAuthor = sh(script: 'git log -1 --pretty=format:%an', returnStdout: true).trim()
  commitInfo = sh(script: 'git log -1 --pretty=format:%s', returnStdout: true).trim()
  commitId = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
  return [author: commitAuthor, id: commitId, info: commitInfo]
}

/*
* 下载源代码
* @param: CodeAddress -> 创建流水线时传入
*/
def PullCode(cfg){
  def utils = new Utils()
  try{
    checkout([
      $class: 'GitSCM', 
      branches: [[name: "*/${cfg.CodeBranch}"]], 
      doGenerateSubmoduleConfigurations: false, 
      extensions: [[$class: 'CloneOption', depth: 1, honorRefspec: true, noTags: true, shallow: true]], 
      submoduleCfg: [], 
      userRemoteConfigs: [[credentialsId: cfg.CodeCredId, url: "${CodeAddress}"]]
    ])
    utils.PrintMessage("green", "下载代码成功")
    def git = GetGitMsg()
    env.GIT_AUTHOR = git['author']
    env.GIT_INFO = git['info']
    env.GIT_COMMITID = git['id']
    return true
  }catch(e){ 
    utils.PrintMessage("blue", "异常捕获: ${e}")
    utils.PrintMessage("red", "下载代码失败")
    return false  
  }
}

/*
* 安装依赖和制品
*/
def BuildCode(cfg){
  def utils = new Utils()
  def cmd = cfg.BuildCmd
  def buildStatus = sh(script: """${cmd}""", returnStatus: true)
  if(buildStatus != 0){ 
    utils.PrintMessage("red", "打包代码失败") 
    return false  
  } else{ 
    utils.PrintMessage("green", "打包代码成功") 
    return true  
  }
}

/*
* 制作镜像
*/
def MakeImage(cfg){
  def utils = new Utils()
  def releaseName = utils.FormatProjectName()
  def buildCmd = """
    sudo docker build -t ${cfg.ImgUrl}/${cfg.ImgPath}/${releaseName}:v${BUILD_NUMBER} . \
    && sudo docker push ${cfg.ImgUrl}/${cfg.ImgPath}/${releaseName}:v${BUILD_NUMBER}
  """
  def makeStatus = sh(script: """${buildCmd}""", returnStatus: true)
  if(makeStatus != 0){ 
    utils.PrintMessage("red", "制作镜像失败") 
    return false  
  }

  withCredentials([string(credentialsId: cfg.CosiCredId, variable: 'CosignPass')]) {
    def cosiCmd = "sudo cosign-sign ${CosignPass} ${cfg.ImgUrl}/${cfg.ImgPath}/${releaseName}:v${BUILD_NUMBER}"
    def cosiStatus = sh(script: """${cosiCmd}""", returnStatus: true)
    if(cosiStatus != 0){ 
      utils.PrintMessage("red", "镜像签名失败") 
      return false
    }else{ 
      utils.PrintMessage("green", "镜像签名成功") 
      return true
    }
  }
}

/*
* 发布镜像
*/
def PublishImage(cfg){
  def utils = new Utils()
  def releaseName = utils.FormatProjectName()
  def helmSetArgs = utils.FormatHelmFlag(cfg)
  def publishCmd = """ helm -n ${cfg.ImgPath} upgrade -i "${releaseName}" \
    "oci://${cfg.ImgUrl}/${cfg.ImgPath}/${cfg.HelmPkgName}" \
    --version "${cfg.HelmPkgVersion}" --atomic --wait --timeout=360s --set ${helmSetArgs}
  """
  def publishStatus = sh(script: """${publishCmd}""", returnStatus: true)
  if(publishStatus != 0){ 
    utils.PrintMessage("red", "发布镜像失败") 
    return false
  }else{ 
    utils.PrintMessage("green", "发布镜像成功") 
    return true
  }
}

/*
* wap项目加载env配置
*/
def LoadEnv(cfg){
  def utils = new Utils()
  def releaseName = utils.FormatProjectName()
  if(!utils.AddConfigMap(cfg)){
    utils.PrintMessage("red", "k8s集群中不存在${JOB_NAME}的configMap")
    return false
  }
  def loadCmd = """kubectl -n ${cfg.ImgPath} get configmap ${releaseName} -o go-template=\'{{index .data ".env"}}\' >${WORKSPACE}/.env"""
  def loadStatus = sh(script: """${loadCmd}""", returnStatus: true)
  if(loadStatus != 0){ 
    utils.PrintMessage("red", "加载env配置失败") 
    return false
  }else{ 
    utils.PrintMessage("green", "加载env配置成功") 
    return true
  }
}

/*
* 代码扫描
*/
def SonarCode(cfg){
  def utils = new Utils()
  def scannerHome = tool 'sonar-scanner'
  def sonarCmd = """
    ${scannerHome}/bin/sonar-scanner \
        -Dsonar.projectKey=${JOB_NAME} \
        -Dsonar.projectName=${JOB_NAME} \
        -Dsonar.projectDescription=项目描述信息,域名:${DomeName} \
        -Dsonar.links.scm=${CodeAddress} \
        -Dsonar.sources=${cfg.ScannerSourcesPath} \
        -Dsonar.projectVersion=1.0 \
        -Dsonar.sourceEncoding=UTF-8 \
        -Dsonar.exclusions=${cfg.ScannerExclusionsPath}
  """
  withSonarQubeEnv('sonarqube'){
    def sonarStatus = sh(script: """${sonarCmd}""", returnStatus: true)
    if(sonarStatus != 0){ 
      utils.PrintMessage("red", "扫描代码失败") 
      return false
    }else{ 
      utils.PrintMessage("green", "扫描代码成功") 
    }
  }
  timeout(time: 5, unit: 'MINUTES') {
    def qg = waitForQualityGate()
    if(qg.status != 'OK'){ 
      utils.PrintMessage("red", "代码质量通不过")
      return false 
    }else{ utils.PrintMessage("green", "代码质量通过") }
  }
  return true
}