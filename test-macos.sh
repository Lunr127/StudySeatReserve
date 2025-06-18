#!/bin/bash

echo "=============================================="
echo "自习座位预约系统测试脚本 (macOS)"
echo "=============================================="
echo

# 检查当前目录是否为StudySeatReserve
CURRENT_DIR=$(basename "$(pwd)")
if [ "$CURRENT_DIR" != "StudySeatReserve" ]; then
    echo "❌ 错误：请在StudySeatReserve根目录下运行此脚本"
    echo "当前目录：$(pwd)"
    echo "请使用：cd /path/to/StudySeatReserve && ./test-macos.sh"
    exit 1
fi

# 进入backend目录
echo "📁 进入backend目录..."
cd backend || {
    echo "❌ 错误：找不到backend目录"
    exit 1
}

# 检查Java环境
echo "☕ 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到Java，请确保Java已安装并在PATH中"
    exit 1
fi

echo "当前Java版本："
java -version
echo

# 检查Maven环境
echo "🔧 检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：未找到Maven，请确保Maven已安装并在PATH中"
    exit 1
fi

echo "当前Maven版本："
mvn -version
echo

# 清理并编译项目
echo "🧹 清理并编译项目..."
if mvn clean compile; then
    echo "✅ 编译成功"
else
    echo "❌ 编译失败，请检查代码"
    exit 1
fi
echo

# 运行单元测试
echo "🧪 运行单元测试..."
if mvn test -Dspring.profiles.active=test; then
    echo "✅ 测试执行完成"
else
    echo "⚠️  测试执行完成（可能有失败的测试）"
fi
echo

# 生成测试报告
echo "📊 生成测试报告..."
if mvn surefire-report:report; then
    echo "✅ 测试报告生成成功"
    echo "测试报告路径: target/site/surefire-report.html"
else
    echo "⚠️  测试报告生成失败"
fi
echo

# 生成代码覆盖率报告
echo "📈 生成代码覆盖率报告..."
if mvn jacoco:report; then
    echo "✅ 代码覆盖率报告生成成功"
    echo "覆盖率报告路径: target/site/jacoco/index.html"
else
    echo "⚠️  代码覆盖率报告生成失败"
fi
echo

echo "=============================================="
echo "测试完成"
echo "=============================================="
echo

# 显示测试结果摘要
if [ -f "target/surefire-reports/TEST-*.xml" ]; then
    echo "📋 测试结果摘要："
    grep -h "tests=" target/surefire-reports/TEST-*.xml | head -1 | \
    sed 's/.*tests="\([^"]*\)".*failures="\([^"]*\)".*errors="\([^"]*\)".*/总测试数: \1, 失败: \2, 错误: \3/'
    echo
fi

# 询问是否打开报告
echo "📖 是否要打开测试报告? (y/n)"
read -r response
if [[ "$response" =~ ^[Yy]$ ]]; then
    if [ -f "target/site/surefire-report.html" ]; then
        echo "🌐 打开测试报告..."
        open target/site/surefire-report.html
    fi
    
    if [ -f "target/site/jacoco/index.html" ]; then
        echo "🌐 打开覆盖率报告..."
        open target/site/jacoco/index.html
    fi
fi

echo "🎉 脚本执行结束！" 