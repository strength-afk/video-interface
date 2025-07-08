package com.example.video_interface.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * 🔐 加密工具类 (后端Java版本)
 * 提供AES解密、HMAC验证、动态密钥生成等安全功能
 * 与前端加密工具保持一致的算法和参数
 */
@Slf4j
@Component
public class CryptoUtil {

    // 🔧 加密配置常量
    private static final String BASE_SECRET = "jiuban_video_2024_security_key";
    private static final long TIME_WINDOW = 300000L; // 5分钟时间窗口
    private static final String DEVICE_SALT = "jiuban_device_fingerprint";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    // 🔧 敏感字段列表
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
        "password", "oldPassword", "newPassword", "token", "username", "email", "phone"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.crypto.security.require-signature:true}")
    private boolean requireSignature;
    
    @Value("${app.crypto.security.timestamp-tolerance:900000}")
    private long timestampTolerance; // 默认15分钟容差

    /**
     * 生成动态密钥
     * @param timestamp 时间戳
     * @param deviceFingerprint 设备指纹
     * @return 动态密钥
     */
    public String generateDynamicKey(long timestamp, String deviceFingerprint) {
        try {
            long timeWindow = timestamp / TIME_WINDOW;
            String keyMaterial = String.join("|", 
                BASE_SECRET, 
                String.valueOf(timeWindow), 
                deviceFingerprint
            );
            
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(keyMaterial.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            log.error("生成动态密钥失败", e);
            throw new RuntimeException("密钥生成失败", e);
        }
    }

    /**
     * AES解密
     * @param ciphertext Base64编码的密文
     * @param iv Base64编码的初始化向量
     * @param key 十六进制密钥
     * @return 明文
     */
    public String aesDecrypt(String ciphertext, String iv, String key) {
        try {
            // 解析密钥
            byte[] keyBytes = hexToBytes(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            
            // 解析IV
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            
            // 解析密文
            byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
            
            // 创建Cipher
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            // 解密
            byte[] decrypted = cipher.doFinal(ciphertextBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * HMAC签名验证
     * @param data 原始数据
     * @param signature Base64编码的签名
     * @param key 签名密钥
     * @return 验证结果
     */
    public boolean hmacVerify(String data, String signature, String key) {
        try {
            String expectedSignature = hmacSign(data, key);
            return signature.equals(expectedSignature);
        } catch (Exception e) {
            log.error("HMAC验证失败", e);
            return false;
        }
    }

    /**
     * HMAC签名
     * @param data 待签名数据
     * @param key 签名密钥
     * @return Base64编码的签名
     */
    public String hmacSign(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            log.error("HMAC签名失败", e);
            throw new RuntimeException("数据签名失败", e);
        }
    }

    /**
     * 解密敏感数据
     * @param encryptedJson 加密的JSON数据
     * @param deviceFingerprint 设备指纹
     * @return 解密后的数据
     */
    public Map<String, Object> decryptSensitiveData(String encryptedJson, String deviceFingerprint) {
        try {
            JsonNode rootNode = objectMapper.readTree(encryptedJson);
            
            // 检查是否包含加密元数据
            if (!rootNode.has("_crypto")) {
                // 没有加密数据，直接返回
                return objectMapper.convertValue(rootNode, Map.class);
            }
            
            JsonNode cryptoNode = rootNode.get("_crypto");
            if (!cryptoNode.get("encrypted").asBoolean()) {
                // 未加密，直接返回
                Map<String, Object> result = objectMapper.convertValue(rootNode, Map.class);
                result.remove("_crypto");
                return result;
            }
            
            // 提取加密元数据
            List<String> encryptedFields = new ArrayList<>();
            cryptoNode.get("fields").forEach(field -> encryptedFields.add(field.asText()));
            long timestamp = cryptoNode.get("timestamp").asLong();
            String signature = cryptoNode.get("signature").asText();
            
            // 验证时间窗口
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > TIME_WINDOW * 2) {
                throw new RuntimeException("数据已过期");
            }
            
            // 生成动态密钥
            String dynamicKey = generateDynamicKey(timestamp, deviceFingerprint);
            
            // 验证签名（开发模式可跳过）
            if (requireSignature) {
                String signatureData = objectMapper.writeValueAsString(Map.of(
                    "fields", encryptedFields,
                    "timestamp", timestamp,
                    "deviceFingerprint", deviceFingerprint
                ));
                
                // 🔍 详细调试内层数据签名验证
                log.debug("🔐 后端内层数据签名调试:");
                log.debug("  加密字段: {}", encryptedFields);
                log.debug("  时间戳: {}", timestamp);
                log.debug("  设备指纹: {}", deviceFingerprint);
                log.debug("  期望签名数据: {}", signatureData);
                log.debug("  动态密钥: {}...", dynamicKey.substring(0, 8));
                log.debug("  接收到的签名: {}", signature);
                
                String expectedSignature = hmacSign(signatureData, dynamicKey);
                log.debug("  期望的签名: {}", expectedSignature);
                
                if (!hmacVerify(signatureData, signature, dynamicKey)) {
                    log.error("❌ 内层数据签名不匹配:");
                    log.error("  期望签名数据: {}", signatureData);
                    log.error("  接收签名: {}", signature);
                    log.error("  期望签名: {}", expectedSignature);
                    throw new RuntimeException("数据签名验证失败");
                }
            } else {
                log.debug("⚠️ 开发模式：跳过数据解密签名验证");
            }
            
            // 解密数据
            Map<String, Object> decryptedData = objectMapper.convertValue(rootNode, Map.class);
            decryptedData.remove("_crypto");
            
            for (String field : encryptedFields) {
                if (decryptedData.containsKey(field)) {
                    Map<String, Object> encryptedField = (Map<String, Object>) decryptedData.get(field);
                    String ciphertext = (String) encryptedField.get("ciphertext");
                    String iv = (String) encryptedField.get("iv");
                    
                    String decryptedValue = aesDecrypt(ciphertext, iv, dynamicKey);
                    decryptedData.put(field, decryptedValue);
                }
            }
            
            log.info("✅ 成功解密敏感数据，字段: {}", encryptedFields);
            return decryptedData;
            
        } catch (Exception e) {
            log.error("❌ 解密敏感数据失败", e);
            throw new RuntimeException("数据解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证请求签名
     * @param method HTTP方法
     * @param url 请求URL（包含context-path的完整路径）
     * @param data 请求数据（JSON字符串）
     * @param timestamp 时间戳
     * @param signature 签名
     * @param deviceFingerprint 设备指纹
     * @return 验证结果
     */
    public boolean verifyRequestSignature(String method, String url, String data, 
                                        long timestamp, String signature, String deviceFingerprint) {
        try {
            // 🌍 增强时间戳验证 - 支持时区容差
            long currentTime = System.currentTimeMillis();
            long timeDiff = Math.abs(currentTime - timestamp);
            
            if (timeDiff > timestampTolerance) {
                log.warn("请求时间戳超出容差范围: timestamp={}, current={}, diff={}ms, tolerance={}ms", 
                    timestamp, currentTime, timeDiff, timestampTolerance);
                return false;
            }
            
            // 🔧 标准化JSON数据格式，确保与前端保持一致
            String normalizedData = "";
            if (data != null && !data.trim().isEmpty()) {
                try {
                    // 解析并重新序列化JSON，确保格式标准化
                    Object dataObj = objectMapper.readValue(data, Object.class);
                    normalizedData = objectMapper.writeValueAsString(dataObj);
                } catch (Exception e) {
                    // 如果不是有效JSON，直接使用原字符串
                    normalizedData = data;
                    log.debug("数据不是有效JSON，使用原字符串: {}", e.getMessage());
                }
            }
            
            // 🔧 去掉context-path前缀，使用相对路径进行签名验证
            // 前端使用相对路径，后端也应该使用相对路径来保持一致
            String relativePath = url;
            if (url.startsWith("/api")) {
                relativePath = url.substring(4); // 去掉 "/api" 前缀
            }
            
            // 🔑 多时间窗口验证 - 处理时区边界情况  
            String signatureData = String.join("|", 
                method.toUpperCase(), 
                relativePath, 
                normalizedData, 
                String.valueOf(timestamp), 
                deviceFingerprint
            );
            
            // 尝试当前时间窗口
            String dynamicKey = generateDynamicKey(timestamp, deviceFingerprint);
            
            // 🔍 详细调试动态密钥生成过程
            long timeWindow = timestamp / TIME_WINDOW;
            String keyMaterial = String.join("|", BASE_SECRET, String.valueOf(timeWindow), deviceFingerprint);
            log.debug("🔑 动态密钥生成详情:");
            log.debug("  - 时间戳: {}", timestamp);
            log.debug("  - 时间窗口: {}", timeWindow);
            log.debug("  - 基础密钥: {}", BASE_SECRET);
            log.debug("  - 设备指纹: {}", deviceFingerprint);
            log.debug("  - 密钥材料: {}", keyMaterial);
            log.debug("  - 动态密钥完整: {}", dynamicKey);
            log.debug("🔧 URL路径处理:");
            log.debug("  - 原始URL: {}", url);
            log.debug("  - 相对路径: {}", relativePath);
            
            boolean isValid = hmacVerify(signatureData, signature, dynamicKey);
            
            if (!isValid) {
                // 🕐 尝试相邻时间窗口（处理时区边界）
                long[] adjacentWindows = {
                    timestamp - TIME_WINDOW,  // 前一个窗口
                    timestamp + TIME_WINDOW   // 后一个窗口
                };
                
                for (long adjacentTimestamp : adjacentWindows) {
                    if (Math.abs(currentTime - adjacentTimestamp) <= timestampTolerance) {
                        String adjacentKey = generateDynamicKey(adjacentTimestamp, deviceFingerprint);
                        if (hmacVerify(signatureData, signature, adjacentKey)) {
                            log.info("✅ 使用相邻时间窗口验证成功: original={}, adjacent={}", timestamp, adjacentTimestamp);
                            return true;
                        }
                    }
                }
                
                log.warn("请求签名验证失败: method={}, url={}, timestamp={}", method, url, timestamp);
                log.debug("签名验证详情:");
                log.debug("  - 原始数据: {}", data != null ? data.substring(0, Math.min(100, data.length())) + "..." : "null");
                log.debug("  - 标准化数据: {}", normalizedData.substring(0, Math.min(100, normalizedData.length())) + "...");
                log.debug("  - 签名数据: {}", signatureData);
                log.debug("  - 动态密钥: {}", dynamicKey.substring(0, 8) + "...");
                log.debug("  - 接收签名: {}", signature.substring(0, 10) + "...");
                log.debug("  - 期望签名: {}", hmacSign(signatureData, dynamicKey).substring(0, 10) + "...");
                log.debug("  - 时间差异: {}ms", timeDiff);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("验证请求签名时发生错误", e);
            return false;
        }
    }

    /**
     * 检查是否包含敏感数据
     * @param data 数据对象
     * @return 是否包含敏感数据
     */
    public boolean hasSensitiveData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        return data.keySet().stream().anyMatch(SENSITIVE_FIELDS::contains);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 十六进制字符串转字节数组
     */
    private byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 获取敏感字段列表
     * @return 敏感字段集合
     */
    public Set<String> getSensitiveFields() {
        return SENSITIVE_FIELDS;
    }
} 