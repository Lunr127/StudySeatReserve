@echo off
echo 正在启动Spring Boot应用进行测试...
echo 按Ctrl+C可以停止测试

java -jar target\reserve-0.0.1-SNAPSHOT.jar --spring.profiles.active=test 