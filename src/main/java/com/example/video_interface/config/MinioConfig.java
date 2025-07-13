package com.example.video_interface.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    
    /**
     * MinIO服务端点
     */
    private String endpoint;
    
    /**
     * 访问密钥
     */
    private String accessKey;
    
    /**
     * 秘密密钥
     */
    private String secretKey;
    
    /**
     * 存储桶名称
     */
    private String bucketName;
    
    /**
     * 区域
     */
    private String region;
    
    /**
     * URL过期时间（秒）
     */
    private Integer urlExpiration;
    
    /**
     * 创建MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        log.info("初始化MinIO客户端，端点: {}, 存储桶: {}", endpoint, bucketName);
        
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region(region)
                .build();
        
        log.info("MinIO客户端初始化完成");
        return minioClient;
    }
} 