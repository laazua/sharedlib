import org.cicd.utils.Utils
import org.cicd.enums.PipelineType


def call(pipelineType){
  def webhook
  def utils = new Utils()
  //加载默认配置
  def cfg_text = libraryResource("config.yaml")
  def cfg = readYaml text: cfg_text
  pipeline{
    agent any
    options{
      skipDefaultCheckout()  //删除隐式checkout scm语句
      disableConcurrentBuilds() //禁止并行
      timeout(time: 1, unit: 'HOURS')  //流水线超时设置1小时
    }
    stages{
      stage("开始加载流水线"){
        steps{
          script{
            switch(pipelineType){
              case PipelineType.WEB:
                webhook = cfg.Web.Webhook
                utils.SendMessage("${JOB_NAME}开始运行", webhook)
                webPipeline(cfg.Web)          
                break
              case PipelineType.WAP:
                webhook = cfg.Wap.Webhook
                utils.SendMessage("${JOB_NAME}开始运行", webhook)
                wapPipeline(cfg.Wap)            
                break
              case PipelineType.APIFPM:
                webhook = cfg.ApiFpm.Webhook
                utils.SendMessage("${JOB_NAME}开始运行", webhook)
                apiPipeline(cfg.ApiFpm)
                break
              case PipelineType.APIHYPERF:
                webhook = cfg.ApiHyperf.Webhook
                utils.SendMessage("${JOB_NAME}开始运行", webhook)
                apiPipeline(cfg.ApiHyperf)
                break
            }
          }
        }
      }
    }
    post{
      success{
        script{
          utils.SendMessage("${JOB_NAME} 发布到k8s成功 ✔", webhook)
        }
      }
      failure{
        script{
          utils.SendMessage("${JOB_NAME} 发布到k8s失败 ❌", webhook)
        }
      }
      aborted {
        script {
          utils.SendMessage("${JOB_NAME} 发布到k8s中断 ❌", webhook)
        }
      }
    }
  }
}

return this