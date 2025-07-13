package com.example.video_interface.filter;

import com.example.video_interface.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 🔓 解密过滤器
 * 自动验证请求签名和解密敏感数据
 * 在请求到达Controller之前完成数据解密处理
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DecryptionFilter implements Filter {

    private final CryptoUtil cryptoUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 🔒 强制启用签名验证 - 生产级安全要求
    private static final boolean SIGNATURE_REQUIRED = true;
    
    @Value("${app.crypto.debug.enabled:false}")
    private boolean debugEnabled;

    @SuppressWarnings("null")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 🔧 防止重复处理同一个请求
        if (httpRequest.getAttribute("DECRYPTION_PROCESSED") != null) {
            log.debug("请求已经过解密处理，直接通过");
            chain.doFilter(request, response);
            return;
        }
        
        // 跳过错误页面请求
        String requestPath = httpRequest.getRequestURI();
        if (requestPath.contains("/error")) {
            log.debug("跳过错误页面请求: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }
        
        // 只处理POST和PUT请求
        String method = httpRequest.getMethod();
        if (!"POST".equals(method) && !"PUT".equals(method) && !"PATCH".equals(method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 检查Content-Type
        String contentType = httpRequest.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            chain.doFilter(request, response);
            return;
        }
        
        try {
            // 读取请求体
            String requestBody = getRequestBody(httpRequest);
            log.debug("读取到请求体: length={}, content={}", 
                requestBody != null ? requestBody.length() : 0, requestBody);
            
            if (!StringUtils.hasText(requestBody)) {
                log.debug("请求体为空，直接通过");
                chain.doFilter(request, response);
                return;
            }
            
            // 获取加密相关头信息
            String timestamp = httpRequest.getHeader("X-Timestamp");
            String signature = httpRequest.getHeader("X-Signature");
            String deviceId = httpRequest.getHeader("X-Device-ID");
            String clientType = httpRequest.getHeader("X-Client-Type");
            
            log.debug("处理请求: method={}, url={}, clientType={}, hasSignature={}, signatureRequired={}", 
                method, httpRequest.getRequestURI(), clientType, signature != null, SIGNATURE_REQUIRED);
            
            // 🔍 详细调试传输数据
            log.debug("请求头调试:");
            log.debug("  - Content-Type: {}", httpRequest.getContentType());
            log.debug("  - Content-Length: {}", httpRequest.getContentLength());
            log.debug("  - X-Timestamp: {}", timestamp);
            log.debug("  - X-Signature: {}", signature);
            log.debug("  - X-Device-ID: {}", deviceId);
            log.debug("  - X-Client-Type: {}", clientType);
            log.debug("请求体原始数据 (前100字符): {}", 
                requestBody != null ? requestBody.substring(0, Math.min(100, requestBody.length())) + "..." : "null");
            
            // 🔒 生产级安全：始终执行完整的签名验证和加密处理
            log.debug("生产级模式：执行完整的安全验证流程");
            
            // 如果没有加密头信息，直接通过
            if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(signature) || !StringUtils.hasText(deviceId)) {
                log.debug("普通请求，无需解密验证");
                CachedBodyHttpServletRequest processedRequest = new CachedBodyHttpServletRequest(httpRequest, requestBody);
                processedRequest.setAttribute("DECRYPTION_PROCESSED", true);
                chain.doFilter(processedRequest, response);
                return;
            }
            
            // 🔒 强制验证签名 - 生产级安全要求
            if (SIGNATURE_REQUIRED) {
                // 验证请求签名
                long timestampLong = Long.parseLong(timestamp);
                boolean signatureValid = cryptoUtil.verifyRequestSignature(
                    method, 
                    httpRequest.getRequestURI(), 
                    requestBody, 
                    timestampLong, 
                    signature, 
                    deviceId
                );
                
                if (!signatureValid) {
                    log.warn("请求签名验证失败: {} {}", method, httpRequest.getRequestURI());
                    setCorsHeaders(httpResponse, httpRequest);
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpResponse.setContentType("application/json;charset=UTF-8");
                    httpResponse.getWriter().write("{\"error\":\"请求签名验证失败\",\"code\":401}");
                    return;
                }
            } else {
                // 🚨 这个分支不应该被执行（SIGNATURE_REQUIRED = true）
                log.error("安全警告：签名验证被意外跳过！");
                throw new RuntimeException("安全验证失败");
            }
            
            log.debug("请求签名验证成功");
            
            // 检查是否包含加密数据
            if (!requestBody.contains("_crypto")) {
                log.debug("请求无加密数据");
                CachedBodyHttpServletRequest processedRequest = new CachedBodyHttpServletRequest(httpRequest, requestBody);
                processedRequest.setAttribute("DECRYPTION_PROCESSED", true);
                chain.doFilter(processedRequest, response);
                return;
            }
            
            // 🔒 生产级安全：始终执行完整的解密验证流程
            log.debug("执行标准解密流程");
            
            // 解密敏感数据
            Map<String, Object> decryptedData = cryptoUtil.decryptSensitiveData(requestBody, deviceId);
            String decryptedJson = objectMapper.writeValueAsString(decryptedData);
            
            log.info("成功解密请求数据: {} {}", method, httpRequest.getRequestURI());
            
            // 🔧 标记请求已经过解密处理，防止重复处理
            CachedBodyHttpServletRequest processedRequest = new CachedBodyHttpServletRequest(httpRequest, decryptedJson);
            processedRequest.setAttribute("DECRYPTION_PROCESSED", true);
            
            // 使用解密后的数据继续处理
            chain.doFilter(processedRequest, response);
            
        } catch (NumberFormatException e) {
            log.error("时间戳格式错误", e);
            setCorsHeaders(httpResponse, httpRequest);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"时间戳格式错误\",\"code\":400}");
        } catch (Exception e) {
            log.error("请求解密处理失败", e);
            setCorsHeaders(httpResponse, httpRequest);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"请求解密失败\",\"code\":400}");
        }
    }

    /**
     * 设置CORS响应头
     */
    private void setCorsHeaders(HttpServletResponse response, HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            // 检查是否为允许的源
            String[] allowedOrigins = {
                "http://localhost:5173",
                "http://localhost:3000", 
                "http://localhost:4000",
                "http://localhost:8080",
                "http://192.168.0.118:3000",
                "http://192.168.0.118:4000"
            };
            
            for (String allowedOrigin : allowedOrigins) {
                if (allowedOrigin.equals(origin)) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    break;
                }
            }
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    /**
     * 读取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        try (InputStream inputStream = request.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * 可缓存请求体的HttpServletRequest包装类
     * 解决InputStream只能读取一次的问题
     */
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request, String body) {
            super(request);
            this.cachedBody = body.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
        }
    }

    /**
     * 缓存的ServletInputStream实现
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }
} 