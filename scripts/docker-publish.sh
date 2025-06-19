#!/bin/bash

# StudySeat 项目 Docker 镜像发布脚本
# 使用方法: ./scripts/docker-publish.sh [版本号] [仓库用户名]

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 获取脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 参数处理
VERSION=${1:-"latest"}
DOCKER_USERNAME=${2:-""}

echo "================================================"
echo "StudySeat Docker 镜像发布工具"
echo "================================================"

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    log_error "Docker 未安装，请先安装 Docker"
    exit 1
fi

# 如果没有提供用户名，提示输入
if [ -z "$DOCKER_USERNAME" ]; then
    read -p "请输入您的 Docker Hub 用户名: " DOCKER_USERNAME
    if [ -z "$DOCKER_USERNAME" ]; then
        log_error "用户名不能为空"
        exit 1
    fi
fi

# 设置镜像名称
BACKEND_IMAGE="$DOCKER_USERNAME/studyseat-backend"
FRONTEND_IMAGE="$DOCKER_USERNAME/studyseat-frontend"

log_info "准备发布镜像："
echo "  - 后端镜像: $BACKEND_IMAGE:$VERSION"
echo "  - 前端镜像: $FRONTEND_IMAGE:$VERSION"
echo "  - 用户名: $DOCKER_USERNAME"
echo ""

# 检查 Docker 登录状态
log_info "检查 Docker 登录状态..."
if ! docker info | grep -q "Username"; then
    log_warning "未登录到 Docker Hub，请登录："
    docker login
    if [ $? -ne 0 ]; then
        log_error "Docker 登录失败"
        exit 1
    fi
fi

# 进入项目根目录
cd "$PROJECT_ROOT"

# 1. 构建后端应用
log_info "步骤 1: 构建后端 Spring Boot 应用..."
cd backend

if ! command -v mvn &> /dev/null; then
    log_error "Maven 未安装，请先安装 Maven"
    exit 1
fi

log_info "正在编译后端应用..."
mvn clean package -DskipTests

if [ ! -f target/reserve-*.jar ]; then
    log_error "后端应用构建失败，JAR 文件未生成"
    exit 1
fi

log_success "后端应用构建完成"

# 2. 构建 Docker 镜像
cd "$PROJECT_ROOT"
log_info "步骤 2: 构建 Docker 镜像..."

# 构建后端镜像
log_info "构建后端镜像..."
docker build -t "$BACKEND_IMAGE:$VERSION" -t "$BACKEND_IMAGE:latest" ./backend
if [ $? -ne 0 ]; then
    log_error "后端镜像构建失败"
    exit 1
fi

# 构建前端镜像
log_info "构建前端镜像..."
docker build -t "$FRONTEND_IMAGE:$VERSION" -t "$FRONTEND_IMAGE:latest" ./frontend
if [ $? -ne 0 ]; then
    log_error "前端镜像构建失败"
    exit 1
fi

log_success "Docker 镜像构建完成"

# 3. 推送镜像到 Docker Hub
log_info "步骤 3: 推送镜像到 Docker Hub..."

log_info "推送后端镜像..."
docker push "$BACKEND_IMAGE:$VERSION"
docker push "$BACKEND_IMAGE:latest"

log_info "推送前端镜像..."
docker push "$FRONTEND_IMAGE:$VERSION"
docker push "$FRONTEND_IMAGE:latest"

log_success "所有镜像推送完成！"

# 4. 生成部署说明
log_info "步骤 4: 生成部署文档..."
cat > "$PROJECT_ROOT/DEPLOYMENT_INSTRUCTIONS.md" << EOF
# StudySeat 系统部署说明

## 镜像信息
- **后端镜像**: \`${BACKEND_IMAGE}:${VERSION}\`
- **前端镜像**: \`${FRONTEND_IMAGE}:${VERSION}\`
- **发布时间**: $(date '+%Y-%m-%d %H:%M:%S')

## 快速部署

### 1. 创建 docker-compose.yml 文件

\`\`\`yaml
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
    image: ${BACKEND_IMAGE}:${VERSION}
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
    image: ${FRONTEND_IMAGE}:${VERSION}
    container_name: studyseat-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
\`\`\`

### 2. 启动服务

\`\`\`bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
\`\`\`

### 3. 访问应用

- 前端地址: http://localhost:80
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html

## 环境变量配置

重要：请修改以下默认配置以确保安全：

- \`MYSQL_ROOT_PASSWORD\`: MySQL root 密码
- \`MYSQL_PASSWORD\`: 应用数据库密码  
- \`REDIS_PASSWORD\`: Redis 密码
- \`JWT_SECRET\`: JWT 密钥（生产环境必须修改）

## 常用管理命令

\`\`\`bash
# 停止服务
docker-compose down

# 更新镜像
docker-compose pull
docker-compose up -d

# 备份数据库
docker exec studyseat-mysql mysqldump -u root -p study_seat_reserve > backup.sql

# 查看容器资源使用
docker stats
\`\`\`

EOF

log_success "部署文档已生成: DEPLOYMENT_INSTRUCTIONS.md"

echo ""
echo "================================================"
echo "🎉 发布完成！"
echo "================================================"
echo "镜像信息："
echo "  - 后端: $BACKEND_IMAGE:$VERSION"
echo "  - 前端: $FRONTEND_IMAGE:$VERSION"
echo ""
echo "其他人可以使用以下命令拉取镜像："
echo "  docker pull $BACKEND_IMAGE:$VERSION"
echo "  docker pull $FRONTEND_IMAGE:$VERSION"
echo ""
echo "部署说明文档: DEPLOYMENT_INSTRUCTIONS.md"
echo "================================================" 