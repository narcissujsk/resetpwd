mkdir C:\agent
set agentpath=%~dp0
copy %agentpath%\JavaService.exe C:\agent
copy %agentpath%\resetpwd.jar C:\agent
cd C:\agent
JavaService.exe -install test "%JAVA_HOME%/jre/bin/server/jvm.dll" -Xmx128m -Djava.class.path="%JAVA_HOME%/lib/tools.jar;c:/agent/resetpwd.jar" -start com.github.resetpwd.Main  -out "c:/agent/out.log" -err "c:/agent/err.log" 
net start test
pause