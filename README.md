# 自习座位预约系统

## 项目概述
这是一个基于微信小程序的自习座位预约系统，旨在提高高校自习室座位的使用率。系统分为学生端和管理员端，学生可以通过小程序预约自习座位，管理员可以管理自习室和座位资源。

## 系统功能
- **学生端功能**：
  - 账号登录/微信登录
  - 浏览自习室信息和座位状态
  - 预约座位（指定日期和时段）
  - 扫码/输码签到与签退
  - 取消预约
  - 收藏自习室和座位
  - 查看个人预约历史
  - 接收预约提醒和通知

- **管理员端功能**：
  - 自习室管理（增删改查）
  - 座位资源管理
  - 生成每日签到码
  - 查看预约数据统计
  - 处理违约记录
  - 发布系统通知

## 系统架构
系统采用前后端分离的架构：
- **前端**：微信小程序（学生端）+ Web管理系统（管理员端）
- **后端**：Spring Boot 2.7 + MyBatis-Plus 3.5 API服务
- **数据库**：MySQL 8.0
- **认证**：JWT + Spring Security
- **文档**：Knife4j（Swagger增强）
- **消息推送**：微信订阅消息

## 项目结构
```
StudySeatReserve/
├── frontend/                # 微信小程序前端
│   ├── components/          # 自定义组件
│   ├── images/              # 图片资源
│   ├── pages/               # 页面文件
│   │   ├── index/          # 首页
│   │   ├── login/          # 登录页
│   │   ├── study-rooms/    # 自习室列表页
│   │   ├── seat-reservation/# 座位预约页
│   │   ├── user-center/    # 用户中心
│   │   ├── admin/          # 管理功能
│   │   └── notifications/  # 通知中心 
│   ├── utils/               # 工具类
│   ├── app.js               # 应用程序逻辑
│   └── app.json             # 全局配置
├── backend/                 # Spring Boot后端
│   ├── src/                 # 源代码
│   │   ├── main/java        # Java代码
│   │   └── main/resources   # 配置文件
│   └── pom.xml              # Maven配置
├── init.sql                 # 数据库初始化脚本
└── README.md                # 项目文档
```

## 环境要求
- **开发环境**：
  - 微信开发者工具（最新版）
  - JDK 11
  - Maven 3.6+
  - MySQL 8.0+
  - IDE（如IntelliJ IDEA, Eclipse等）
- **生产环境**：
  - 微信小程序账号
  - 支持Java的Web服务器
  - MySQL数据库服务
  - 域名和SSL证书（小程序要求）

## 部署指南

### 1. 后端部署

#### 1.1 数据库配置
1. 创建MySQL数据库：
```sql
CREATE DATABASE study_seat_reserve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入初始数据库结构：
```bash
mysql -u username -p study_seat_reserve < init.sql
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
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.studyseat.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: is_deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

knife4j:
  enable: true
  basic:
    enable: false
  setting:
    language: zh-CN
    enableSwaggerModels: true
    enableDocumentManage: true
    swaggerModelName: 实体类列表
    enableVersion: false

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24小时有效期
  token-prefix: "Bearer "
  header: "Authorization"

wechat:
  appid: your_wechat_appid
  secret: your_wechat_secret
  # 订阅消息模板ID
  templates:
    reservation-notify: template_id_for_reservation  # 预约成功通知
    checkin-reminder: template_id_for_checkin        # 签到提醒
    violation-notify: template_id_for_violation      # 违约通知
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

### 1.3 数据库设计说明

系统使用MySQL数据库，主要包括以下表：
- `user` - 用户基本信息表
- `admin` - 管理员信息表 
- `student` - 学生信息表
- `study_room` - 自习室信息表
- `seat` - 座位信息表
- `reservation` - 预约信息表
- `check_in` - 签到记录表
- `violation` - 违约记录表
- `notification` - 通知消息表
- `favorite` - 收藏表
- `check_code` - 签到码表
- `system_param` - 系统参数表

表结构详见 `init.sql` 文件。

### 1.4 API文档

项目集成了Knife4j（Swagger增强），启动后端服务后，访问以下地址查看API文档：
```
http://localhost:8080/doc.html
```

主要API接口分组：
- 用户认证 - `/api/auth/**`
- 学生功能 - `/api/student/**`
- 自习室管理 - `/api/study-room/**` 
- 座位管理 - `/api/seat/**`
- 预约管理 - `/api/reservation/**`
- 签到管理 - `/api/check-in/**`
- 通知消息 - `/api/notification/**`

### 2. 小程序前端部署

1. 在微信开发者工具中导入项目：
   - 打开微信开发者工具
   - 选择"导入项目"
   - 选择项目目录下的`frontend`文件夹
   - 填入AppID（微信小程序的AppID）

2. 修改API配置：
   打开`frontend/utils/request.js`文件，修改API地址：
   ```javascript
   // 开发环境 API 基础地址
   const BASE_URL = 'http://localhost:8080/api';
   
   // 生产环境需修改为
   // const BASE_URL = 'https://your-domain.com/api';
   ```

3. 在开发者工具中预览、调试小程序

4. 上传小程序代码，提交审核并发布

### 3. Web管理系统部署

当前项目暂未实现单独的Web管理系统，管理功能集成在小程序内。若未来计划实现Web管理系统，可按以下步骤进行：

1. 安装依赖：
```bash
cd admin-web # 未来会创建此目录
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

## 开发指南

### 微信登录流程
1. 前端调用 `wx.login()` 获取临时登录凭证 code
2. 将 code 发送到后端 `/api/auth/wx-login` 接口
3. 后端通过微信API换取用户 openid
4. 根据 openid 查找或创建用户，生成JWT令牌返回
5. 前端存储令牌用于后续请求身份验证

### 座位预约流程
1. 用户浏览自习室列表，选择特定自习室
2. 系统展示座位布局和实时可用状态
3. 用户选择座位和时间段
4. 提交预约请求到 `/api/reservation/create`
5. 系统验证座位可用性并创建预约记录
6. 发送预约成功通知

### 签到流程
1. 用户到达自习室后，打开小程序的签到页面
2. 选择签到方式：扫码或手动输入签到码
3. 系统验证签到信息并记录签到时间
4. 更新预约状态为"使用中"
5. 用户离开时进行签退操作（可选）

## 进阶功能

### 座位推荐系统
根据用户的历史习惯和当前自习室容量情况，系统可以智能推荐最适合的座位，考虑因素包括：
- 用户历史选择偏好（靠窗、有电源等）
- 当前自习室人流密度
- 剩余可用时间
- 周围座位的预约情况

### 违约管理机制
系统对违约行为（未签到、提前离开等）进行记录和惩罚：
- 违约记录自动生成
- 违约次数达到阈值后，暂时限制用户预约权限
- 管理员可审核申诉并调整违约记录

### 数据统计分析
系统提供自习室使用情况的统计分析功能：
- 各时段使用率热力图
- 不同自习室的受欢迎程度比较
- 学生使用习惯分析
- 季节性、周期性使用模式识别

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

4. 签到码无法生成或验证？
   - 检查系统时钟是否同步
   - 验证签到码的生成逻辑
   - 确认自习室和签到码关联是否正确

5. Spring Boot应用启动失败？
   - 检查JDK版本是否为11
   - 检查端口8080是否被占用
   - 查看启动日志排查具体错误

## 项目维护

### 版本更新
- 后端API版本通过URL路径区分（如 `/api/v1/*`、`/api/v2/*`）
- 小程序更新需遵循微信审核流程
- 数据库结构变更需编写迁移脚本

### 性能优化
- 对热点查询使用缓存（如Redis）
- 使用批量操作减少数据库访问次数
- 定期执行数据库维护（清理过期数据、优化索引）
- 使用消息队列处理异步任务（如通知推送）

## 联系与支持

如有问题，请联系开发团队：
- 邮箱：support@studyseat.example.com
- 问题反馈：https://github.com/your-username/StudySeatReserve/issues 
- 文档Wiki：https://github.com/your-username/StudySeatReserve/wiki