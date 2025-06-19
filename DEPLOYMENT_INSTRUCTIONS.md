# StudySeat 系统部署说明

## 镜像信息
- **后端镜像**: `xinghanli24/studyseat-backend:v1.0.0`
- **前端镜像**: `xinghanli24/studyseat-frontend:v1.0.0`
- **发布时间**: 2025-06-19 21:17:33

## 快速部署

### 1. 创建 docker-compose.yml 文件

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: studyseat-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: study_seat_reserve
      MYSQL_USER: studyseat
      MYSQL_PASSWORD: studyseat123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    container_name: studyseat-redis
    restart: unless-stopped
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes --requirepass redis123456

  backend:
    image: xinghanli24/studyseat-backend:v1.0.0
    container_name: studyseat-backend
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
      MYSQL_DATABASE: study_seat_reserve
      MYSQL_USERNAME: studyseat
      MYSQL_PASSWORD: studyseat123
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: redis123456
      JWT_SECRET: your-jwt-secret-change-this
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis

  frontend:
    image: xinghanli24/studyseat-frontend:v1.0.0
    container_name: studyseat-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 2. 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 访问应用

- 前端地址: http://localhost:80
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html

## 环境变量配置

重要：请修改以下默认配置以确保安全：

- `MYSQL_ROOT_PASSWORD`: MySQL root 密码
- `MYSQL_PASSWORD`: 应用数据库密码  
- `REDIS_PASSWORD`: Redis 密码
- `JWT_SECRET`: JWT 密钥（生产环境必须修改）

## 常用管理命令

```bash
# 停止服务
docker-compose down

# 更新镜像
docker-compose pull
docker-compose up -d

# 备份数据库
docker exec studyseat-mysql mysqldump -u root -p study_seat_reserve > backup.sql

# 查看容器资源使用
docker stats
```

