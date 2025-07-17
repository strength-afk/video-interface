package com.example.video_interface.service.common;

import java.util.Map;

/**
 * iDataRiver支付服务接口，定义下单、支付、查单、回调等方法
 */
public interface IIdrPaymentService {
    /**
     * 创建商品订单
     * @param projectId 项目ID
     * @param skuId 商品ID
     * @param quantity 购买数量
     * @param contactInfo 联系方式（如邮箱、手机号）
     * @return 下单结果（包含订单ID等信息）
     */
    Map<String, Object> createOrder(Map<String, Object> params);

    /**
     * 获取订单支付链接
     * @param orderId 订单ID
     * @param method 支付方式（如alipay、wechat等，需从订单详情获取）
     * @param redirectUrl 支付成功后跳转链接
     * @param callbackUrl 支付/退款回调通知链接
     * @return 支付链接等信息
     */
    Map<String, Object> getPayUrl(Map<String, Object> params);

    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Map<String, Object> queryOrder(String orderId);

    /**
     * 处理支付回调
     * @param callbackData 回调数据
     * @return 处理结果
     */
    boolean handleCallback(Map<String, Object> callbackData);
} 