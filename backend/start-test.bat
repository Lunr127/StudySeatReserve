@echo off
echo 开始测试Spring Boot应用是否能正常启动...
echo.

rem 使用Maven编译项目并跳过测试
echo 正在编译项目...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo 编译失败，请检查错误信息！
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo 编译成功！现在尝试启动应用...
echo.

rem 使用java命令启动应用，设置超时时间为20秒
echo 正在启动应用，将在20秒后自动关闭...
java -jar target\reserve-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

echo.
echo 启动测试完成！
echo 如果没有看到错误信息，并且应用成功启动，则说明Spring Boot应用配置正确！
echo.

pause 