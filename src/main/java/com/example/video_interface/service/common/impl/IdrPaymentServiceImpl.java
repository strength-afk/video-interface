package com.example.video_interface.service.common.impl;
import com.example.video_interface.service.common.IdrApiClient;
import com.example.video_interface.service.common.IIdrPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * iDataRiver支付服务实现类，负责下单、支付、查单、回调等业务逻辑
 */
@Service
public class IdrPaymentServiceImpl implements IIdrPaymentService {

    @Autowired
    private IdrApiClient idrApiClient;

    @Value("${idr.api.projectId}")
    private String buymeabtcProjectId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建商品订单
     */
    @Override
    public Map<String, Object> createOrder(Map<String, Object> params) {
        Map<String, Object> body = new HashMap<>();
        // BUYMEABTC特殊处理
        body.put("projectId", buymeabtcProjectId);
        body.put("orderInfo", params);
        ResponseEntity<String> response = idrApiClient.post("/mapi/order/add", body, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 获取订单支付链接
     */
    @Override
    public Map<String, Object> getPayUrl(Map<String, Object> params) {
        ResponseEntity<String> response = idrApiClient.post("/mapi/order/pay", params, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 查询订单详情
     */
    @Override
    public Map<String, Object> queryOrder(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", orderId);
        ResponseEntity<String> response = idrApiClient.get("/mapi/order/info", params, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 处理支付回调（这里只做简单示例，实际应校验签名并同步订单状态）
     */
    @Override
    public boolean handleCallback(Map<String, Object> callbackData) {
        // 实际业务应校验回调数据、校验订单状态、同步本地订单
        // 这里只做简单返回true
        return true;
    }

    /**
     * 解析API响应，返回Map结构
     */
    private Map<String, Object> parseResponse(ResponseEntity<String> response) {
        try {
            if (response != null && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
} 