#!/bin/bash

echo "=== 用户自动解锁功能测试 ==="

# 1. 查看当前锁定用户
echo "1. 查看当前锁定用户状态..."
curl -X POST http://localhost:8080/api/admin/users/locked-users \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'

echo -e "\n2. 手动触发自动解锁检查..."
curl -X POST http://localhost:8080/api/admin/users/auto-unlock \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'

echo -e "\n3. 再次查看锁定用户状态..."
curl -X POST http://localhost:8080/api/admin/users/locked-users \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -s | jq '.'

echo -e "\n=== 测试完成 ===" 