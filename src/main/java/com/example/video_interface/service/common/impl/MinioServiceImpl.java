package com.example.video_interface.service.common.impl;

import com.example.video_interface.config.MinioConfig;
import com.example.video_interface.service.common.IMinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements IMinioService {
    
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    
    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        try {
            log.info("开始上传文件: {}, 大小: {} bytes", objectName, file.getSize());
            
            // 确保存储桶存在
            ensureBucketExists();
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            log.info("文件上传成功: {}", objectName);
            return objectName;
            
        } catch (Exception e) {
            log.error("MinIO上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String objectName, String contentType, long size) {
        try {
            log.info("开始上传文件流: {}, 大小: {} bytes", objectName, size);
            
            // 确保存储桶存在
            ensureBucketExists();
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build()
            );
            
            log.info("文件流上传成功: {}", objectName);
            return objectName;
            
        } catch (Exception e) {
            log.error("MinIO文件流上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件流上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getFileUrl(String objectName) {
        return getFileUrl(objectName, minioConfig.getUrlExpiration());
    }
    
    @Override
    public String getFileUrl(String objectName, int expiration) {
        try {
            log.debug("生成文件访问URL: {}, 过期时间: {}秒", objectName, expiration);
            
            String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .expiry(expiration, TimeUnit.SECONDS)
                    .build()
            );
            
            log.debug("文件访问URL生成成功: {}", url);
            return url;
            
        } catch (Exception e) {
            log.error("MinIO URL生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件访问URL失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteFile(String objectName) {
        try {
            log.info("删除文件: {}", objectName);
            
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            
            log.info("文件删除成功: {}", objectName);
            return true;
            
        } catch (Exception e) {
            log.error("MinIO删除失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            log.error("MinIO文件检查失败: {}", e.getMessage(), e);
            throw new RuntimeException("检查文件是否存在失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public FileInfo getFileInfo(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            
            return new FileInfo(
                objectName,
                stat.size(),
                stat.contentType(),
                stat.etag(),
                stat.lastModified().toString()
            );
            
        } catch (Exception e) {
            log.error("MinIO文件信息获取失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取文件信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build()
            );
            
            if (!found) {
                log.info("存储桶不存在，创建存储桶: {}", minioConfig.getBucketName());
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build()
                );
                log.info("存储桶创建成功: {}", minioConfig.getBucketName());
            }
            
        } catch (Exception e) {
            log.error("MinIO存储桶操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("存储桶操作失败: " + e.getMessage(), e);
        }
    }
} 