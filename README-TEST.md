# macOS 测试脚本使用说明

本项目提供了适合 macOS 系统的测试脚本 `test-macos.sh`，用于自动运行单元测试、生成测试报告和代码覆盖率报告。

## 前提条件

1. **Java 环境**：确保已安装 Java 11 或更高版本
2. **Maven 环境**：确保已安装 Maven 3.6 或更高版本

检查命令：
```bash
java -version
mvn -version
```

## 使用方法

### 1. 在项目根目录下运行

```bash
cd /path/to/StudySeatReserve
./test-macos.sh
```

### 2. 脚本功能

脚本会自动执行以下步骤：

1. ✅ **环境检查**：验证 Java 和 Maven 环境
2. 📁 **目录切换**：自动进入 backend 目录
3. 🧹 **清理编译**：执行 `mvn clean compile`
4. 🧪 **运行测试**：执行 `mvn test -Dspring.profiles.active=test`
5. 📊 **生成测试报告**：执行 `mvn surefire-report:report`
6. 📈 **生成覆盖率报告**：执行 `mvn jacoco:report`
7. 📋 **显示结果摘要**：展示测试统计信息
8. 🌐 **打开报告**：询问是否在浏览器中打开报告

### 3. 输出文件

执行完成后，会在 `backend/target/site/` 目录下生成：

- **测试报告**：`surefire-report.html`
- **覆盖率报告**：`jacoco/index.html`

## 已修复的问题

### 1. JWT 配置问题
- 在测试配置文件 `application-test.yml` 中添加了 JWT 相关配置
- 确保测试环境下 `JwtUtil` Bean 能正常创建

### 2. 测试环境配置
- 添加了微信小程序测试配置
- 添加了系统自定义配置
- 使用 H2 内存数据库进行测试

## 故障排除

### 如果遇到权限问题：
```bash
chmod +x test-macos.sh
```

### 如果遇到路径问题：
确保在 `StudySeatReserve` 根目录下执行脚本

### 如果遇到 Java/Maven 环境问题：
确保已正确安装并配置环境变量

## 对比原 Windows 脚本

相比于 `start-test.bat`，macOS 脚本的主要改进：

1. **跨平台兼容**：使用 bash shell 脚本
2. **智能检查**：自动验证环境和目录
3. **友好提示**：使用 emoji 和彩色输出
4. **交互体验**：询问是否打开报告
5. **错误处理**：完善的错误检查和退出机制
6. **环境适配**：无需手动设置 JAVA_HOME

## 联系支持

如果遇到问题，请检查：
1. Java 和 Maven 版本是否符合要求
2. 项目依赖是否完整下载
3. 网络连接是否正常（Maven 下载依赖需要） 


# 自习座位预约系统测试文档

## 目录

1. [测试概述](#测试概述)
2. [测试环境](#测试环境)
3. [测试策略](#测试策略)
4. [单元测试](#单元测试)
   - [用户认证模块](#用户认证模块单元测试)
   - [自习室管理模块](#自习室管理模块单元测试)
   - [座位管理模块](#座位管理模块单元测试)
   - [预约系统模块](#预约系统模块单元测试)
   - [签到系统模块](#签到系统模块单元测试)
   - [通知系统模块](#通知系统模块单元测试)
   - [个人中心模块](#个人中心模块单元测试)
   - [收藏与快速预约模块](#收藏与快速预约模块单元测试)
5. [集成测试](#集成测试)
   - [用户认证模块](#用户认证模块集成测试)
   - [自习室管理模块](#自习室管理模块集成测试)
   - [座位管理模块](#座位管理模块集成测试)
   - [预约系统模块](#预约系统模块集成测试)
   - [签到系统模块](#签到系统模块集成测试)
   - [通知系统模块](#通知系统模块集成测试)
   - [个人中心模块](#个人中心模块集成测试)
   - [收藏与快速预约模块](#收藏与快速预约模块集成测试)
6. [端到端测试](#端到端测试)
7. [性能测试](#性能测试)
8. [测试报告](#测试报告)
9. [测试总结](#测试总结)

## 测试概述

本文档详细记录了自习座位预约系统各模块的测试过程，包括测试策略、测试用例设计、测试实现和测试结果。测试覆盖了系统的所有已完成模块，确保系统功能正确、可靠、安全。

## 测试环境

### 开发环境

- JDK 11
- Spring Boot 2.7.14
- MyBatis-Plus 3.5.3.1
- MySQL 8.0.40
- H2 Database (测试用)
- JUnit 5
- Mockito 3.6.28

### 测试环境

- 本地开发环境
- H2内存数据库 (单元测试)
- MySQL测试数据库 (集成测试)

## 测试策略

### 测试类型

1. **单元测试**：测试各模块的Service层和工具类的功能正确性
2. **集成测试**：测试Controller层接口和多个组件的交互
3. **端到端测试**：测试完整业务流程
4. **性能测试**：测试系统在高负载下的性能表现

### 测试框架和工具

- JUnit 5：单元测试框架
- Mockito：模拟依赖组件
- Spring Test：Spring集成测试
- H2数据库：测试数据存储
- JMeter：性能测试

### 测试覆盖率目标

- 单元测试：代码覆盖率 > 80%
- 集成测试：API覆盖率 > 90%
- 端到端测试：主要业务流程 100% 覆盖

## 单元测试

### 用户认证模块单元测试

#### 测试计划

1. 测试JWT工具类的功能
2. 测试用户登录服务
3. 测试微信授权登录服务
4. 测试用户信息获取服务

#### 测试实现

为用户认证模块创建以下测试类：

1. JwtUtilTest：测试JWT生成、验证、解析功能
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/util/JwtUtilTest.java`
   - 测试方法：
     - testGenerateToken：测试生成JWT令牌
     - testGetUsernameFromToken：测试从令牌中获取用户名
     - testGetUserIdFromToken：测试从令牌中获取用户ID
     - testGetUserTypeNumberFromToken：测试从令牌中获取用户类型（数字）
     - testGetUserTypeFromToken：测试从令牌中获取用户类型（字符串）
     - testValidateToken：测试验证令牌有效性
     - testIsTokenExpired：测试令牌过期检查
     - testGetTokenFromRequest：测试从HTTP请求中获取令牌
     - testGetUserIdFromRequest：测试从HTTP请求中获取用户ID

2. AuthServiceTest：测试用户登录和授权功能
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/service/AuthServiceTest.java`
   - 测试方法：
     - testLoginSuccess：测试成功登录
     - testLoginFailBadCredentials：测试错误凭据登录
     - testLoginFailUserNotFound：测试用户不存在的登录
     - testWxLoginExistingUser：测试已存在用户的微信登录
     - testWxLoginNewUser：测试新用户的微信登录（自动注册）
     - testWxLoginFailGetOpenId：测试获取OpenID失败的微信登录
     - testLogout：测试登出功能

#### 测试结果

用户认证模块的单元测试已全部通过，覆盖了JWT工具类和认证服务的所有主要功能。测试验证了：

1. JWT令牌的生成、验证和解析功能正常
2. 用户登录功能在各种场景下的行为正确
3. 微信授权登录功能在各种场景下的行为正确
4. 登出功能正常工作

### 自习室管理模块单元测试

#### 测试计划

1. 测试自习室服务的基本CRUD功能
2. 测试自习室高级查询功能
3. 测试自习室权限验证功能

#### 测试实现

为自习室管理模块创建以下测试类：

1. StudyRoomServiceTest：测试自习室服务功能
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/service/StudyRoomServiceTest.java`
   - 测试方法：
     - testCreateStudyRoom：测试创建自习室
     - testUpdateStudyRoom：测试更新自习室
     - testDeleteStudyRoom：测试删除自习室
     - testGetStudyRoomById：测试查询自习室详情
     - testPageStudyRoom：测试分页查询自习室
     - testUpdateStatus：测试更新自习室状态
     - testCheckPermissionAdmin：测试管理员权限验证
     - testCheckPermissionStudentForAllSchool：测试学生访问全校自习室的权限
     - testCheckPermissionStudentForDepartment：测试学生访问特定院系自习室的权限

#### 测试结果

自习室管理模块的单元测试已全部通过，覆盖了自习室服务的所有主要功能。测试验证了：

1. 自习室的创建、更新、删除和查询功能正常
2. 自习室的分页查询和条件筛选功能正常
3. 自习室状态管理功能正常
4. 自习室权限验证在各种场景下的行为正确

### 座位管理模块单元测试

#### 测试计划

1. 测试座位服务的基本CRUD功能
2. 测试座位高级查询功能
3. 测试座位状态管理功能
4. 测试座位批量生成功能

#### 测试实现

为座位管理模块创建以下测试类：

1. SeatServiceTest：测试座位服务功能
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/service/SeatServiceTest.java`
   - 测试方法：
     - testAddSeat：测试添加座位功能
     - testBatchAddSeats：测试批量添加座位功能
     - testUpdateSeat：测试更新座位信息功能
     - testUpdateSeatWithNonExistentId：测试更新不存在座位的情况
     - testUpdateSeatStatus：测试更新座位状态功能
     - testUpdateSeatStatusWithInvalidParams：测试更新座位状态参数无效的情况
     - testDeleteSeat：测试删除座位功能
     - testDeleteAllSeatsInRoom：测试删除自习室所有座位功能
     - testGetSeatsByRoomId：测试根据自习室ID查询座位功能
     - testQuerySeatsByCondition：测试条件查询座位功能
     - testGetSeatVOById：测试根据ID查询座位详情功能
     - testGenerateSeats：测试自动生成座位功能
     - testGenerateSeatsWithInvalidParams：测试生成座位参数无效的情况

2. SeatMapperTest：测试座位数据访问功能
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/mapper/SeatMapperTest.java`
   - 测试方法：
     - testInsertAndSelect：测试基本的插入和查询操作
     - testUpdate：测试更新操作
     - testDelete：测试删除操作
     - testBatchInsert：测试批量插入操作
     - testUpdateSeatStatus：测试更新座位状态操作
     - testQuerySeatsByCondition：测试高级查询功能
     - testGetSeatVOById：测试获取座位详情功能

#### 测试结果

座位管理模块的单元测试已全部通过，覆盖了座位服务和数据访问层的所有主要功能。测试验证了：

1. 座位的创建、更新、删除和查询功能正常
2. 座位的批量添加和自动生成功能正常
3. 座位状态管理功能正常
4. 座位条件查询功能正常，支持多条件筛选
5. 数据访问层正确处理与数据库的交互

在测试过程中发现的问题：
1. H2数据库在测试环境中需要特别配置MySQL兼容模式
2. 座位条件查询需要依赖自习室数据，测试时需要确保测试数据完整性

### 预约系统模块单元测试

#### 测试计划

1. 测试预约创建功能
2. 测试预约取消功能
3. 测试预约延长功能
4. 测试预约冲突检测功能
5. 测试预约状态管理功能

#### 测试实现

为预约系统模块创建以下测试类：

1. ReservationServiceTest：测试预约服务功能
2. ReservationConflictCheckerTest：测试预约冲突检测功能

#### 测试用例设计

##### ReservationServiceTest

- 测试创建预约
- 测试取消预约
- 测试延长预约
- 测试获取预约详情
- 测试获取学生当前预约
- 测试获取学生历史预约
- 测试获取座位预约情况
- 测试预约自动状态更新

##### ReservationConflictCheckerTest

- 测试无冲突预约
- 测试同一座位同一时间段冲突
- 测试同一座位部分时间段冲突
- 测试不同座位无冲突
- 测试学生同时段多个预约冲突

### 签到系统模块单元测试

#### 测试计划

1. 测试签到码生成功能
2. 测试签到验证功能
3. 测试签到状态管理功能
4. 测试违约自动处理功能

#### 测试实现

为签到系统模块创建以下测试类：

1. CheckCodeServiceTest：测试签到码服务
2. CheckInServiceTest：测试签到服务
3. ViolationProcessingServiceTest：测试违约处理服务

#### 测试用例设计

##### CheckCodeServiceTest

- 测试生成签到码
- 测试批量生成签到码
- 测试获取有效签到码
- 测试验证签到码有效性

##### CheckInServiceTest

- 测试扫码签到
- 测试手动输入编码签到
- 测试签退
- 测试获取签到记录
- 测试签到权限验证

##### ViolationProcessingServiceTest

- 测试超时未签到违约处理
- 测试提前离开违约处理
- 测试违约记录创建
- 测试违约统计

### 通知系统模块单元测试

#### 测试计划

1. 测试通知发送功能
2. 测试通知查询功能
3. 测试通知状态管理功能
4. 测试定时任务功能

#### 测试实现

为通知系统模块创建以下测试类：

1. NotificationServiceTest：测试通知服务
2. NotificationScheduleServiceTest：测试通知定时任务

#### 测试用例设计

##### NotificationServiceTest

- 测试发送系统通知
- 测试发送预约提醒
- 测试发送迟到提醒
- 测试发送违约通知
- 测试获取用户通知列表
- 测试标记通知为已读
- 测试删除通知

##### NotificationScheduleServiceTest

- 测试预约提醒定时任务
- 测试迟到提醒定时任务
- 测试过期通知清理任务

### 个人中心模块单元测试

#### 测试计划

1. 测试个人预约历史查询功能
2. 测试违约记录查询功能
3. 测试个人偏好设置功能

#### 测试实现

为个人中心模块创建以下测试类：

1. UserServiceExtendedTest：测试用户扩展服务
2. ViolationServiceTest：测试违约记录服务
3. UserPreferenceServiceTest：测试用户偏好服务

#### 测试用例设计

##### UserServiceExtendedTest

- 测试获取用户详细信息
- 测试更新用户信息

##### ViolationServiceTest

- 测试获取用户违约记录
- 测试获取用户违约统计

##### UserPreferenceServiceTest

- 测试获取用户偏好设置
- 测试更新用户偏好设置
- 测试创建默认偏好设置

### 收藏与快速预约模块单元测试

#### 测试计划

1. 测试收藏功能
2. 测试收藏查询功能
3. 测试取消收藏功能

#### 测试实现

为收藏与快速预约模块创建以下测试类：

1. FavoriteServiceTest：测试收藏服务

#### 测试用例设计

##### FavoriteServiceTest

- 测试添加收藏
- 测试获取用户收藏列表
- 测试删除收藏
- 测试检查是否已收藏

## 集成测试

### 用户认证模块集成测试

#### 测试计划

1. 测试登录接口
2. 测试微信授权接口
3. 测试获取用户信息接口

#### 测试实现

为用户认证模块创建以下集成测试类：

1. AuthControllerTest：测试认证控制器
2. UserControllerTest：测试用户控制器

#### 测试用例设计

##### AuthControllerTest

- 测试账号密码登录接口
- 测试微信授权登录接口
- 测试无效凭据登录

##### UserControllerTest

- 测试获取当前用户信息
- 测试未授权访问
- 测试授权访问

### 自习室管理模块集成测试

#### 测试计划

1. 测试自习室CRUD接口
2. 测试自习室查询接口

#### 测试实现

为自习室管理模块创建以下集成测试类：

1. StudyRoomControllerTest：测试自习室控制器

#### 测试用例设计

##### StudyRoomControllerTest

- 测试创建自习室API
- 测试更新自习室API
- 测试删除自习室API
- 测试获取自习室详情API
- 测试分页查询自习室API
- 测试权限控制

### 座位管理模块集成测试

#### 测试计划

1. 测试座位CRUD接口
2. 测试座位查询接口

#### 测试实现

为座位管理模块创建以下集成测试类：

1. SeatControllerTest：测试座位控制器
   - 文件路径：`backend/src/test/java/com/studyseat/reserve/controller/SeatControllerTest.java`
   - 测试方法：
     - testAddSeat：测试创建座位API
     - testBatchAddSeats：测试批量添加座位API
     - testGenerateSeats：测试自动生成座位API
     - testUpdateSeat：测试更新座位API
     - testUpdateSeatStatus：测试更新座位状态API
     - testDeleteSeat：测试删除座位API
     - testDeleteAllSeatsInRoom：测试删除自习室所有座位API
     - testGetSeatById：测试获取座位详情API
     - testGetSeatsByRoomId：测试按自习室ID查询座位API
     - testQuerySeatsByCondition：测试按条件筛选座位API
     - testNonPermissionAccess：测试无权限访问时的行为

#### 测试结果

座位管理模块的集成测试已全部通过，覆盖了所有API接口的功能。测试验证了：

1. 座位管理相关API接口的正确性
2. API权限控制的有效性
3. 参数验证和错误处理的正确性
4. 各种操作场景下的系统行为符合预期

实现集成测试过程中遇到的挑战：
1. Spring Security配置需要正确模拟，特别是CSRF保护
2. 需要适当模拟服务层依赖
3. JSON序列化和反序列化需要与实际请求一致

### 预约系统模块集成测试

#### 测试计划

1. 测试预约创建接口
2. 测试预约取消接口
3. 测试预约延长接口
4. 测试预约查询接口

#### 测试实现

为预约系统模块创建以下集成测试类：

1. ReservationControllerTest：测试预约控制器

#### 测试用例设计

##### ReservationControllerTest

- 测试创建预约API
- 测试取消预约API
- 测试延长预约API
- 测试获取预约详情API
- 测试获取当前预约API
- 测试获取历史预约API
- 测试权限控制

### 签到系统模块集成测试

#### 测试计划

1. 测试签到码生成接口
2. 测试签到验证接口
3. 测试签到状态管理接口

#### 测试实现

为签到系统模块创建以下集成测试类：

1. CheckCodeControllerTest：测试签到码控制器
2. CheckInControllerTest：测试签到控制器

#### 测试用例设计

##### CheckCodeControllerTest

- 测试获取签到码API
- 测试生成签到码API
- 测试权限控制

##### CheckInControllerTest

- 测试签到API
- 测试签退API
- 测试获取签到记录API
- 测试权限控制

### 通知系统模块集成测试

#### 测试计划

1. 测试通知发送接口
2. 测试通知查询接口
3. 测试通知状态管理接口

#### 测试实现

为通知系统模块创建以下集成测试类：

1. NotificationControllerTest：测试通知控制器

#### 测试用例设计

##### NotificationControllerTest

- 测试发送通知API
- 测试获取通知列表API
- 测试标记通知为已读API
- 测试删除通知API
- 测试权限控制

### 个人中心模块集成测试

#### 测试计划

1. 测试个人预约历史查询接口
2. 测试违约记录查询接口
3. 测试个人偏好设置接口

#### 测试实现

为个人中心模块创建以下集成测试类：

1. UserCenterControllerTest：测试个人中心控制器

#### 测试用例设计

##### UserCenterControllerTest

- 测试获取用户详情API
- 测试获取违约记录API
- 测试获取用户偏好设置API
- 测试更新用户偏好设置API
- 测试权限控制

### 收藏与快速预约模块集成测试

#### 测试计划

1. 测试收藏接口
2. 测试收藏查询接口
3. 测试取消收藏接口

#### 测试实现

为收藏与快速预约模块创建以下集成测试类：

1. FavoriteControllerTest：测试收藏控制器

#### 测试用例设计

##### FavoriteControllerTest

- 测试添加收藏API
- 测试获取收藏列表API
- 测试删除收藏API
- 测试权限控制

## 端到端测试

### 测试计划

1. 测试学生预约座位的完整流程
2. 测试管理员管理自习室的完整流程
3. 测试签到和违约处理的完整流程

### 测试用例设计

#### 学生预约座位流程测试

1. 学生登录
2. 浏览自习室列表
3. 选择特定自习室
4. 选择特定座位
5. 选择时间段
6. 确认预约
7. 查看预约详情
8. 签到使用
9. 结束使用

#### 管理员管理自习室流程测试

1. 管理员登录
2. 创建新自习室
3. 生成座位
4. 查看自习室列表
5. 查看座位详情
6. 修改座位状态
7. 查看预约情况
8. 查看签到情况

#### 签到和违约处理流程测试

1. 学生预约座位
2. 在预约时间内签到
3. 使用座位
4. 签退
5. 再次预约座位
6. 不进行签到（制造违约）
7. 查看违约记录

## 性能测试

### 测试计划

1. 测试系统在高并发预约场景下的性能
2. 测试系统在大量查询场景下的性能
3. 测试系统在大量签到场景下的性能

### 测试场景设计

#### 高并发预约场景

- 同时有100/500/1000个用户进行预约操作
- 记录响应时间、成功率、错误率
- 测试系统稳定性和正确性

#### 大量查询场景

- 同时有100/500/1000个用户查询自习室和座位
- 记录响应时间、查询性能
- 测试缓存效果和数据库性能

#### 大量签到场景

- 同时有100/500/1000个用户进行签到操作
- 记录响应时间、成功率
- 测试系统处理并发签到的能力

## 测试报告

### 发现的问题

| ID | 问题描述 | 严重性 | 模块 | 状态 |
| --- | --- | --- | --- | --- |
| BUG-001 | StudyRoomServiceTest 中的 floor 字段类型错误 | 中 | 自习室管理模块 | 已修复 |
| BUG-002 | StudyRoomServiceTest 中的方法名与实际实现不一致 | 低 | 自习室管理模块 | 已修复 |
| BUG-003 | SecurityUtil 类中缺少获取学生所属学院的方法 | 中 | 用户认证模块 | 已修复 |

## 测试总结

目前已完成用户认证模块、自习室管理模块和座位管理模块的单元测试，覆盖了这些模块的核心功能。测试过程中发现了一些问题，主要集中在方法命名和参数类型不一致上，这些问题已经得到修复。

座位管理模块的测试显示该模块设计良好，接口清晰，功能完整。特别是座位自动生成功能和条件查询功能表现出色，为用户提供了灵活的座位管理能力。

后续需要继续完成其他模块的单元测试，并开始进行集成测试和端到端测试。特别是预约系统和签到系统这两个核心模块，需要重点测试其功能正确性和性能表现。

在测试过程中，我们发现代码结构良好，各模块职责明确，这为测试工作提供了便利。同时，使用Mockito框架模拟依赖组件，使得单元测试能够独立进行，不受外部环境影响。为集成测试配置了H2内存数据库，提高了测试的独立性和效率。