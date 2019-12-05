# narcissujsk
## jar
通过jar包运行java程序 Java -jar *.jar

 AES加密算法就是众多对称加密算法中的一种，它的英文全称是Advanced Encryption Standard，
 翻译过来是高级加密标准，它是用来替代之前的DES加密算法的。
 AES加密算法采用分组密码体制，每个分组数据的长度为128位16个字节，密钥长度可以是128位16个字节、192位或256位，
 一共有四种加密模式，我们通常采用需要初始向量IV的CBC模式，初始向量的长度也是128位16个字节。

安装服务
chmod +x resetPwdAgent
./resetPwdAgent install
./resetPwdAgent start
删除服务
./resetPwdAgent remove

修改wrapper.conf文件
主要修改下面几项
（1）JVM位置
wrapper.java.command=C:\jdk1.5.0_07\bin\java 或者 wrapper.java.command=%JAVA_HOME%/bin/java（需要在系统的环境变量里配置JAVA_HOME）
(2）MAIN CLASS此处决定了使用Java Service Wrapper的方式
wrapper.java.mainclass=org.tanukisoftware.wrapper.WrapperSimpleApp
（3）你的jar程序所需要的jar包必须全部在此表明
wrapper.java.classpath.1=../lib/JavaServiceTest.jar
wrapper.java.classpath.2=../lib/wrapper.jar
wrapper.java.classpath.3=../lib/sqljdbc4.jar
（4）你的Wrapper.DLL或wapper.jar所在的目录
 wrapper.java.library.path.1=../lib
（5）你的Java应用程序的主类
wrapper.app.parameter.1=com.test.JavaServiceTest
（6）注册为服务的名称和显示名，你可以随意进行设置
wrapper.name=testwrapper
wrapper.displayname= Test Wrapper Sample Application
（7）服务描述信息
 wrapper.description=Test Wrapper Sample Application Description
（8）服务的启动类型
wrapper.ntservice.starttype=AUTO_START
（9）按天生产日志
wrapper.logfile=../logs/wrapper.YYYYMMDD.log
wrapper.logfile.rollmode=DATE