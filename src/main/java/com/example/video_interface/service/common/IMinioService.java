package com.example.video_interface.service.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO服务接口
 */
public interface IMinioService {
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param objectName 对象名称（相对路径）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String objectName);
    
    /**
     * 上传文件流
     * @param inputStream 输入流
     * @param objectName 对象名称（相对路径）
     * @param contentType 内容类型
     * @param size 文件大小
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String objectName, String contentType, long size);
    
    /**
     * 获取文件访问URL（预签名URL，私有读写）
     * @param objectName 对象名称（相对路径）
     * @return 预签名URL
     */
    String getFileUrl(String objectName);
    
    /**
     * 获取文件访问URL（预签名URL，私有读写）
     * @param objectName 对象名称（相对路径）
     * @param expiration 过期时间（秒）
     * @return 预签名URL
     */
    String getFileUrl(String objectName, int expiration);
    
    /**
     * 删除文件
     * @param objectName 对象名称（相对路径）
     * @return 是否删除成功
     */
    boolean deleteFile(String objectName);
    
    /**
     * 检查文件是否存在
     * @param objectName 对象名称（相对路径）
     * @return 是否存在
     */
    boolean fileExists(String objectName);
    
    /**
     * 获取文件信息
     * @param objectName 对象名称（相对路径）
     * @return 文件信息
     */
    FileInfo getFileInfo(String objectName);
    
    /**
     * 文件信息
     */
    class FileInfo {
        private String objectName;
        private long size;
        private String contentType;
        private String etag;
        private String lastModified;
        
        public FileInfo(String objectName, long size, String contentType, String etag, String lastModified) {
            this.objectName = objectName;
            this.size = size;
            this.contentType = contentType;
            this.etag = etag;
            this.lastModified = lastModified;
        }
        
        // Getters
        public String getObjectName() { return objectName; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
        public String getEtag() { return etag; }
        public String getLastModified() { return lastModified; }
    }
} 