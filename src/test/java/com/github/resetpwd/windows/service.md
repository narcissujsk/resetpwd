JavaService.exe -install ECService "%JAVA_HOME%/jre/bin/server/jvm.dll" -Xmx128m -Djava.class.path="%JAVA_HOME%/lib/tools.jar;c:/ECService/ec.jar" -start com.ecservice.ECService -method StartService -stop com.ecservice.ECService -method StopService -out "%CD%/out.log" -err "%CD%/err.log" -current "%CD%" -auto

JavaService.exe -install ECService "%JAVA_HOME%/jre/bin/server/jvm.dll" -Xmx128m -Djava.class.path="%JAVA_HOME%/lib/tools.jar;c:/ECService/ec.jar" -start com.ecservice.ECService -method StartService -stop com.ecservice.ECService -method StopService -out "%CD%/out.log" -err "%CD%/err.log" -current "%CD%" -auto


说明：

1. -install SE : 是你要发布服务的名称；


2. 你系统环境中设置JAVA_HOME，指定你所需要使用的jre；


3. 在-Djava.class.path中指定你需要运行的jar，这里有个需要注意的地方后面会提到；


4. 设置jvm的基本参数，主要就是内存的分配；


5. 指定信息文件和异常文件，及路径；

%JAVA_HOME%与环境变量一致，%CD%为当前的目录路径，参数-start 指定类ECService的路径com.ecservice.ECService，-method指定类中要调用的方法名StartService，-auto代表服务开机自动启动。
 

JavaService一共提供了8个参数可供选择，其中我们只需要关心安装NT服务的-install参数和卸载NT服务的-uninstall参数。
使用-install参数安装NT服务时还需要提供与服务相关的其它一些参数，其命令格式如下：
JavaService -install service_name jvm_library [jvm_options]
        -start start_class [-method start_method] [-params (start_parameters)]
        [-stop start_class [-method stop_method] [-params (stop_parameters)]]
        [-out out_log_file] [-err err_log_file]
        [-current current_dir]
        [-path extra_path]
        [-depends other_service]
        [-auto | -manual]
        [-shutdown seconds]
        [-user user_name -password password]
        [-append | -overwrite]
        [-startup seconds]
        [-description service_desc]


相关参数的作用说明如下：
service_name: The name of the service.
 jvm_library:  The location of the JVM DLL used to run the service.
 jvm_option:   An option to use when starting the JVM, such as:
                       "-Djava.class.path=c:/classes" or "-Xmx128m".
 start_class:  The class to load when starting the service.
 start_method: The method to call in the start_class. default: main
 start_parameters:Parameter(s) to pass in to the start_method.
 stop_class:   The class to load when stopping the service.
 stop_method:  The method to call in the stop_class. default: main
 stop_parameters:      Parameter(s) to pass in to the stop_method.
 out_log_file: A file to redirect System.out into. (gets overwritten)
 err_log_file: A file to redirect System.err into. (gets overwritten)
 current_dir:  The current working directory for the service.
                       Relative paths will be relative to this directory.
 extra_path:   Path additions, for native DLLs etc. (no spaces)
 other_service:        Single service name dependency, must start first.
 auto / manual:        Startup automatic (default) or manual mode.
 seconds:      Java method processing time (startup:sleep, shutdown:timeout).
 user_name:    User specified to execute the service (user@domain).
 password:     Password applicable if user specified to run the service.
 append / overwrite:   Log file output mode, append (default) or overwrite.
 service_desc: Text describing installed service (quoted string, max 1024).
 
注册完毕后，执行启动服务命令
net start ECService

停止服务
net stop ECService

运行命令JavaService -uninstall ECService 可以删除掉服务。

删除服务：sc delete DYIMService(服务名)

JavaService.exe -install test "%JAVA_HOME%/jre/bin/server/jvm.dll" -Xmx128m -Djava.class.path="%JAVA_HOME%/lib/tools.jar;d:/resetpwd.main.jar" -start com.github.resetpwd.Main  -out "d:/out.log" -err "d:/err.log" -current "d:/" -auto

net start test

JavaService -uninstall test

sc delete test