package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFileUploadDTO {
    
    /**
     * 文件相对路径
     */
    private String filePath;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 上传是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 