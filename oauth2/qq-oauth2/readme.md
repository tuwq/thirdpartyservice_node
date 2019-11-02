# QQ授权oauth2登录
## 1. 导入qqConnect_Server_SDK_java中的Sdk4j.jar至maven中
	mvn install:install-file -Dfile=F:/jar/Sdk4J.jar -DgroupId=com.sdk4j -DartifactId=sdk4j -Dversion=1.0 -Dpackaging=jar
## 2. 修改qqconnectconfig.properties中与QQ相关的配置
## 3. 注意回调地址匹配,否则会造成accessToken为空的情况
## 4. 更多查看官方例子qqConnect_Server_SDK_java