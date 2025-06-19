#!/bin/bash

# 自习座位预约系统 Docker 部署脚本
# 使用方法: ./scripts/deploy.sh [start|stop|restart|status|logs]

set -e

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
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

# 检查Docker和Docker Compose
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装或不在 PATH 中"
        exit 1
    fi

    if ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装或不在 PATH 中"
        exit 1
    fi
}

# 检查环境变量文件
check_env() {
    cd "$PROJECT_ROOT"
    
    if [ ! -f ".env" ]; then
        log_warning ".env 文件不存在"
        if [ -f "env.example" ]; then
            log_info "从 env.example 复制配置文件..."
            cp env.example .env
            log_warning "请编辑 .env 文件配置正确的参数"
            log_warning "特别注意修改微信小程序 AppID 和 Secret"
        else
            log_error "env.example 文件也不存在，无法创建配置文件"
            exit 1
        fi
    fi
}

# 启动服务
start_services() {
    cd "$PROJECT_ROOT"
    
    log_info "启动自习座位预约系统..."
    
    # 拉取最新镜像（如果使用远程镜像）
    log_info "拉取依赖镜像..."
    docker compose pull mysql redis
    
    # 启动服务
    log_info "启动所有服务..."
    docker compose up -d
    
    # 等待服务启动
    log_info "等待服务启动完成..."
    sleep 10
    
    # 检查服务状态
    check_services_health
}

# 停止服务
stop_services() {
    cd "$PROJECT_ROOT"
    
    log_info "停止自习座位预约系统..."
    docker compose down
    
    log_success "服务已停止"
}

# 重启服务
restart_services() {
    log_info "重启自习座位预约系统..."
    stop_services
    sleep 5
    start_services
}

# 检查服务健康状态
check_services_health() {
    cd "$PROJECT_ROOT"
    
    log_info "检查服务健康状态..."
    
    # 检查各个服务状态
    services=("studyseat-mysql" "studyseat-redis" "studyseat-backend" "studyseat-frontend")
    
    for service in "${services[@]}"; do
        if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "$service.*healthy\|$service.*Up"; then
            log_success "$service: 运行正常"
        else
            log_error "$service: 可能存在问题"
            docker ps --filter "name=$service" --format "table {{.Names}}\t{{.Status}}"
        fi
    done
    
    # 显示服务访问地址
    echo ""
    log_info "服务访问地址:"
    echo "前端服务: http://localhost:${FRONTEND_PORT:-80}"
    echo "后端服务: http://localhost:${BACKEND_PORT:-8080}"
    echo "API文档: http://localhost:${BACKEND_PORT:-8080}/doc.html"
    echo "数据库端口: ${MYSQL_PORT:-3306}"
}

# 查看服务状态
show_status() {
    cd "$PROJECT_ROOT"
    
    log_info "服务运行状态:"
    docker compose ps
    
    echo ""
    log_info "容器资源使用情况:"
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}" \
        studyseat-mysql studyseat-redis studyseat-backend studyseat-frontend 2>/dev/null || true
}

# 查看日志
show_logs() {
    cd "$PROJECT_ROOT"
    
    local service=${2:-""}
    
    if [ -n "$service" ]; then
        log_info "显示 $service 服务日志:"
        docker compose logs -f --tail=100 "$service"
    else
        log_info "显示所有服务日志:"
        docker compose logs -f --tail=50
    fi
}

# 清理资源
cleanup() {
    cd "$PROJECT_ROOT"
    
    log_warning "这将删除所有容器、网络和匿名卷（数据卷会保留）"
    read -p "确认继续? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "清理 Docker 资源..."
        docker compose down --remove-orphans
        docker system prune -f
        log_success "清理完成"
    else
        log_info "取消清理操作"
    fi
}

# 主函数
main() {
    local action=${1:-"start"}
    
    echo "================================================"
    echo "自习座位预约系统 Docker 部署工具"
    echo "================================================"
    
    # 检查依赖
    check_dependencies
    
    case "$action" in
        "start")
            check_env
            start_services
            ;;
        "stop")
            stop_services
            ;;
        "restart")
            check_env
            restart_services
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs "$@"
            ;;
        "cleanup")
            cleanup
            ;;
        "help"|"-h"|"--help")
            echo "使用方法: $0 [命令]"
            echo ""
            echo "可用命令:"
            echo "  start    - 启动所有服务（默认）"
            echo "  stop     - 停止所有服务"
            echo "  restart  - 重启所有服务"
            echo "  status   - 查看服务状态"
            echo "  logs     - 查看服务日志"
            echo "  cleanup  - 清理 Docker 资源"
            echo "  help     - 显示此帮助信息"
            echo ""
            echo "示例:"
            echo "  $0 start"
            echo "  $0 logs backend"
            echo "  $0 status"
            ;;
        *)
            log_error "未知命令: $action"
            echo "使用 '$0 help' 查看帮助信息"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@" 