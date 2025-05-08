# 自习座位预约系统 - 微信小程序前端

## 项目介绍

自习座位预约系统的微信小程序前端，用于学生浏览自习室、预约座位、管理个人预约等功能。

## 技术栈

- 微信小程序原生开发框架
- Promise风格API封装
- 组件化开发

## 目录结构

```
frontend/
├── components/           # 自定义组件
│   └── custom-button/    # 自定义按钮组件
├── images/               # 图片资源
├── pages/                # 页面文件
│   ├── index/            # 首页
│   ├── login/            # 登录页
│   ├── study-rooms/      # 自习室列表页
│   ├── seat-reservation/ # 座位预约页
│   ├── user-center/      # 用户中心页
│   └── notifications/    # 通知页面
├── utils/                # 工具类
│   └── request.js        # 网络请求封装
├── styles/               # 样式文件
├── store/                # 全局状态管理
├── app.js                # 应用程序逻辑
├── app.json              # 全局配置
├── app.wxss              # 全局样式
├── project.config.json   # 项目配置
└── sitemap.json          # 微信索引配置
```

## 安装和使用

1. 下载并安装[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)

2. 克隆本仓库
   ```
   git clone <repository-url>
   ```

3. 在微信开发者工具中导入项目
   - 选择项目根目录中的frontend文件夹
   - 使用测试号或申请的小程序AppID（需要在project.config.json中更新）

4. 开发环境配置
   - 在app.js中配置后端API地址（默认为http://localhost:8080）
   - 确保网络请求域名已在微信小程序后台配置（测试环境可关闭域名校验）

## 功能模块

- **用户认证**：微信授权登录、用户信息获取
- **自习室浏览**：查看自习室列表、自习室详情、筛选搜索
- **座位预约**：选择座位、时间段预约、取消和延长预约
- **签到系统**：扫码签到、手动输入签到码
- **个人中心**：预约记录、收藏管理、个人设置
- **通知系统**：系统通知、预约提醒

## 开发规范

- 组件化：公共UI元素应封装为组件
- 样式规范：使用app.wxss中定义的全局变量和类
- 请求处理：使用utils/request.js中的封装方法
- 命名规范：
  - 文件名：kebab-case（如custom-button）
  - 变量和函数：camelCase（如userName）
  - 常量：UPPER_CASE（如MAX_COUNT）

## 测试

在微信开发者工具中使用自带的调试工具和模拟器进行测试。请确保测试以下场景：

- 不同设备尺寸的适配性
- 网络请求成功/失败的处理
- 用户授权流程
- 页面跳转和数据传递

## 注意事项

- 微信小程序对网络请求有域名限制，正式环境需要配置SSL证书和域名备案
- 存储敏感信息时注意使用适当的加密措施
- 账号权限和页面访问控制需要同时在前端和后端实现 