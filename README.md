# 自习座位预约系统

## 项目概述
这是一个基于微信小程序的自习座位预约系统，旨在提高高校自习室座位的使用率。系统分为学生端和管理员端，学生可以通过小程序预约自习座位，管理员可以管理自习室和座位资源。

## 开发环境初始化指南

### 新开发人员快速开始

#### 第一步：环境准备

1. **安装JDK 11**
   ```bash
   # 下载并安装JDK 11
   # 官方下载地址：https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
   # 验证安装
   java -version
   javac -version
   ```

2. **安装Maven 3.6+**
   ```bash
   # 下载并安装Maven
   # 官方下载地址：https://maven.apache.org/download.cgi
   # 配置环境变量MAVEN_HOME
   # 验证安装
   mvn -version
   ```

3. **安装MySQL 8.0+**
   ```bash
   # 下载并安装MySQL 8.0
   # 官方下载地址：https://dev.mysql.com/downloads/mysql/
   # 启动MySQL服务
   # Windows: net start mysql
   # Linux/Mac: sudo systemctl start mysql
   ```

4. **安装微信开发者工具**
   ```
   # 下载地址：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html
   # 安装后使用微信扫码登录
   ```

5. **安装IDE（推荐IntelliJ IDEA或VSCode）**
   ```
   # IntelliJ IDEA: https://www.jetbrains.com/idea/
   # VSCode: https://code.visualstudio.com/
   ```

#### 第二步：项目克隆和配置

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd StudySeatReserve
   ```

2. **数据库初始化**
   ```sql
   # 1. 登录MySQL
   mysql -u root -p
   
   # 2. 创建数据库
   CREATE DATABASE study_seat_reserve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 3. 退出MySQL客户端，导入初始数据
   exit
   mysql -u root -p study_seat_reserve < init.sql
   ```

3. **后端配置**
   ```bash
   # 进入后端目录
   cd backend
   
   # 复制配置文件模板并修改
   cp src/main/resources/application.yml.template src/main/resources/application.yml
   
   # 编辑application.yml，修改数据库连接信息
   # spring.datasource.username: 你的MySQL用户名
   # spring.datasource.password: 你的MySQL密码
   # spring.datasource.url: 根据实际情况修改端口和数据库名
   ```

4. **前端配置**
   ```bash
   # 进入前端目录
   cd frontend
   
   # 在微信开发者工具中打开前端项目
   # 项目路径：选择frontend文件夹
   # AppID：填入你的小程序AppID（测试可使用测试号）
   ```

#### 第三步：运行项目

1. **启动后端服务**
   ```bash
   # 在backend目录下
   cd backend
   
   # 使用Maven启动
   mvn clean spring-boot:run
   
   # 或者在IDE中运行ReserveApplication.java主类
   ```

2. **验证后端启动**
   ```bash
   # 访问API文档
   http://localhost:8080/doc.html
   
   # 访问健康检查
   http://localhost:8080/actuator/health
   ```

3. **启动前端小程序**
   ```
   # 在微信开发者工具中：
   # 1. 点击"编译"按钮
   # 2. 在模拟器中查看运行效果
   # 3. 确保网络连接正常，可以调用后端接口
   ```

#### 第四步：初始数据说明

系统已预置以下测试数据：

**管理员账号：**
- 用户名：admin，密码：123456（系统管理员）
- 用户名：roomadmin1，密码：123456（自习室管理员）

**学生账号：**
- 用户名：student1，密码：123456
- 用户名：student2，密码：123456

**测试自习室：**
- 图书馆1楼自习室（100个座位）
- 图书馆2楼自习室（80个座位）
- 实验楼A区自习室（60个座位）
- 教学楼B栋自习室（40个座位）

#### 第五步：开发注意事项

1. **数据库连接**
   - 确保MySQL服务正在运行
   - 检查防火墙设置是否阻止连接
   - 验证数据库用户权限

2. **小程序开发**
   - 需要在微信公众平台注册小程序账号
   - 开发阶段可使用测试AppID
   - 注意小程序域名白名单配置

3. **API调试**
   - 后端API文档地址：http://localhost:8080/doc.html
   - 使用Postman或内置工具测试接口
   - 注意JWT Token的获取和使用

4. **常见问题**
   - 端口冲突：修改application.yml中的server.port
   - 数据库连接失败：检查MySQL服务状态和连接参数
   - 小程序网络错误：检查小程序合法域名配置

## Docker 容器化部署

### 🐳 快速使用 Docker 部署

如果您想快速体验系统，推荐使用 Docker 部署方式：

```bash
# 1. 克隆项目
git clone <repository-url>
cd StudySeatReserve

# 2. 配置环境变量
cp env.example .env
# 编辑 .env 文件，修改微信小程序 AppID 等配置

# 3. 一键启动所有服务
./scripts/deploy.sh start
```

启动后访问：
- 前端: http://localhost:80
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html

详细的 Docker 部署说明请参考：[Docker 部署指南](DOCKER_DEPLOYMENT.md)

### Docker 相关命令

```bash
# 构建镜像
./scripts/build.sh [版本号]

# 启动服务
./scripts/deploy.sh start

# 查看状态
./scripts/deploy.sh status

# 查看日志
./scripts/deploy.sh logs [服务名]

# 停止服务
./scripts/deploy.sh stop

# 发布镜像
./scripts/publish.sh [版本号] [仓库地址]
```

## 编译和自动化测试流程

### 编译项目

#### 后端编译
```bash
# 进入后端目录
cd backend

# 清理并编译项目
mvn clean compile

# 打包项目（包含测试）
mvn clean package

# 跳过测试的打包（快速打包）
mvn clean package -DskipTests
```

#### 前端编译
```bash
# 微信小程序项目无需特殊编译步骤
# 在微信开发者工具中点击"编译"即可
```

### 自动化测试

#### 后端测试

1. **运行所有测试**
   ```bash
   cd backend
   mvn test
   ```

2. **运行特定测试类**
   ```bash
   # 运行用户服务测试
   mvn test -Dtest=UserServiceTest
   
   # 运行预约服务测试
   mvn test -Dtest=ReservationServiceTest
   ```

3. **生成测试覆盖率报告**
   ```bash
   mvn clean test jacoco:report
   # 查看报告：target/site/jacoco/index.html
   ```

4. **测试分类**
   - **单元测试**：Service层业务逻辑测试
   - **集成测试**：Controller层API接口测试
   - **数据库测试**：Mapper层数据访问测试

#### 测试数据库配置

项目使用H2内存数据库进行测试，配置文件位于：
```
backend/src/test/resources/application-test.yml
```

#### 快速测试脚本

提供了Windows批处理脚本快速运行测试：
```bash
# 简单测试（编译+基础测试）
cd backend
./simple-test.bat

# 完整测试（包含覆盖率报告）
./start-test.bat
```

#### 测试覆盖范围

当前测试覆盖以下模块：
- ✅ 用户认证服务测试
- ✅ 自习室管理服务测试  
- ✅ 座位管理服务测试
- ✅ 预约系统测试
- ✅ 签到系统测试
- ✅ 通知系统测试
- ✅ JWT工具类测试
- ✅ API接口集成测试

#### 持续集成建议

为了确保代码质量，建议：
1. 每次提交前运行完整测试套件
2. Pull Request必须通过所有测试
3. 维护测试覆盖率在80%以上
4. 定期更新测试数据和测试用例

### 性能测试

#### 数据库性能测试
```bash
# 使用JMeter或类似工具进行压力测试
# 测试场景：
# 1. 并发预约压力测试
# 2. 高频签到操作测试
# 3. 大量用户同时查询测试
```

#### API性能基准
- 用户登录：< 500ms
- 获取自习室列表：< 300ms  
- 创建预约：< 200ms
- 签到操作：< 150ms

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
- **前端**：微信小程序（学生端和管理员端）
- **后端**：Spring Boot 2.7.14 + MyBatis-Plus 3.5.3.1 REST API服务
- **数据库**：MySQL 8.0+ 
- **认证授权**：JWT + Spring Security
- **API文档**：Knife4j 2.0.9（Swagger增强）
- **消息推送**：微信订阅消息
- **定时任务**：Spring Task
- **数据库连接池**：Druid 1.2.16
- **JSON处理**：FastJSON 2.0.32
- **测试框架**：JUnit 5 + H2内存数据库
- **二维码生成**：自定义Canvas绘制

## 核心功能模块

### 已完成功能 ✅

#### 用户认证模块
- 微信小程序登录（OpenID认证）
- 传统账号密码登录  
- JWT令牌管理
- 用户权限控制（学生/管理员）

#### 自习室管理模块
- 自习室增删改查
- 自习室信息展示（名称、位置、容量、开放时间）
- 按条件筛选（建筑、归属、开放状态）
- 权限验证（管理员管理，学生只读）

#### 座位管理模块  
- 座位布局可视化展示
- 座位特殊属性标记（电源、靠窗、角落）
- 座位状态管理（正常/停用）
- 座位批量生成和管理

#### 预约系统模块
- 座位预约创建
- 预约时间段选择
- 预约冲突检测
- 预约取消和延长
- 预约状态管理（待签到/使用中/已完成/已违约）

#### 签到系统模块
- 扫码签到（二维码）
- 手动输入编码签到
- 每日签到码自动生成
- 签到权限验证
- 签退功能

#### 通知系统模块
- 预约提醒（开始前15分钟）
- 迟到提醒（开始后10分钟）
- 违约通知
- 系统通知
- 微信订阅消息集成

#### 个人中心模块
- 用户信息管理
- 预约历史查看
- 违约记录查询
- 收藏功能（自习室和座位）
- 个人偏好设置

#### 管理员功能模块
- 自习室和座位管理
- 签到码生成和管理
- 用户数据统计
- 通知推送管理

### 技术特色
- **前端组件化**：可复用的自定义组件（座位网格、通用按钮等）
- **后端分层架构**：Controller-Service-Mapper清晰分层
- **数据库设计**：支持软删除、乐观锁、逻辑分页
- **定时任务**：自动处理违约、生成签到码、清理过期数据
- **安全保障**：JWT认证、权限控制、参数验证
- **性能优化**：连接池、缓存机制、批量操作
- **用户体验**：现代化UI设计、实时状态更新、友好错误提示

## 项目结构
```
StudySeatReserve/
├── frontend/                    # 微信小程序前端
│   ├── components/              # 自定义组件
│   │   ├── custom-button/       # 通用按钮组件
│   │   └── seat-grid/           # 座位布局可视化组件
│   ├── images/                  # 图片资源
│   │   ├── icons/              # 功能图标
│   │   └── backgrounds/        # 背景图片
│   ├── pages/                   # 页面文件
│   │   ├── index/              # 首页
│   │   ├── login/              # 登录页
│   │   ├── study-rooms/        # 自习室相关页面
│   │   │   ├── study-rooms.*   # 自习室列表
│   │   │   └── detail/         # 自习室详情
│   │   ├── seat-reservation/   # 座位预约页
│   │   ├── time-selection/     # 时间段选择页
│   │   ├── reservation-confirm/ # 预约确认页
│   │   ├── check-in/           # 签到页面
│   │   ├── qr-code/            # 二维码展示页
│   │   ├── user-center/        # 用户中心
│   │   ├── my-reservations/    # 我的预约
│   │   ├── my-favorites/       # 我的收藏
│   │   ├── settings/           # 个人设置
│   │   ├── violation-records/  # 违约记录
│   │   ├── notifications/      # 通知中心
│   │   └── admin/              # 管理功能
│   │       ├── study-room-management/  # 自习室管理
│   │       ├── seat-management/        # 座位管理
│   │       └── check-code-management/  # 签到码管理
│   ├── utils/                   # 工具类
│   │   ├── api.js              # API调用封装
│   │   ├── request.js          # 网络请求工具
│   │   ├── auth.js             # 认证工具
│   │   └── qrcode.js           # 二维码生成工具
│   ├── styles/                  # 全局样式
│   ├── app.js                   # 应用程序逻辑
│   ├── app.json                 # 全局配置
│   └── app.wxss                 # 全局样式
├── backend/                     # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/studyseat/reserve/
│   │   │   │   ├── controller/     # 控制器层
│   │   │   │   │   ├── AuthController.java        # 认证接口
│   │   │   │   │   ├── UserController.java        # 用户接口
│   │   │   │   │   ├── StudyRoomController.java   # 自习室接口
│   │   │   │   │   ├── SeatController.java        # 座位接口
│   │   │   │   │   ├── ReservationController.java # 预约接口
│   │   │   │   │   ├── CheckInController.java     # 签到接口
│   │   │   │   │   ├── CheckCodeController.java   # 签到码接口
│   │   │   │   │   └── NotificationController.java # 通知接口
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   │   ├── impl/       # 服务实现类
│   │   │   │   │   └── ...         # 各种服务接口
│   │   │   │   ├── mapper/         # 数据访问层
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Admin.java
│   │   │   │   │   ├── Student.java
│   │   │   │   │   ├── StudyRoom.java
│   │   │   │   │   ├── Seat.java
│   │   │   │   │   ├── Reservation.java
│   │   │   │   │   ├── CheckIn.java
│   │   │   │   │   ├── CheckCode.java
│   │   │   │   │   ├── Notification.java
│   │   │   │   │   ├── Violation.java
│   │   │   │   │   ├── Favorite.java
│   │   │   │   │   ├── UserPreference.java
│   │   │   │   │   └── SystemParam.java
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── vo/             # 视图对象
│   │   │   │   ├── config/         # 配置类
│   │   │   │   │   ├── SecurityConfig.java    # 安全配置
│   │   │   │   │   ├── MyBatisPlusConfig.java # MyBatis配置
│   │   │   │   │   ├── CorsConfig.java        # 跨域配置
│   │   │   │   │   ├── SwaggerConfig.java     # API文档配置
│   │   │   │   │   └── ScheduleConfig.java    # 定时任务配置
│   │   │   │   ├── util/           # 工具类
│   │   │   │   │   ├── JwtUtil.java          # JWT工具
│   │   │   │   │   ├── WxApiUtil.java        # 微信API工具
│   │   │   │   │   └── DateUtil.java         # 日期工具
│   │   │   │   ├── common/         # 通用类
│   │   │   │   │   ├── Result.java           # 统一响应格式
│   │   │   │   │   └── Constants.java        # 常量定义
│   │   │   │   ├── exception/      # 异常处理
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── filter/         # 过滤器
│   │   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   │   └── ReserveApplication.java   # 启动类
│   │   │   └── resources/
│   │   │       ├── mapper/         # MyBatis XML映射文件
│   │   │       ├── application.yml # 主配置文件
│   │   │       └── logback-spring.xml # 日志配置
│   │   └── test/                   # 测试代码
│   │       ├── java/               # 测试类
│   │       │   └── com/studyseat/reserve/
│   │       │       ├── controller/ # 控制器测试
│   │       │       ├── service/    # 服务测试
│   │       │       ├── mapper/     # 数据访问测试
│   │       │       └── util/       # 工具类测试
│   │       └── resources/
│   │           └── application-test.yml # 测试配置
│   ├── pom.xml                     # Maven配置
│   ├── start-test.bat             # 测试脚本
│   └── simple-test.bat            # 简单测试脚本
├── init.sql                       # 数据库初始化脚本
├── progress.md                    # 开发进度文档
├── test_documentation.md          # 测试文档
├── image_description.md           # 图片资源说明
├── .gitignore                     # Git忽略文件
└── README.md                      # 项目文档
```

## 项目开发状态

### 当前版本：v1.0.0
### 开发进度：🟢 基本功能完成 (90%)

#### 已完成模块
- ✅ **用户认证系统** (100%) - 微信登录、账号登录、JWT认证
- ✅ **自习室管理** (100%) - 增删改查、权限控制、条件筛选  
- ✅ **座位管理** (100%) - 布局可视化、特殊属性、状态管理
- ✅ **预约系统** (100%) - 创建预约、冲突检测、状态管理
- ✅ **签到系统** (100%) - 扫码签到、手动输入、二维码生成
- ✅ **通知系统** (100%) - 预约提醒、迟到提醒、微信推送
- ✅ **个人中心** (100%) - 预约历史、违约记录、收藏功能
- ✅ **管理员功能** (95%) - 自习室管理、座位管理、签到码管理

#### 进行中模块
- 🔄 **数据分析模块** (30%) - 使用统计、报表生成
- 🔄 **系统管理** (40%) - 参数配置、日志管理

#### 待开发功能
- ⏳ **移动端适配优化** - 响应式布局改进
- ⏳ **高级搜索功能** - 多条件组合搜索
- ⏳ **批量操作** - 批量预约、批量管理
- ⏳ **数据导出** - Excel报表、使用分析

### 技术债务与改进计划
1. **性能优化**：数据库查询优化、缓存机制
2. **测试覆盖**：提升测试覆盖率至90%+
3. **文档完善**：API文档、部署文档
4. **监控告警**：系统监控、性能指标

### 已知问题
- [ ] 高并发预约可能存在竞态条件
- [ ] 长时间运行后内存使用需要优化
- [ ] 部分浏览器兼容性问题

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
- 管理员功能 - `/api/admin/**`

## 贡献指南

### 开发规范
1. **代码风格**：遵循阿里巴巴Java开发手册
2. **提交规范**：使用语义化提交信息
3. **分支策略**：Git Flow工作流
4. **测试要求**：新功能必须包含单元测试

### 提交流程
```bash
# 1. Fork项目并克隆
git clone <your-fork-url>
cd StudySeatReserve

# 2. 创建功能分支
git checkout -b feature/your-feature-name

# 3. 开发并测试
mvn test

# 4. 提交代码
git add .
git commit -m "feat: 添加新功能描述"

# 5. 推送并创建PR
git push origin feature/your-feature-name
```

### Pull Request要求
- [ ] 通过所有自动化测试
- [ ] 代码覆盖率不低于80%
- [ ] 包含详细的功能描述
- [ ] 更新相关文档

## 常见问题 (FAQ)

### Q1: 数据库连接失败
**A**: 检查以下项目：
- MySQL服务是否启动
- 数据库连接参数是否正确
- 防火墙是否开放3306端口
- 用户权限是否足够

### Q2: 小程序网络请求失败
**A**: 确认以下配置：
- 后端服务正在运行 (http://localhost:8080)
- 小程序开发工具中开启"不校验合法域名"
- 检查网络连接

### Q3: JWT令牌验证失败
**A**: 可能原因：
- 令牌已过期（默认24小时）
- 密钥配置错误
- 请求头格式不正确 (Bearer token)

### Q4: 测试运行失败
**A**: 解决方案：
```bash
# 清理并重新运行
mvn clean test

# 检查H2数据库配置
# 确保test-schema.sql和test-data.sql存在
```

### Q5: 签到二维码无法扫描
**A**: 检查步骤：
- 确保签到码已生成且有效
- 二维码是否包含正确的签到信息
- 检查二维码生成算法

### Q6: 性能问题
**A**: 优化建议：
- 启用数据库连接池监控
- 检查慢查询日志
- 增加适当的数据库索引
- 考虑引入Redis缓存

## 版本历史

### v1.0.0 (当前版本)
- ✅ 完成基础功能开发
- ✅ 实现用户认证系统
- ✅ 完成预约和签到流程
- ✅ 集成微信小程序

### 未来版本计划
- **v1.1.0**: 数据分析和报表功能
- **v1.2.0**: 高级搜索和批量操作  
- **v2.0.0**: 微服务架构改造

## 致谢
感谢以下开源项目的支持：
- Spring Boot
- MyBatis-Plus  
- Knife4j
- 微信小程序官方文档

## 许可证
本项目采用 [MIT License](LICENSE) 开源协议。

## 联系方式
如有问题或建议，请：
- 创建 [GitHub Issue](../../issues)
- 发送邮件至项目维护者
- 加入技术交流群
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