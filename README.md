### 共享库

* **jenkins所需插件**
```
 - gitee
 - Global Pipeline Libraries
 - pipeline stage view
 - Pipeline Utility Steps
 - Deprecated Groovy Libraries
 - AnsiColor
```
* **定义的全局参数**
```
 - CodeAddress: 项目代码仓库(字符参数)
 - DomeName: 项目域名(字符参数)
 - HttpPath: http路由(wap项目, 字符参数)
 - HelmArgs: helm参数(api项目,文本参数)
 - 以上参数在定义流水线任务时创建
```
* **注意**
```
 - 将helm发布到k8s环境中时,对发布时采用的名称有要求
```

