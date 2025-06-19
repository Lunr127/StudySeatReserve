#!/bin/bash

# 自习座位预约系统 Docker 构建脚本
# 使用方法: ./scripts/build.sh [版本号]

set -e

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 设置默认版本
VERSION=${1:-latest}

# 加载环境变量
if [ -f "$PROJECT_ROOT/.env" ]; then
    echo "加载环境变量文件..."
    source "$PROJECT_ROOT/.env"
fi

# 设置默认值
DOCKER_REGISTRY=${DOCKER_REGISTRY:-""}
DOCKER_NAMESPACE=${DOCKER_NAMESPACE:-"studyseat"}

echo "================================================"
echo "开始构建自习座位预约系统 Docker 镜像"
echo "版本: $VERSION"
echo "注册表: ${DOCKER_REGISTRY:-"本地"}"
echo "命名空间: $DOCKER_NAMESPACE"
echo "================================================"

# 进入项目根目录
cd "$PROJECT_ROOT"

# 1. 构建后端应用
echo ""
echo "1. 构建后端 Spring Boot 应用..."
cd backend

# 检查Maven是否可用
if ! command -v mvn &> /dev/null; then
    echo "错误: Maven 未安装或不在 PATH 中"
    exit 1
fi

# 清理并打包
echo "正在编译和打包后端应用..."
mvn clean package -DskipTests

# 检查JAR文件是否生成
if [ ! -f target/reserve-*.jar ]; then
    echo "错误: JAR 文件未生成"
    exit 1
fi

echo "后端应用打包完成"

# 2. 构建Docker镜像
cd "$PROJECT_ROOT"

echo ""
echo "2. 构建 Docker 镜像..."

# 设置镜像标签
if [ -n "$DOCKER_REGISTRY" ]; then
    BACKEND_IMAGE="$DOCKER_REGISTRY/$DOCKER_NAMESPACE/studyseat-backend:$VERSION"
    FRONTEND_IMAGE="$DOCKER_REGISTRY/$DOCKER_NAMESPACE/studyseat-frontend:$VERSION"
else
    BACKEND_IMAGE="$DOCKER_NAMESPACE/studyseat-backend:$VERSION"
    FRONTEND_IMAGE="$DOCKER_NAMESPACE/studyseat-frontend:$VERSION"
fi

# 构建后端镜像
echo "构建后端镜像: $BACKEND_IMAGE"
docker build -t "$BACKEND_IMAGE" ./backend

# 构建前端镜像
echo "构建前端镜像: $FRONTEND_IMAGE"
docker build -t "$FRONTEND_IMAGE" ./frontend

echo ""
echo "================================================"
echo "Docker 镜像构建完成!"
echo "后端镜像: $BACKEND_IMAGE"
echo "前端镜像: $FRONTEND_IMAGE"
echo "================================================"

# 3. 显示镜像信息
echo ""
echo "镜像信息:"
docker images | grep "$DOCKER_NAMESPACE/studyseat"

echo ""
echo "构建完成! 可以使用以下命令启动服务:"
echo "docker-compose up -d" 