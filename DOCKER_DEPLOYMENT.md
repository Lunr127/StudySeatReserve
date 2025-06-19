# 自习座位预约系统 Docker 部署指南

## 概览

本文档介绍如何使用 Docker 在任何预装了 Docker 的机器上快速部署自习座位预约系统。

## 系统架构

该系统包含以下组件：
- **MySQL 8.0**: 数据库服务
- **Redis 7**: 缓存服务
- **Spring Boot**: 后端API服务
- **Nginx**: 前端静态文件服务

## 快速开始

### 前置要求

- Docker 20.0+ 
- Docker Compose 2.0+
- 2GB+ 可用内存
- 5GB+ 可用磁盘空间

### 一键部署

```bash
# 1. 克隆项目
git clone <your-repository-url>
cd StudySeatReserve

# 2. 配置环境变量
cp env.example .env
vim .env  # 编辑配置

# 3. 启动服务
./scripts/deploy.sh start
```

### 详细步骤

#### 步骤 1: 环境准备

```bash
# 检查 Docker 版本
docker --version
docker-compose --version

# 确保 Docker 服务运行
docker info
```

#### 步骤 2: 配置环境变量

编辑 `.env` 文件，重要配置项：

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your-secure-password
MYSQL_DATABASE=study_seat_reserve
MYSQL_USER=studyseat
MYSQL_PASSWORD=your-db-password

# 应用配置
JWT_SECRET=your-jwt-secret-key
WX_APPID=your-wechat-appid
WX_SECRET=your-wechat-secret

# 端口配置
BACKEND_PORT=8080
FRONTEND_PORT=80
MYSQL_PORT=3306
```

#### 步骤 3: 启动服务

```bash
# 使用部署脚本
./scripts/deploy.sh start

# 或手动使用 docker-compose
docker-compose up -d
```

## 服务访问

启动成功后，可以通过以下地址访问：

- **前端服务**: http://localhost:80
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/doc.html (开发环境)
- **数据库**: localhost:3306

## 常用管理命令

### 服务管理

```bash
# 查看服务状态
./scripts/deploy.sh status

# 查看日志
./scripts/deploy.sh logs [服务名]

# 重启服务
./scripts/deploy.sh restart

# 停止服务
./scripts/deploy.sh stop
```

### Docker Compose 命令

```bash
# 查看服务状态
docker-compose ps

# 查看特定服务日志
docker-compose logs -f backend

# 重启特定服务
docker-compose restart backend

# 更新镜像
docker-compose pull
docker-compose up -d
```

## 数据管理

### 数据备份

```bash
# 备份数据库
docker exec studyseat-mysql mysqldump -u root -p study_seat_reserve > backup.sql

# 备份数据卷
docker run --rm -v studyseat_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```

### 数据恢复

```bash
# 恢复数据库
docker exec -i studyseat-mysql mysql -u root -p study_seat_reserve < backup.sql

# 恢复数据卷
docker run --rm -v studyseat_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /
```

## 镜像构建与发布

### 本地构建

```bash
# 构建所有镜像
./scripts/build.sh [版本号]

# 构建特定版本
./scripts/build.sh v1.0.0
```

### 发布到仓库

```bash
# 登录 Docker 仓库
docker login

# 发布镜像
./scripts/publish.sh [版本号] [仓库地址]

# 示例
./scripts/publish.sh v1.0.0 registry.example.com
```

## 生产环境部署

### 使用预构建镜像

```bash
# 下载生产配置
wget https://raw.githubusercontent.com/your-repo/StudySeatReserve/main/docker-compose.prod.yml

# 配置环境变量
cp env.example .env
# 编辑 .env 文件

# 启动服务
docker-compose -f docker-compose.prod.yml up -d
```

### 安全配置

1. **修改默认密码**
   ```bash
   # 修改所有默认密码
   MYSQL_ROOT_PASSWORD=complex-password
   MYSQL_PASSWORD=complex-password
   REDIS_PASSWORD=complex-password
   JWT_SECRET=long-random-string
   ```

2. **网络安全**
   ```bash
   # 仅暴露必要端口
   FRONTEND_PORT=80
   BACKEND_PORT=8080
   # 不要暴露数据库端口到外网
   ```

3. **SSL/TLS配置**
   - 配置反向代理（如 Nginx）
   - 使用 Let's Encrypt 证书
   - 强制 HTTPS 访问

## 监控与日志

### 服务监控

```bash
# 查看容器资源使用
docker stats

# 查看服务健康状态
docker-compose ps
```

### 日志管理

```bash
# 查看所有服务日志
./scripts/deploy.sh logs

# 查看特定服务日志
./scripts/deploy.sh logs backend

# 持续监控日志
docker-compose logs -f --tail=100
```

## 故障排除

### 常见问题

1. **服务启动失败**
   ```bash
   # 查看详细错误信息
   docker-compose logs backend
   
   # 检查端口冲突
   netstat -tulpn | grep :8080
   ```

2. **数据库连接失败**
   ```bash
   # 检查数据库服务状态
   docker-compose ps mysql
   
   # 测试数据库连接
   docker exec -it studyseat-mysql mysql -u root -p
   ```

3. **内存不足**
   ```bash
   # 查看内存使用
   docker stats --no-stream
   
   # 调整 JVM 内存参数
   JAVA_OPTS="-Xmx256m -Xms128m"
   ```

### 日志分析

```bash
# 后端应用日志
docker exec studyseat-backend tail -f /app/logs/studyseat.log

# Nginx 访问日志
docker exec studyseat-frontend tail -f /var/log/nginx/access.log

# MySQL 错误日志
docker exec studyseat-mysql tail -f /var/log/mysql/error.log
```

## 更新升级

### 滚动更新

```bash
# 拉取最新镜像
docker-compose pull

# 逐个重启服务
docker-compose up -d --no-deps backend
docker-compose up -d --no-deps frontend
```

### 版本回滚

```bash
# 指定版本回滚
VERSION=v1.0.0 docker-compose -f docker-compose.prod.yml up -d
```

## 性能优化

### 数据库优化

```bash
# 调整 MySQL 配置
# 编辑 mysql.conf 文件
innodb_buffer_pool_size=512M
max_connections=200
```

### 应用优化

```bash
# 调整 JVM 参数
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# 启用应用缓存
SPRING_PROFILES_ACTIVE=prod,cache
```

## 支持与反馈

如遇到问题，请：

1. 查看本文档的故障排除部分
2. 检查 GitHub Issues
3. 提交新的 Issue 并附上日志信息

---

**注意**: 生产环境部署前请务必：
- 修改所有默认密码
- 配置适当的安全策略
- 进行充分的测试
- 准备数据备份方案 