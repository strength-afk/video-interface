package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5MoviePurchaseRequest;
import com.example.video_interface.dto.h5.H5MoviePurchaseResponse;

/**
 * H5端VIP电影单片购买服务接口
 */
public interface IH5MoviePurchaseService {
    
    /**
     * 创建VIP电影单片购买订单
     * @param request 购买请求
     * @return 购买响应
     */
    H5MoviePurchaseResponse createPurchaseOrder(H5MoviePurchaseRequest request);
    
    /**
     * 查询支付状态
     * @param orderId 订单ID
     * @return 支付状态
     */
    String queryPaymentStatus(String orderId);
    
    /**
     * 检查用户是否已购买电影
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return true if purchased, false otherwise
     */
    boolean hasUserPurchasedMovie(Long userId, Long movieId);
} 