# 自习座位预约系统

## 项目概述
这是一个基于微信小程序的自习座位预约系统，旨在提高高校自习室座位的使用率。系统分为学生端和管理员端，学生可以通过小程序预约自习座位，管理员可以管理自习室和座位资源。

## 系统架构
系统采用前后端分离的架构：
- 前端：微信小程序（学生端）+ Web管理系统（管理员端）
- 后端：Spring Boot + MyBatis-Plus API服务
- 数据库：MySQL
- 消息推送：微信订阅消息

## 环境要求
- 微信开发者工具
- JDK 11
- Maven 3.6+
- MySQL (v8.0+)
- 微信小程序账号
- 服务器（用于部署后端）
- IDE（如IntelliJ IDEA, Eclipse等）

## 部署指南

### 1. 后端部署

#### 1.1 数据库配置
1. 创建MySQL数据库：
```sql
CREATE DATABASE study_seat_reserve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入初始数据库结构：
```bash
mysql -u root -proot study_seat_reserve < database/init.sql
```

#### 1.2 后端服务配置
1. 配置application.yml文件：
```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/study_seat_reserve?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.studyseat.entity
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000

wechat:
  appid: your_wechat_appid
  secret: your_wechat_secret
```

2. 在IntelliJ IDEA或Eclipse中导入项目：
   - 打开IDE
   - 选择"导入项目"
   - 选择项目根目录下的pom.xml文件
   - 完成导入

3. 本地开发运行：
   - 在IDE中直接运行主应用类（通常为带有@SpringBootApplication注解的类）
   - 或使用Maven命令：
   ```bash
   mvn spring-boot:run
   ```

4. 打包部署：
```bash
mvn clean package
```
生成的JAR文件位于target目录下，可通过以下命令运行：
```bash
java -jar target/study-seat-reserve-0.0.1-SNAPSHOT.jar
```

### 2. 小程序前端部署

1. 在微信开发者工具中导入项目：
   - 打开微信开发者工具
   - 选择"导入项目"
   - 选择项目目录下的`miniprogram`文件夹
   - 填入AppID（微信小程序的AppID）

2. 修改API配置：
   打开`miniprogram/config/config.js`文件，修改API地址：
   ```javascript
   // 开发环境配置
   const dev = {
     apiUrl: 'http://localhost:8080/api',
   };
   
   // 生产环境配置
   const prod = {
     apiUrl: 'https://你的域名/api',
   };
   ```

3. 在开发者工具中预览、调试小程序

4. 上传小程序代码，提交审核并发布

### 3. Web管理系统部署

1. 安装依赖：
```bash
cd admin-web
npm install
```

2. 修改API配置：
打开`admin-web/src/config.js`文件，修改API地址：
```javascript
export const API_URL = 'http://localhost:8080/api';
```

3. 构建生产版本：
```bash
npm run build
```

4. 部署构建文件：
将`dist`目录下的文件部署到Web服务器（如Nginx）

## 前后端联调指南

### 1. 本地开发环境联调

1. 确保后端服务已启动（默认端口8080）
2. 修改小程序和管理系统的API地址为本地地址
3. 启动微信开发者工具，确保"不校验合法域名"选项被勾选
4. 使用微信开发者工具调试器和控制台查看请求和响应
5. 后端日志可在IDE控制台查看

### 2. 测试环境联调

1. 部署后端到测试服务器
2. 修改小程序和管理系统的API地址为测试服务器地址
3. 在微信开发者工具中开启"不校验合法域名"进行测试
4. 查看Spring Boot日志文件了解API调用情况

### 3. 生产环境配置

1. 在微信公众平台配置服务器域名：
   - 登录微信公众平台
   - 进入"开发"->"开发设置"
   - 在"服务器域名"中添加后端API的域名

2. 配置微信消息推送：
   - 在微信公众平台开通订阅消息能力
   - 创建消息模板（预约提醒、签到提醒等）
   - 在后端配置文件中更新模板ID

## 常见问题

1. 小程序无法连接后端API？
   - 检查API地址配置是否正确
   - 确认后端服务是否正常运行
   - 本地开发需勾选"不校验合法域名"
   - 线上环境需在微信公众平台配置域名白名单

2. 消息推送未收到？
   - 检查订阅消息模板配置
   - 确认用户是否授权接收消息
   - 查看后端日志中推送API的调用情况

3. 数据库连接问题？
   - 检查application.yml中的数据库配置
   - 确认MySQL服务是否正常运行
   - 检查用户名密码是否正确
   - 尝试使用数据库客户端工具测试连接

4. Spring Boot应用启动失败？
   - 检查JDK版本是否为11
   - 检查端口8080是否被占用
   - 查看启动日志排查具体错误

## 联系与支持

如有问题，请联系开发团队：
- 邮箱：support@example.com
- 问题反馈：https://github.com/your-repo/issues 