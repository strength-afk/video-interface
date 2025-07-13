package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5MoviePurchaseRequest;
import com.example.video_interface.dto.h5.H5MoviePurchaseResponse;
import com.example.video_interface.model.Movie;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.MovieRepository;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.service.h5.IH5MoviePurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * H5端VIP电影单片购买服务实现类
 */
@Slf4j
@Service
public class H5MoviePurchaseServiceImpl implements IH5MoviePurchaseService {
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public H5MoviePurchaseResponse createPurchaseOrder(H5MoviePurchaseRequest request) {
        log.info("创建VIP电影单片购买订单，电影ID: {}, 用户ID: {}", request.getMovieId(), request.getUserId());
        
        try {
            // 验证电影是否存在且为VIP电影
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
            
            if (movie.getChargeType() != Movie.ChargeType.VIP) {
                return H5MoviePurchaseResponse.builder()
                        .success(false)
                        .message("该电影不是VIP电影")
                        .build();
            }
            
            // 验证用户是否存在
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            // 检查用户是否已经是VIP
            if (Boolean.TRUE.equals(user.getIsVip())) {
                return H5MoviePurchaseResponse.builder()
                        .success(false)
                        .message("您已是VIP会员，无需购买此电影")
                        .build();
            }
            
            // 检查用户是否已购买过此电影
            if (hasUserPurchasedMovie(request.getUserId(), request.getMovieId())) {
                return H5MoviePurchaseResponse.builder()
                        .success(false)
                        .message("您已购买过此电影")
                        .build();
            }
            
            // 生成订单ID
            String orderId = generateOrderId();
            
            // 获取电影价格
            BigDecimal price = movie.getPrice() != null ? movie.getPrice() : BigDecimal.valueOf(9.9);
            
            // TODO: 这里应该调用真实的支付接口
            // 目前返回模拟的支付信息
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + orderId;
            String paymentUrl = "https://pay.example.com/pay?orderId=" + orderId;
            
            log.info("创建购买订单成功，订单ID: {}, 价格: {}", orderId, price);
            
            return H5MoviePurchaseResponse.builder()
                    .success(true)
                    .message("订单创建成功")
                    .orderId(orderId)
                    .amount(price.toString())
                    .qrCodeUrl(qrCodeUrl)
                    .paymentUrl(paymentUrl)
                    .paymentStatus("PENDING")
                    .build();
                    
        } catch (Exception e) {
            log.error("创建购买订单失败", e);
            return H5MoviePurchaseResponse.builder()
                    .success(false)
                    .message("创建订单失败：" + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public String queryPaymentStatus(String orderId) {
        log.info("查询支付状态，订单ID: {}", orderId);
        
        // TODO: 这里应该调用真实的支付状态查询接口
        // 目前返回模拟状态
        return "PENDING";
    }
    
    @Override
    public boolean hasUserPurchasedMovie(Long userId, Long movieId) {
        log.debug("检查用户是否已购买电影，用户ID: {}, 电影ID: {}", userId, movieId);
        
        // TODO: 这里应该查询订单表，检查用户是否已购买该电影
        // 目前返回false，表示未购买
        return false;
    }
    
    /**
     * 生成订单ID
     * @return 订单ID
     */
    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "MOVIE_" + timestamp + "_" + random;
    }
} 