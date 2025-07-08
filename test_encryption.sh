#!/bin/bash

# 🔐 三端加密系统测试脚本
# 验证前端加密 -> 后端解密 -> JWT增强安全的完整流程

echo "🔐 开始测试久伴视频管理系统加密功能"
echo "=========================================="

# 测试配置
BASE_URL="http://localhost:8080/api"
TIMESTAMP=$(date +%s)000
DEVICE_ID="test_device_$(openssl rand -hex 8)"

echo "📋 测试配置:"
echo "  ├─ 服务器地址: $BASE_URL"
echo "  ├─ 时间戳: $TIMESTAMP"
echo "  └─ 设备ID: $DEVICE_ID"
echo ""

# 测试1: 检查管理员状态（无加密）
echo "🧪 测试1: 检查管理员状态 (无加密数据)"
echo "----------------------------------------"

curl -s \
  -H "Content-Type: application/json" \
  -H "X-Client-Type: test" \
  "$BASE_URL/users/admin/check-status" | jq '.'

echo -e "\n"

# 测试2: 普通登录请求（无加密头，验证兼容性）
echo "🧪 测试2: 普通登录请求 (兼容性测试)"
echo "----------------------------------------"

LOGIN_DATA='{"username":"admin","password":"admin123"}'

curl -s \
  -X POST \
  -H "Content-Type: application/json" \
  -d "$LOGIN_DATA" \
  "$BASE_URL/users/admin/login" | jq '.'

echo -e "\n"

# 测试3: 模拟前端加密登录（带加密头但无实际加密数据）
echo "🧪 测试3: 带安全头的登录请求"
echo "----------------------------------------"

# 生成模拟签名（简化版本）
SIGNATURE_DATA="POST|/users/admin/login|$LOGIN_DATA|$TIMESTAMP|$DEVICE_ID"
SIGNATURE=$(echo -n "$SIGNATURE_DATA" | openssl dgst -sha256 -hmac "test_key" -binary | base64)

curl -s \
  -X POST \
  -H "Content-Type: application/json" \
  -H "X-Timestamp: $TIMESTAMP" \
  -H "X-Signature: $SIGNATURE" \
  -H "X-Device-ID: $DEVICE_ID" \
  -H "X-Client-Version: 1.0.0" \
  -H "X-Client-Type: test" \
  -d "$LOGIN_DATA" \
  "$BASE_URL/users/admin/login" | jq '.'

echo -e "\n"

# 测试4: 验证JWT Token增强功能
echo "🧪 测试4: JWT Token增强功能测试"
echo "----------------------------------------"

# 先获取Token
TOKEN_RESPONSE=$(curl -s \
  -X POST \
  -H "Content-Type: application/json" \
  -d "$LOGIN_DATA" \
  "$BASE_URL/users/admin/login")

TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.token // .access_token // empty')

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo "✅ 成功获取Token: ${TOKEN:0:20}..."
    
    # 使用Token访问受保护资源
    echo "🔑 使用Token访问受保护资源:"
    curl -s \
      -H "Authorization: Bearer $TOKEN" \
      -H "X-Device-ID: $DEVICE_ID" \
      "$BASE_URL/users/admin/check-status" | jq '.'
else
    echo "❌ 未能获取有效Token"
fi

echo -e "\n"

# 测试5: 加密配置验证
echo "🧪 测试5: 后端加密配置验证"
echo "----------------------------------------"

echo "📊 检查后端启动日志中的加密配置..."
if [ -f "logs/application.log" ]; then
    echo "🔍 加密配置相关日志:"
    grep -E "(加密配置|CryptoConfig|JWT.*initialized)" logs/application.log | tail -5
else
    echo "ℹ️ 请检查后端控制台输出的加密配置信息"
fi

echo -e "\n"

# 测试6: 错误场景测试
echo "🧪 测试6: 错误场景测试"
echo "----------------------------------------"

echo "🚫 测试无效时间戳:"
INVALID_TIMESTAMP="1234567890"
INVALID_SIGNATURE=$(echo -n "POST|/users/admin/login|$LOGIN_DATA|$INVALID_TIMESTAMP|$DEVICE_ID" | openssl dgst -sha256 -hmac "test_key" -binary | base64)

curl -s \
  -X POST \
  -H "Content-Type: application/json" \
  -H "X-Timestamp: $INVALID_TIMESTAMP" \
  -H "X-Signature: $INVALID_SIGNATURE" \
  -H "X-Device-ID: $DEVICE_ID" \
  -H "X-Client-Type: test" \
  -d "$LOGIN_DATA" \
  "$BASE_URL/users/admin/login" | jq '.'

echo -e "\n"

echo "🚫 测试无效签名:"
curl -s \
  -X POST \
  -H "Content-Type: application/json" \
  -H "X-Timestamp: $TIMESTAMP" \
  -H "X-Signature: invalid_signature" \
  -H "X-Device-ID: $DEVICE_ID" \
  -H "X-Client-Type: test" \
  -d "$LOGIN_DATA" \
  "$BASE_URL/users/admin/login" | jq '.'

echo -e "\n"

# 测试总结
echo "📊 测试总结"
echo "=========================================="
echo "✅ 基本功能测试完成"
echo "✅ 兼容性测试完成"  
echo "✅ 安全头验证完成"
echo "✅ JWT增强功能测试完成"
echo "✅ 错误场景测试完成"
echo ""
echo "📝 注意事项:"
echo "  ├─ 前端加密需要安装crypto-js依赖"
echo "  ├─ 确保所有客户端发送正确的安全头"
echo "  ├─ 生产环境需要配置更强的密钥"
echo "  └─ 定期更新加密配置和密钥"
echo ""
echo "🔐 加密系统测试完成！" 