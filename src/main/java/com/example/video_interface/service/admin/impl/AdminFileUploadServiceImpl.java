package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminFileUploadDTO;
import com.example.video_interface.service.admin.IAdminFileUploadService;
import com.example.video_interface.service.common.IMinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 管理员文件上传服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminFileUploadServiceImpl implements IAdminFileUploadService {
    
    private final IMinioService minioService;
    
    @Override
    public AdminFileUploadDTO uploadFile(MultipartFile file, String type) {
        try {
            log.info("开始上传文件: {}, 类型: {}, 大小: {} bytes", 
                file.getOriginalFilename(), type, file.getSize());
            
            // 生成文件路径
            String fileName = generateFileName(file.getOriginalFilename(), type);
            String objectName = generateObjectName(fileName, type);
            
            // 上传文件到MinIO
            String filePath = minioService.uploadFile(file, objectName);
            
            // 生成访问URL
            String fileUrl = minioService.getFileUrl(filePath);
            
            AdminFileUploadDTO response = AdminFileUploadDTO.builder()
                    .filePath(filePath)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .fileName(file.getOriginalFilename())
                    .success(true)
                    .build();
            
            log.info("文件上传成功: {}", filePath);
            return response;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            
            return AdminFileUploadDTO.builder()
                    .success(false)
                    .errorMessage("文件上传失败: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public AdminFileUploadDTO deleteFile(String filePath) {
        try {
            log.info("删除文件: {}", filePath);
            
            boolean success = minioService.deleteFile(filePath);
            
            AdminFileUploadDTO response = AdminFileUploadDTO.builder()
                    .filePath(filePath)
                    .success(success)
                    .errorMessage(success ? null : "文件删除失败")
                    .build();
            
            if (success) {
                log.info("文件删除成功: {}", filePath);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            
            return AdminFileUploadDTO.builder()
                    .filePath(filePath)
                    .success(false)
                    .errorMessage("文件删除失败: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public AdminFileUploadDTO getFileUrl(String filePath, int expiration) {
        try {
            log.debug("获取文件访问URL: {}, 过期时间: {}秒", filePath, expiration);
            
            String fileUrl = minioService.getFileUrl(filePath, expiration);
            
            return AdminFileUploadDTO.builder()
                    .filePath(filePath)
                    .fileUrl(fileUrl)
                    .success(true)
                    .build();
            
        } catch (Exception e) {
            log.error("获取文件访问URL失败: {}", filePath, e);
            
            return AdminFileUploadDTO.builder()
                    .filePath(filePath)
                    .success(false)
                    .errorMessage("获取文件访问URL失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename, String type) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        return String.format("%s_%s_%s%s", type, timestamp, uuid, extension);
    }
    
    /**
     * 生成对象名称（相对路径）
     */
    private String generateObjectName(String fileName, String type) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", type, date, fileName);
    }
} 