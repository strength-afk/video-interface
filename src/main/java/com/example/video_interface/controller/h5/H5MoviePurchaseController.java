package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5MoviePurchaseRequest;
import com.example.video_interface.dto.h5.H5MoviePurchaseResponse;
import com.example.video_interface.service.h5.IH5MoviePurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * H5端VIP电影单片购买控制器
 */
@Slf4j
@RestController
@RequestMapping("/h5/movie/purchase")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4000"}, allowCredentials = "true")
public class H5MoviePurchaseController {
    
    @Autowired
    private IH5MoviePurchaseService moviePurchaseService;
    
    /**
     * 创建VIP电影单片购买订单
     * @param request 购买请求
     * @return 购买响应
     */
    @PostMapping("/create")
    public ResponseEntity<H5MoviePurchaseResponse> createPurchaseOrder(@Valid @RequestBody H5MoviePurchaseRequest request) {
        log.info("收到VIP电影单片购买请求，电影ID: {}, 用户ID: {}", request.getMovieId(), request.getUserId());
        
        H5MoviePurchaseResponse response = moviePurchaseService.createPurchaseOrder(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 查询支付状态
     * @param orderId 订单ID
     * @return 支付状态
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<String> queryPaymentStatus(@PathVariable String orderId) {
        log.info("查询支付状态，订单ID: {}", orderId);
        
        String status = moviePurchaseService.queryPaymentStatus(orderId);
        return ResponseEntity.ok(status);
    }
    
    /**
     * 检查用户是否已购买电影
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return 是否已购买
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUserPurchase(@RequestParam Long userId, @RequestParam Long movieId) {
        log.info("检查用户是否已购买电影，用户ID: {}, 电影ID: {}", userId, movieId);
        
        boolean hasPurchased = moviePurchaseService.hasUserPurchasedMovie(userId, movieId);
        return ResponseEntity.ok(hasPurchased);
    }
} 