#!/bin/bash

echo "🧪 测试统一认证端点..."
curl -X POST http://localhost:8080/api/users/auth \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4000" \
  -d '{"username":"testuser","password":"123456"}' \
  -s | jq '.'

echo -e "\n✅ 测试完成" 