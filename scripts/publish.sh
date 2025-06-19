#!/bin/bash

# 自习座位预约系统 Docker 镜像发布脚本
# 使用方法: ./scripts/publish.sh [版本号] [仓库地址]

set -e

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 参数处理
VERSION=${1:-"latest"}
REGISTRY=${2:-""}

# 加载环境变量
if [ -f "$PROJECT_ROOT/.env" ]; then
    source "$PROJECT_ROOT/.env"
fi

# 设置默认值
DOCKER_REGISTRY=${REGISTRY:-${DOCKER_REGISTRY:-""}}
DOCKER_NAMESPACE=${DOCKER_NAMESPACE:-"studyseat"}

echo "================================================"
echo "发布自习座位预约系统到 Docker 仓库"
echo "版本: $VERSION"
echo "仓库: ${DOCKER_REGISTRY:-"Docker Hub"}"
echo "命名空间: $DOCKER_NAMESPACE"
echo "================================================"

# 检查Docker登录状态
check_docker_login() {
    if [ -n "$DOCKER_REGISTRY" ]; then
        log_info "检查 Docker 登录状态..."
        if ! docker info | grep -q "Username"; then
            log_warning "未登录到 Docker 仓库，请先登录"
            docker login "$DOCKER_REGISTRY"
        fi
    fi
}

# 构建镜像
build_images() {
    log_info "构建镜像..."
    "$SCRIPT_DIR/build.sh" "$VERSION"
}

# 标记镜像
tag_images() {
    log_info "标记镜像..."
    
    # 设置镜像名称
    if [ -n "$DOCKER_REGISTRY" ]; then
        BACKEND_IMAGE="$DOCKER_REGISTRY/$DOCKER_NAMESPACE/studyseat-backend"
        FRONTEND_IMAGE="$DOCKER_REGISTRY/$DOCKER_NAMESPACE/studyseat-frontend"
    else
        BACKEND_IMAGE="$DOCKER_NAMESPACE/studyseat-backend"
        FRONTEND_IMAGE="$DOCKER_NAMESPACE/studyseat-frontend"
    fi
    
    # 标记后端镜像
    docker tag "$BACKEND_IMAGE:$VERSION" "$BACKEND_IMAGE:latest"
    
    # 标记前端镜像
    docker tag "$FRONTEND_IMAGE:$VERSION" "$FRONTEND_IMAGE:latest"
    
    log_success "镜像标记完成"
}

# 推送镜像
push_images() {
    log_info "推送镜像到仓库..."
    
    # 推送后端镜像
    log_info "推送后端镜像..."
    docker push "$BACKEND_IMAGE:$VERSION"
    docker push "$BACKEND_IMAGE:latest"
    
    # 推送前端镜像
    log_info "推送前端镜像..."
    docker push "$FRONTEND_IMAGE:$VERSION"
    docker push "$FRONTEND_IMAGE:latest"
    
    log_success "所有镜像推送完成"
}

# 生成部署文档
generate_deployment_docs() {
    log_info "生成部署文档..."
    
    local docs_file="$PROJECT_ROOT/DOCKER_DEPLOYMENT.md"
    
    cat > "$docs_file" << EOF
# Docker 部署文档

## 镜像信息

- **后端镜像**: \`${BACKEND_IMAGE}:${VERSION}\`
- **前端镜像**: \`${FRONTEND_IMAGE}:${VERSION}\`
- **发布时间**: $(date '+%Y-%m-%d %H:%M:%S')

## 快速部署

### 1. 下载 docker-compose.yml

\`\`\`bash
wget https://raw.githubusercontent.com/your-repo/StudySeatReserve/main/docker-compose.yml
\`\`\`

### 2. 配置环境变量

\`\`\`bash
# 复制环境变量模板
wget https://raw.githubusercontent.com/your-repo/StudySeatReserve/main/env.example -O .env

# 编辑配置文件
vim .env
\`\`\`

### 3. 启动服务

\`\`\`bash
docker-compose up -d
\`\`\`

## 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| MYSQL_ROOT_PASSWORD | MySQL root密码 | root123456 |
| MYSQL_DATABASE | 数据库名 | study_seat_reserve |
| MYSQL_USER | 数据库用户 | studyseat |
| MYSQL_PASSWORD | 数据库密码 | studyseat123 |
| JWT_SECRET | JWT密钥 | 需要修改 |
| WX_APPID | 微信小程序AppID | 需要配置 |
| WX_SECRET | 微信小程序Secret | 需要配置 |

## 访问地址

- 前端访问: http://localhost:80
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/doc.html

## 常用命令

\`\`\`bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 更新镜像
docker-compose pull && docker-compose up -d
\`\`\`

## 注意事项

1. 首次启动会自动初始化数据库
2. 请确保修改默认密码
3. 生产环境建议配置SSL证书
4. 定期备份数据库数据

EOF

    log_success "部署文档已生成: $docs_file"
}

# 清理本地镜像（可选）
cleanup_local() {
    read -p "是否清理本地构建的镜像? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "清理本地镜像..."
        docker rmi "$BACKEND_IMAGE:$VERSION" "$BACKEND_IMAGE:latest" \
                   "$FRONTEND_IMAGE:$VERSION" "$FRONTEND_IMAGE:latest" 2>/dev/null || true
        log_success "本地镜像已清理"
    fi
}

# 主函数
main() {
    # 检查参数
    if [ "$1" = "help" ] || [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        echo "使用方法: $0 [版本号] [仓库地址]"
        echo ""
        echo "参数说明:"
        echo "  版本号    - Docker镜像版本标签 (默认: latest)"
        echo "  仓库地址  - Docker仓库地址 (可选)"
        echo ""
        echo "示例:"
        echo "  $0 v1.0.0"
        echo "  $0 v1.0.0 registry.example.com"
        echo "  $0 latest docker.io"
        exit 0
    fi
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装"
        exit 1
    fi
    
    # 执行发布流程
    check_docker_login
    build_images
    tag_images
    push_images
    generate_deployment_docs
    
    echo ""
    echo "================================================"
    echo "发布完成!"
    echo "后端镜像: $BACKEND_IMAGE:$VERSION"
    echo "前端镜像: $FRONTEND_IMAGE:$VERSION"
    echo "================================================"
    
    cleanup_local
}

# 执行主函数
main "$@" 