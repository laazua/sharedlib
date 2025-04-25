import org.cicd.devops.Cmd


def call(cfg){
  def cmd = new Cmd()

  stage("下载代码"){
    if(!cmd.PullCode(cfg)){
      error "下载代码失败"
    }
  }

  // stage("扫描代码"){
  //   cmd.SonarCode(cfg)
  // }

  // stage("构建代码"){
  //   cmd.BuildCode(cfg)
  // }

  stage("制作镜像"){
    if(!cmd.MakeImage(cfg)){
      error "制作镜像失败"
    }
  }

  stage("发布镜像"){
    if(!cmd.PublishImage(cfg)){
      error "发布镜像失败"
    }
  }
}

return this