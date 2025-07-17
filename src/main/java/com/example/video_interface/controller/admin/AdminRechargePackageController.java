package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminRechargePackageDTO;
import com.example.video_interface.service.admin.IAdminRechargePackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 管理后台充值套餐管理控制器
 * 提供充值套餐的增删改查和上下架接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/recharge-packages")
@RequiredArgsConstructor
public class AdminRechargePackageController {
    private final IAdminRechargePackageService rechargePackageService;

    /**
     * 分页查询充值套餐列表
     * POST /admin/recharge-packages/list
     */
    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 0;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;
        Page<AdminRechargePackageDTO> result = rechargePackageService.getRechargePackageList(params, PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                        "content", result.getContent(),
                        "totalElements", result.getTotalElements(),
                        "totalPages", result.getTotalPages(),
                        "page", result.getNumber(),
                        "size", result.getSize()
                ),
                "message", "查询成功"
        ));
    }

    /**
     * 获取充值套餐详情
     * POST /admin/recharge-packages/detail
     */
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        AdminRechargePackageDTO dto = rechargePackageService.getRechargePackageDetail(id);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", dto,
                "message", "查询成功"
        ));
    }

    /**
     * 创建充值套餐
     * POST /admin/recharge-packages/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AdminRechargePackageDTO dto) {
        AdminRechargePackageDTO created = rechargePackageService.createRechargePackage(dto);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", created,
                "message", "创建成功"
        ));
    }

    /**
     * 更新充值套餐
     * POST /admin/recharge-packages/update
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AdminRechargePackageDTO dto) {
        AdminRechargePackageDTO updated = rechargePackageService.updateRechargePackage(dto);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", "更新成功"
        ));
    }

    /**
     * 删除充值套餐（逻辑删除）
     * POST /admin/recharge-packages/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        boolean success = rechargePackageService.deleteRechargePackage(id);
        return ResponseEntity.ok(Map.of(
                "code", success ? 200 : 400,
                "message", success ? "删除成功" : "删除失败"
        ));
    }

    /**
     * 上下架充值套餐
     * POST /admin/recharge-packages/toggle-status
     */
    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString();
        boolean success = rechargePackageService.toggleRechargePackageStatus(id, status);
        return ResponseEntity.ok(Map.of(
                "code", success ? 200 : 400,
                "message", success ? "操作成功" : "操作失败"
        ));
    }

    /**
     * 获取所有充值套餐（供激活码管理使用）
     * GET /admin/recharge-packages/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        log.info("收到获取所有充值套餐请求");
        try {
            var packages = rechargePackageService.getAllRechargePackages();
            log.info("返回 {} 个充值套餐", packages.size());
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "data", packages,
                    "message", "查询成功"
            ));
        } catch (Exception e) {
            log.error("获取充值套餐失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "获取充值套餐失败: " + e.getMessage()
            ));
        }
    }
} 