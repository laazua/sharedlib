// 凭证解析
import com.laazua.lib.enums.CredentialsType


def call(CredentialsType credentialsType, String credentialsId) {
    // 获取凭证内容
    def credentialsMap = [
        // 密码类型凭证
        (credentialsType.PASSWD)        :   {   -> withCredentials([
                                                        usernamePassword(
                                                            credentialsId: credentialsId, 
                                                            usernameVariable: 'USERNAME', 
                                                            passwordVariable: 'PASSWORD'
                                                        )
                                                    ]) {
                                                        return [username: USERNAME, password: PASSWORD]
                                                    } 
                                            },
        // Secret Text 类型凭证
        (credentialsType.SECRETKEY)     :   {   -> withCredentials([
                                                        string(
                                                            credentialsId: credentialsId,
                                                            variable: 'API_KEY'
                                                        )
                                                    ]) {
                                                        return [apiKey: API_KEY]
                                                    }
                                            }
    ]

    return credentialsMap[credentialsType]?.call() ?: error("不支持的凭证类型, 检查传入的参数: ${credentialsType}")
}


// def credentials = [
//     [
//         $class: 'UsernamePasswordBinding',
//         credentialsId: credentialsId,
//         usernameVariable: 'USERNAME',
//         passwordVariable: 'PASSWORD'
//     ],
//     [
//         $class: 'StringBinding',
//         credentialsId: credentialsId,
//         variable: 'API_KEY']
// ]