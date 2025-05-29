@echo off
echo ==============================================
echo 自习座位预约系统测试脚本
echo ==============================================
echo.

REM 设置Java环境变量
set JAVA_HOME=D:\Java\JDK
set PATH=%JAVA_HOME%\bin;%PATH%

echo 当前Java版本:
java -version
echo.

echo 清理并编译项目...
call mvn clean compile
echo.

echo 运行单元测试...
call mvn test
echo.

echo 生成测试报告...
call mvn surefire-report:report
echo.

echo 测试报告已生成，路径: target/site/surefire-report.html
echo.

echo 生成代码覆盖率报告...
call mvn jacoco:report
echo.

echo 代码覆盖率报告已生成，路径: target/site/jacoco/index.html
echo.

echo ==============================================
echo 测试完成
echo ==============================================

REM 打开测试报告
start "" "target\site\surefire-report.html"

REM 打开覆盖率报告
start "" "target\site\jacoco\index.html"

pause 