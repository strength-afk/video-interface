package com.example.video_interface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理应用中的异常，提供友好的错误响应
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理IllegalArgumentException异常
     * 将业务逻辑异常转换为400错误，返回具体的错误信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("业务逻辑异常: {} - 请求路径: {}", ex.getMessage(), request.getDescription(false));
        
        Map<String, Object> response = Map.of(
            "code", 400,
            "message", ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理RuntimeException异常
     * 将运行时异常转换为500错误
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("运行时异常: {} - 请求路径: {}", ex.getMessage(), request.getDescription(false), ex);
        
        Map<String, Object> response = Map.of(
            "code", 500,
            "message", "服务器内部错误，请稍后重试"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        log.error("未捕获的异常: {} - 请求路径: {}", ex.getMessage(), request.getDescription(false), ex);
        
        Map<String, Object> response = Map.of(
            "code", 500,
            "message", "服务器内部错误，请稍后重试"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 