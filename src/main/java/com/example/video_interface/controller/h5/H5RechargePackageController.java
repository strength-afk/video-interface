package com.example.video_interface.controller.h5;

import com.example.video_interface.service.h5.IH5RechargePackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * H5端充值套餐查询接口
 */
@Slf4j
@RestController
@RequestMapping("/h5/recharge/packages")
@RequiredArgsConstructor
public class H5RechargePackageController {
    private final IH5RechargePackageService rechargePackageService;

    /**
     * 获取所有ACTIVE状态的充值套餐
     * GET /h5/recharge/packages
     * @return 充值套餐列表
     */
    @GetMapping
    public ResponseEntity<?> getActiveRechargePackages() {
        try {
            return ResponseEntity.ok(rechargePackageService.getActiveRechargePackages());
        } catch (Exception e) {
            log.error("获取充值套餐列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("获取充值套餐失败，请稍后重试");
        }
    }
} 