package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminVipPackageDTO;
import com.example.video_interface.service.admin.IAdminVipPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 管理后台VIP套餐管理控制器
 * 提供VIP套餐的增删改查和上下架接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/vip-packages")
@RequiredArgsConstructor
public class AdminVipPackageController {
    private final IAdminVipPackageService vipPackageService;

    /**
     * 分页查询VIP套餐列表
     * POST /admin/vip-packages/list
     */
    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 0;
        int size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;
        Page<AdminVipPackageDTO> result = vipPackageService.getVipPackageList(params, PageRequest.of(page, size));
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
     * 获取VIP套餐详情
     * POST /admin/vip-packages/detail
     */
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        AdminVipPackageDTO dto = vipPackageService.getVipPackageDetail(id);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", dto,
                "message", "查询成功"
        ));
    }

    /**
     * 创建VIP套餐
     * POST /admin/vip-packages/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AdminVipPackageDTO dto) {
        AdminVipPackageDTO created = vipPackageService.createVipPackage(dto);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", created,
                "message", "创建成功"
        ));
    }

    /**
     * 更新VIP套餐
     * POST /admin/vip-packages/update
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AdminVipPackageDTO dto) {
        AdminVipPackageDTO updated = vipPackageService.updateVipPackage(dto);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", "更新成功"
        ));
    }

    /**
     * 删除VIP套餐（逻辑删除）
     * POST /admin/vip-packages/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        boolean success = vipPackageService.deleteVipPackage(id);
        return ResponseEntity.ok(Map.of(
                "code", success ? 200 : 400,
                "message", success ? "删除成功" : "删除失败"
        ));
    }

    /**
     * 上下架VIP套餐
     * POST /admin/vip-packages/toggle-status
     */
    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString();
        boolean success = vipPackageService.toggleVipPackageStatus(id, status);
        return ResponseEntity.ok(Map.of(
                "code", success ? 200 : 400,
                "message", success ? "操作成功" : "操作失败"
        ));
    }
} 