import com.laazua.lib.enums.ProjectType
import com.laazua.lib.pipelines.api.GoPipeline
import com.laazua.lib.pipelines.api.JavaPipeline
import com.laazua.lib.pipelines.api.PythonPipeline
import com.laazua.lib.pipelines.api.K8sPipeline
import com.laazua.lib.pipelines.web.VuePipeline
import com.laazua.lib.pipelines.web.ReactPipeline


/**
* runPipeline 运行流水线
* 
* 根据传入的枚举参数(项目类型),运行指定的流水线
* 
* @param projectType 具体项目类型
* @param config 项目配置
*
*/
def call(ProjectType projectType, Map config = [:]) {
    echo "=== 开始运行流水线 ==="

    // 默认配置,配置流水线本身相关参数,(参数命名规则 options 开头)
    def defaultConfig = [
        optionsTimeout: 60,                    // 构建超时时间
        optionsTimeoutUnit: 'MINUTES',         // 构建超时单位
        optionsDisableConcurrentBuilds: true,  // 禁止并发构建
        optionsBuildDiscarderNumToKeep: '3',   // 构建记录保留策略
        optionsRetryNum: 0,                    // 构建重试次数
        optionsSkipDefaultCheckout: false      // 跳过默认的 SCM checkout
    ]
    // 合并入口配置与默认配置
    def finalConfig = defaultConfig + config
    // 选择并加载流水线
    def pipeline = resolvePipeline(projectType, finalConfig)
    pipeline.execute()
}


def resolvePipeline(ProjectType projectType, Map config) {
    def pipelineMap = [
        // API 项目
        (ProjectType.API_GO)     : { -> new GoPipeline(this, config) },
        (ProjectType.API_JAVA)   : { -> new JavaPipeline(this, config) },
        (ProjectType.API_PYTHON) : { -> new PythonPipeline(this, config) },
        (ProjectType.API_K8S)    : { -> new K8sPipeline(this, config) },
        
        // Web 项目  
        (ProjectType.WEB_VUE)    : { -> new VuePipeline(this, config) },
        (ProjectType.WEB_REACT)  : { -> new ReactPipeline(this, config) }
    ]
    
    return pipelineMap[projectType]?.call() ?: error("不支持的项目类型, 检查传入的参数: ${projectType}")
}