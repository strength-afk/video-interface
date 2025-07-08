#!/bin/bash

# 🔒 管理员登录防爆破功能测试脚本
# 验证管理员账号连续登录失败5次后会被锁定

echo "🔒 开始测试管理员登录防爆破功能"
echo "================================================="

# 配置
BASE_URL="http://localhost:8080/api"
ADMIN_USERNAME="admin"
CORRECT_PASSWORD="admin123"
WRONG_PASSWORD="wrongpassword"

# 颜色输出
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

# 等待后端启动
wait_for_backend() {
    log_info "等待后端服务启动..."
    for i in {1..30}; do
        if curl -s "$BASE_URL/users/admin/check-status" >/dev/null 2>&1; then
            log_success "后端服务已启动"
            return 0
        fi
        echo -n "."
        sleep 2
    done
    log_error "后端服务启动超时"
    exit 1
}

# 检查管理员状态
check_admin_status() {
    log_info "检查管理员状态..."
    response=$(curl -s "$BASE_URL/users/admin/check-status")
    echo "管理员状态: $response"
    
    has_admin=$(echo "$response" | grep -o '"hasAdmin":[^,}]*' | cut -d':' -f2)
    if [ "$has_admin" = "false" ]; then
        log_warning "系统中没有管理员，创建测试管理员..."
        create_test_admin
    else
        log_info "系统中已存在管理员"
    fi
}

# 创建测试管理员
create_test_admin() {
    log_info "创建测试管理员账号: $ADMIN_USERNAME"
    
    response=$(curl -s -X POST "$BASE_URL/users/admin/init" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$ADMIN_USERNAME\",
            \"password\": \"$CORRECT_PASSWORD\",
            \"email\": \"testadmin@example.com\"
        }")
    
    if echo "$response" | grep -q "成功"; then
        log_success "测试管理员创建成功"
    else
        log_error "测试管理员创建失败: $response"
        # 如果创建失败，可能是已存在，继续测试
    fi
}

# 测试管理员登录
test_admin_login() {
    local username=$1
    local password=$2
    local test_name=$3
    
    log_info "测试: $test_name"
    
    response=$(curl -s -X POST "$BASE_URL/users/admin/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$username\",
            \"password\": \"$password\"
        }")
    
    echo "响应: $response"
    
    if echo "$response" | grep -q "成功"; then
        log_success "✅ 登录成功"
        return 0
    else
        log_error "❌ 登录失败"
        return 1
    fi
}

# 测试防爆破功能
test_brute_force_protection() {
    log_info "开始测试防爆破功能"
    echo "================================================="
    
    log_info "第1步: 测试正确密码登录（验证账号正常）"
    test_admin_login "$ADMIN_USERNAME" "$CORRECT_PASSWORD" "正确密码登录"
    
    echo ""
    log_info "第2步: 连续5次错误密码登录（触发防爆破）"
    
    for i in {1..5}; do
        echo ""
        log_warning "尝试 $i/5 - 使用错误密码"
        test_admin_login "$ADMIN_USERNAME" "$WRONG_PASSWORD" "错误密码登录 #$i"
        
        if [ $i -lt 5 ]; then
            sleep 2  # 间隔2秒
        fi
    done
    
    echo ""
    log_info "第3步: 验证账号是否被锁定"
    log_warning "再次尝试正确密码登录（应该被拒绝）"
    
    response=$(curl -s -X POST "$BASE_URL/users/admin/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$ADMIN_USERNAME\",
            \"password\": \"$CORRECT_PASSWORD\"
        }")
    
    echo "锁定验证响应: $response"
    
    if echo "$response" | grep -q "锁定\|locked"; then
        log_success "✅ 防爆破功能正常 - 账号已被锁定"
    else
        log_error "❌ 防爆破功能异常 - 账号未被锁定"
    fi
    
    echo ""
    log_info "第4步: 检查错误消息是否包含剩余尝试次数信息"
    if echo "$response" | grep -q "次数过多\|已被锁定\|locked"; then
        log_success "✅ 错误消息正确显示锁定信息"
    else
        log_warning "⚠️ 错误消息可能需要优化"
    fi
}

# 显示配置信息
show_config() {
    log_info "当前防爆破配置:"
    echo "  • 管理员最大失败次数: 5次"
    echo "  • 锁定时长: 60分钟" 
    echo "  • 不针对IP，按账号锁定"
    echo "  • 登录成功后清除失败计数"
    echo ""
}

# 主测试流程
main() {
    echo "🔒 管理员登录防爆破功能测试"
    echo "测试账号: $ADMIN_USERNAME"
    echo "测试时间: $(date)"
    echo "================================================="
    
    show_config
    
    # 等待后端启动
    wait_for_backend
    
    # 检查管理员状态
    check_admin_status
    
    echo ""
    # 执行防爆破测试
    test_brute_force_protection
    
    echo ""
    echo "================================================="
    log_info "测试完成!"
    echo ""
    log_warning "注意事项:"
    echo "  1. 测试管理员账号 '$ADMIN_USERNAME' 现在已被锁定"
    echo "  2. 如需解除锁定，请等待60分钟或重启应用"
    echo "  3. 生产环境请使用其他管理员账号"
    echo "================================================="
}

# 执行主函数
main 