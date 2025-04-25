package org.cicd.utils

import groovy.json.JsonOutput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
* 发送通知消息
* @param: msg
* @param: webhook
*/
@NonCPS
def SendMessage(message, webhook){
  def items = []
  def nowTime = LocalDateTime.now()
  def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def currentDateTime = LocalDateTime.now().format(formatter)
  if(message.contains("开始运行")){
    [
      "当前时间": "${currentDateTime}", 
      "运行说明": "${message}"
    ].each{ key, value ->
      mItem = [ tag: "div", text: [ tag: "lark_md", content: null ] ] 
      mItem.text.content = "${key}: ${value}"; items.add(mItem) 
    }
  }else{
    [
      "当前时间": "${currentDateTime}", 
      "提交作者": "${env.GIT_AUTHOR}", 
      "提交信息": "${env.GIT_INFO}",
      "提交ID" : "${env.GIT_COMMITID}", 
      "项目域名": "${DomeName}", 
      "运行说明": "${message}"
    ].each{ key, value -> 
      mItem = [ tag: "div", text: [ tag: "lark_md", content: null ] ]
      mItem.text.content = "${key}: ${value}"; items.add(mItem) 
    }
  }
  def url = new URL(webhook)
  def connection = url.openConnection()
  connection.setRequestMethod("POST")
  connection.setRequestProperty("Content-Type", "application/json")
  connection.setDoOutput(true)

  def payload = [
    "msg_type": "interactive",
    "card": [
      "header": [
        "title": [
          "tag": "plain_text",
          "content": "任务名称: ${JOB_NAME}"
        ],
        "template": "turquoise",
        "background_style": "primary",
      ],
      "elements": items
    ]
  ]
  def jsonPayload = JsonOutput.toJson(payload)
  connection.getOutputStream().write(jsonPayload.getBytes("UTF-8"))

  def responseCode = connection.getResponseCode()
  if(responseCode == 200){
    PrintMessage("green", "消息已成功发送到飞书机器人!")
  }else{
    PrintMessage("red", "发送消息到飞书机器人失败,响应代码: ${responseCode}")
  }
  // 读取响应数据
  //   def inputStream = connection.getInputStream()
  //   def response = inputStream.text  
  //   PrintMessage("blue", "响应数据: ${response}")
  //   PrintMessage("red", "发送消息到飞书机器人失败: ${e.message}")
  connection.disconnect()
}


/*
* 格式化输出内容
* @param: color
* @param: value
* 需要AnsiColor插件
*/
def PrintMessage(color, value){
  def colors = [
    'blue'  : "\033[1;34m -------------------------------[${value}]------------------------------- \033[0m",
    'green' : "\033[1;32m -------------------------------[${value}]------------------------------- \033[0m",
    'red'   : "\033[1;31m -------------------------------[${value}]------------------------------- \033[0m"
    ]
  ansiColor('xterm') {
    println(colors[color])
  }
}


/*
* 格式化helm发布参数
*/
def FormatHelmFlag(cfg){
  def imgName = FormatProjectName()
  switch(cfg.ImgPath){
    case "yuzhua-web":
      return "image.repository=${cfg.ImgUrl}/${cfg.ImgPath}/${imgName},image.tag=v${BUILD_NUMBER},ingress.domain=${DomeName},ingress.className=${cfg.ImgPath},replicaCount=1"
    case "yuzhua-wap":
      def wapDomeName = FormatDomeName()
      return "image.repository=${cfg.ImgUrl}/${cfg.ImgPath}/${imgName},image.tag=v${BUILD_NUMBER},ingress.className=${cfg.ImgPath},${wapDomeName},readinessProbe.httpGet.path=${HttpPath}"
    case "yuzhua-api":
      def helmArgs = FormatHelmArgs()
      return "image.repository=${cfg.ImgUrl}/${cfg.ImgPath}/${imgName},image.tag=v${BUILD_NUMBER},${helmArgs}"
  }
}


/*
* 格式化HelmArgs参数的格式
*/
def FormatHelmArgs(){
  def args = HelmArgs.split('\n').findAll { !it.startsWith('#') }.collect { it.trim() }.join(',')
  return args
}

/*
* 格式化多域名参数
*/
def FormatDomeName(){
  // --set ingress.domain[0]=wap-hanc.yuzhua-test.com
  def domeArra = []
  if(DomeName.split(",").size()>1){
    DomeName.split(",").eachWithIndex { item, i ->
      domeArra.add("ingress.domain[${i}]=${item}")
    }
    return domeArra.join(",")
  }
  return "ingress.domain[0]=${DomeName}"
}

/*
* 格式化项目名称
*/
def FormatProjectName(){
  def jobName = JOB_NAME
  return jobName.replaceAll("_", "-").replaceAll("\\.", "-").toLowerCase()
}

/*
* 判断k8s集群中是否存在项目的configMap
* 如果不存在configMap就从文件中加载配置文件
*/

def AddConfigMap(cfg){
  def releaseName = FormatProjectName()
  def statusGet = sh(script: """kubectl -n yuzhua-wap get configmaps ${releaseName}""", returnStatus: true)
  if(statusGet != 0){
    def statusAdd = sh(script: """kubectl -n yuzhua-wap create configmap ${releaseName} --from-file=.env=${cfg.EnvPath}/${JOB_NAME}.env""",returnStatus: true)
    if(statusAdd == 0){
      return true
    }
    return false
  }else{
    return true
  }
}