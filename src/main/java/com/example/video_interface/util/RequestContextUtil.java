package com.example.video_interface.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestContextUtil {
    
    private static final String[] IP_HEADERS = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    /**
     * 获取当前请求的HttpServletRequest
     */
    private static Optional<HttpServletRequest> getRequestFromContext() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * Get the current HttpServletRequest. Throws IllegalStateException if no request is found.
     * @return The current HttpServletRequest
     * @throws IllegalStateException if no request is found in the current context
     */
    public HttpServletRequest getCurrentRequest() {
        return getRequestFromContext()
            .orElseThrow(() -> new IllegalStateException("No current request found"));
    }

    /**
     * 获取客户端真实IP
     */
    public String getClientIp() {
        HttpServletRequest request = getRequestFromContext()
            .orElseThrow(() -> new IllegalStateException("No current request found"));
            
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取会话ID
     */
    public static String getSessionId() {
        return getRequestFromContext()
                .map(request -> request.getSession(true).getId())
                .orElse("default-session");
    }

    /**
     * 获取客户端ID
     */
    public String getClientId() {
        return getRequestFromContext()
            .map(request -> {
                String clientId = request.getHeader("X-Client-ID");
                return clientId != null ? clientId : request.getRemoteAddr();
            })
            .orElseThrow(() -> new IllegalStateException("No current request found"));
    }

    /**
     * 获取客户端真实IP地址 - 静态方法
     * 用于登录安全服务等需要获取IP的场景
     */
    public static String getClientIpAddress() {
        try {
            HttpServletRequest request = getRequestFromContext()
                .orElse(null);
            
            if (request == null) {
                log.warn("无法获取当前请求上下文，使用默认IP");
                return "unknown";
            }
            
            // 检查各种可能的IP头
            for (String header : IP_HEADERS) {
                String ip = request.getHeader(header);
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // 处理多级代理的情况，取第一个IP
                    if (ip.contains(",")) {
                        ip = ip.split(",")[0].trim();
                    }
                    log.debug("🌐 从头部 {} 获取到IP: {}", header, ip);
                    return ip;
                }
            }
            
            // 如果没有找到代理IP，使用远程地址
            String remoteAddr = request.getRemoteAddr();
            log.debug("🌐 使用远程地址: {}", remoteAddr);
            return remoteAddr != null ? remoteAddr : "unknown";
            
        } catch (Exception e) {
            log.error("获取客户端IP地址失败: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * 从请求头中获取认证令牌
     * @return 认证令牌，如果不存在则返回null
     */
    public static String getAuthToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 