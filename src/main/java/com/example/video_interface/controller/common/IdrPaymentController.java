package com.example.video_interface.controller.common;

import com.example.video_interface.service.common.IIdrPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * iDataRiver支付相关接口，供前端调用
 */
@Slf4j
@RestController
@RequestMapping("/payment/idr")
public class IdrPaymentController {

    @Autowired
    private IIdrPaymentService idrPaymentService;
    
    /**
     * 获取订单支付链接
     * @param params 包含orderId、method、redirectUrl、callbackUrl
     * @return 支付链接等信息
     */
    @PostMapping("/order/pay")
    public Map<String, Object> getPayUrl(@RequestBody Map<String, Object> params) {
        log.info("获取支付链接请求，参数: {}", params);
        try {
            Map<String, Object> result = idrPaymentService.getPayUrl(params);
            log.info("获取支付链接成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("获取支付链接失败", e);
            return Map.of("code", 500, "msg", "获取支付链接失败");
        }
    }

    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/order/info")
    public Map<String, Object> queryOrder(@RequestParam("orderId") String orderId) {
        log.info("查询订单详情请求，orderId: {}", orderId);
        try {
            Map<String, Object> result = idrPaymentService.queryOrder(orderId);
            log.info("查询订单详情成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("查询订单详情失败，orderId: {}", orderId, e);
            return Map.of("code", 500, "msg", "查询订单详情失败");
        }
    }
    
    /**
     * 支付/退款回调接口（供iDataRiver平台调用）
     * 重要：此接口必须返回200状态码，否则iDataRiver会认为回调失败并重试
     * 
     * @param callbackData 回调数据，包含event、orderId等字段
     * @return 处理结果，"success"表示成功，"fail"表示失败
     */
    @PostMapping("/order/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到iDataRiver支付回调请求，数据: {}", callbackData);
        
        try {
            // 验证回调数据
            if (callbackData == null || callbackData.isEmpty()) {
                log.error("回调数据为空");
                return ResponseEntity.ok("fail");
            }
            
            // 处理回调
            boolean result = idrPaymentService.handleCallback(callbackData);
            
            if (result) {
                log.info("iDataRiver回调处理成功");
                return ResponseEntity.ok("success");
            } else {
                log.error("iDataRiver回调处理失败");
                return ResponseEntity.ok("fail");
            }
            
        } catch (Exception e) {
            log.error("处理iDataRiver回调时发生异常", e);
            // 即使发生异常也要返回200状态码，避免iDataRiver重试
            return ResponseEntity.ok("fail");
        }
    }
} 