package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminFileUploadDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员文件上传服务接口
 */
public interface IAdminFileUploadService {
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param type 文件类型
     * @return 上传结果
     */
    AdminFileUploadDTO uploadFile(MultipartFile file, String type);
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 删除结果
     */
    AdminFileUploadDTO deleteFile(String filePath);
    
    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @param expiration 过期时间（秒）
     * @return 文件URL结果
     */
    AdminFileUploadDTO getFileUrl(String filePath, int expiration);
} 