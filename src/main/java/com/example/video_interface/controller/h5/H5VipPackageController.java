package com.example.video_interface.controller.h5;

import com.example.video_interface.service.h5.IH5VipPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * H5端VIP套餐查询接口
 */
@Slf4j
@RestController
@RequestMapping("/h5/vip/packages")
@RequiredArgsConstructor
public class H5VipPackageController {
    private final IH5VipPackageService vipPackageService;

    /**
     * 获取所有ACTIVE状态的VIP套餐
     * GET /h5/vip/packages
     * @return 套餐列表
     */
    @GetMapping
    public ResponseEntity<?> getActiveVipPackages() {
        try {
            return ResponseEntity.ok(vipPackageService.getActiveVipPackages());
        } catch (Exception e) {
            log.error("获取VIP套餐列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("获取VIP套餐失败，请稍后重试");
        }
    }
} 