package com.example.video_interface.service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * iDataRiver支付API工具类，封装所有支付相关HTTP请求
 * 自动加上Authorization和多语言Header
 */
@Component
public class IdrApiClient {

    @Value("${idr.api.base-url}")
    private String baseUrl;

    @Value("${idr.api.secret}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送GET请求
     */
    public ResponseEntity<String> get(String path, Map<String, String> params, String locale) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        String url = builder.toUriString();
        HttpHeaders headers = buildHeaders(locale);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // 日志输出
        System.out.println("[IdrApiClient][GET] url: " + url);
        System.out.println("[IdrApiClient][GET] headers: " + headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("[IdrApiClient][GET] response: " + response.getBody());
        return response;
    }

    /**
     * 发送POST请求
     */
    public ResponseEntity<String> post(String path, Object body, String locale) {
        String url = baseUrl + path;
        HttpHeaders headers = buildHeaders(locale);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        // 日志输出
        System.out.println("[IdrApiClient][POST] url: " + url);
        System.out.println("[IdrApiClient][POST] headers: " + headers);
        System.out.println("[IdrApiClient][POST] body: " + body);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("[IdrApiClient][POST] response: " + response.getBody());
        return response;
    }

    /**
     * 构建请求头
     */
    private HttpHeaders buildHeaders(String locale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", secret); // 只传密钥本身，不加Bearer
        if (locale != null && !locale.isEmpty()) {
            headers.set("X-Idr-Locale", locale);
        } else {
            headers.set("X-Idr-Locale", "zh-cn");
        }
        return headers;
    }
} 