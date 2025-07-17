package com.example.video_interface.controller.common;

import com.example.video_interface.service.common.IIdrPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * iDataRiver支付相关接口，供前端调用
 */
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
        return idrPaymentService.getPayUrl(params);
    }

    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/order/info")
    public Map<String, Object> queryOrder(@RequestParam("orderId") String orderId) {
        return idrPaymentService.queryOrder(orderId);
    }

    /**
     * 支付/退款回调接口（供iDataRiver平台调用）
     * @param callbackData 回调数据
     * @return 处理结果
     */
    @PostMapping("/order/callback")
    public String handleCallback(@RequestBody Map<String, Object> callbackData) {
        boolean result = idrPaymentService.handleCallback(callbackData);
        return result ? "success" : "fail";
    }
} 