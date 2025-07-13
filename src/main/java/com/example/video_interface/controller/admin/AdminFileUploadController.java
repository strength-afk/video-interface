package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminFileUploadDTO;
import com.example.video_interface.service.admin.IAdminFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/files")
@RequiredArgsConstructor
public class AdminFileUploadController {
    
    private final IAdminFileUploadService adminFileUploadService;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<AdminFileUploadDTO> uploadFile(@RequestParam("file") MultipartFile file,
                                                        @RequestParam(value = "type", defaultValue = "movie") String type) {
        log.info("接收到文件上传请求: {}, 类型: {}, 大小: {} bytes", 
            file.getOriginalFilename(), type, file.getSize());
        
        AdminFileUploadDTO response = adminFileUploadService.uploadFile(file, type);
        
        if (response.getSuccess()) {
            log.info("文件上传成功: {}", response.getFilePath());
            return ResponseEntity.ok(response);
        } else {
            log.error("文件上传失败: {}", file.getOriginalFilename());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public ResponseEntity<AdminFileUploadDTO> deleteFile(@RequestParam("filePath") String filePath) {
        log.info("接收到文件删除请求: {}", filePath);
        
        AdminFileUploadDTO response = adminFileUploadService.deleteFile(filePath);
        
        if (response.getSuccess()) {
            log.info("文件删除成功: {}", filePath);
            return ResponseEntity.ok(response);
        } else {
            log.error("文件删除失败: {}", filePath);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取文件访问URL
     */
    @GetMapping("/url")
    public ResponseEntity<AdminFileUploadDTO> getFileUrl(@RequestParam("filePath") String filePath,
                                                        @RequestParam(value = "expiration", defaultValue = "3600") int expiration) {
        log.debug("接收到获取文件URL请求: {}, 过期时间: {}秒", filePath, expiration);
        
        AdminFileUploadDTO response = adminFileUploadService.getFileUrl(filePath, expiration);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            log.error("获取文件访问URL失败: {}", filePath);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
} 