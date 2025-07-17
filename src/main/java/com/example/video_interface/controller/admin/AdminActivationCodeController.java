package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminActivationCodeDTO;
import com.example.video_interface.service.admin.IAdminActivationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台激活码控制器
 * 处理激活码相关的请求，包括会员激活码和充值激活码的管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/activation-codes")
@RequiredArgsConstructor
public class AdminActivationCodeController {
    
    private final IAdminActivationCodeService activationCodeService;
    
    /**
     * 获取激活码列表
     * @param params 查询参数
     * @return 激活码列表
     */
    @PostMapping("/list")
    public ResponseEntity<?> getActivationCodeList(@RequestBody Map<String, Object> params) {
        log.info("获取激活码列表请求，参数: {}", params);
        
        try {
            // 安全地获取参数值
            Integer page = null;
            Integer size = null;
            String codeType = null;
            String codeStatus = null;
            String batchNumber = null;
            String keyword = null;
            
            if (params.get("page") != null) {
                if (params.get("page") instanceof Integer) {
                    page = (Integer) params.get("page");
                } else {
                    page = Integer.valueOf(params.get("page").toString());
                }
            }
            
            if (params.get("size") != null) {
                if (params.get("size") instanceof Integer) {
                    size = (Integer) params.get("size");
                } else {
                    size = Integer.valueOf(params.get("size").toString());
                }
            }
            
            if (params.get("codeType") != null) {
                String typeStr = params.get("codeType").toString();
                // 如果类型为空字符串，则设为null
                codeType = typeStr.isEmpty() ? null : typeStr;
            }
            
            if (params.get("codeStatus") != null) {
                String statusStr = params.get("codeStatus").toString();
                // 如果状态为空字符串，则设为null
                codeStatus = statusStr.isEmpty() ? null : statusStr;
            }
            
            if (params.get("batchNumber") != null) {
                String batchStr = params.get("batchNumber").toString();
                // 如果批次号为空字符串，则设为null
                batchNumber = batchStr.isEmpty() ? null : batchStr;
            }
            
            if (params.get("keyword") != null) {
                String keywordStr = params.get("keyword").toString();
                // 如果关键词为空字符串，则设为null
                keyword = keywordStr.isEmpty() ? null : keywordStr;
            }
            
            // 设置默认值
            if (page == null) page = 0;
            if (size == null) size = 20;
            
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<AdminActivationCodeDTO> activationCodes = activationCodeService.getActivationCodeList(
                    codeType, codeStatus, batchNumber, keyword, pageRequest);
            
            log.info("获取激活码列表成功，总数: {}", activationCodes.getTotalElements());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", activationCodes,
                "message", "查询成功"
            ));
            
        } catch (Exception e) {
            log.error("获取激活码列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取激活码列表失败：" + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取激活码详情
     * @param id 激活码ID
     * @return 激活码详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivationCodeById(@PathVariable Long id) {
        log.info("获取激活码详情请求 - ID: {}", id);
        
        try {
            AdminActivationCodeDTO activationCode = activationCodeService.getActivationCodeById(id);
            log.info("获取激活码详情成功 - ID: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", activationCode,
                "message", "查询成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("获取激活码详情失败 - ID: {} - {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取激活码详情发生错误 - ID: {} - {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取激活码详情失败，请稍后重试"
            ));
        }
    }
    

    
    /**
     * 批量创建激活码
     * @param params 批量创建参数
     * @return 创建结果
     */
    @PostMapping("/batch-create")
    public ResponseEntity<?> batchCreateActivationCodes(@RequestBody Map<String, Object> params) {
        log.info("批量创建激活码请求，参数: {}", params);
        
        try {
            Map<String, Object> result = activationCodeService.batchCreateActivationCodes(params);
            log.info("批量创建激活码成功 - 数量: {}", result.get("count"));
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", result,
                "message", "批量创建成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("批量创建激活码失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("批量创建激活码发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "批量创建激活码失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 更新激活码
     * @param activationCodeDTO 激活码信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateActivationCode(@RequestBody AdminActivationCodeDTO activationCodeDTO) {
        log.info("更新激活码请求 - ID: {}", activationCodeDTO.getId());
        
        try {
            AdminActivationCodeDTO updatedActivationCode = activationCodeService.updateActivationCode(activationCodeDTO);
            log.info("激活码更新成功 - ID: {}", updatedActivationCode.getId());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updatedActivationCode,
                "message", "更新成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("激活码更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("激活码更新发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "激活码更新失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 删除激活码
     * @param params 删除参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteActivationCode(@RequestBody Map<String, Object> params) {
        log.info("删除激活码请求，参数: {}", params);
        
        try {
            Long id = null;
            if (params.get("id") != null) {
                if (params.get("id") instanceof Integer) {
                    id = ((Integer) params.get("id")).longValue();
                } else {
                    id = Long.valueOf(params.get("id").toString());
                }
            }
            
            if (id == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "激活码ID不能为空"
                ));
            }
            
            boolean result = activationCodeService.deleteActivationCode(id);
            log.info("激活码删除成功 - ID: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("success", result),
                "message", "删除成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("激活码删除失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("激活码删除发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "激活码删除失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 批量删除激活码
     * @param params 批量删除参数
     * @return 删除结果
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<?> batchDeleteActivationCodes(@RequestBody Map<String, Object> params) {
        log.info("批量删除激活码请求，参数: {}", params);
        
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) params.get("ids");
            
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "激活码ID列表不能为空"
                ));
            }
            
            boolean result = activationCodeService.batchDeleteActivationCodes(ids);
            log.info("批量删除激活码成功 - 数量: {}", ids.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("success", result),
                "message", "批量删除成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("批量删除激活码失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("批量删除激活码发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "批量删除激活码失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 切换激活码状态
     * @param params 状态切换参数
     * @return 操作结果
     */
    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleActivationCodeStatus(@RequestBody Map<String, Object> params) {
        log.info("切换激活码状态请求，参数: {}", params);
        
        try {
            Long id = null;
            if (params.get("id") != null) {
                if (params.get("id") instanceof Integer) {
                    id = ((Integer) params.get("id")).longValue();
                } else {
                    id = Long.valueOf(params.get("id").toString());
                }
            }
            
            if (id == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "激活码ID不能为空"
                ));
            }
            
            boolean result = activationCodeService.toggleActivationCodeStatus(id);
            log.info("激活码状态切换成功 - ID: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("success", result),
                "message", "状态切换成功"
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("激活码状态切换失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("激活码状态切换发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "激活码状态切换失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 获取激活码统计信息
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getActivationCodeStatistics() {
        log.info("获取激活码统计信息请求");
        
        try {
            Map<String, Object> statistics = activationCodeService.getActivationCodeStatistics();
            log.info("获取激活码统计信息成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", statistics,
                "message", "查询成功"
            ));
            
        } catch (Exception e) {
            log.error("获取激活码统计信息发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取统计信息失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 根据激活码类型获取统计信息
     * @param codeType 激活码类型
     * @return 统计信息
     */
    @GetMapping("/statistics/{codeType}")
    public ResponseEntity<?> getActivationCodeStatisticsByType(@PathVariable String codeType) {
        log.info("获取激活码统计信息请求 - 类型: {}", codeType);
        
        try {
            Map<String, Object> statistics = activationCodeService.getActivationCodeStatisticsByType(codeType);
            log.info("获取激活码统计信息成功 - 类型: {}", codeType);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", statistics,
                "message", "查询成功"
            ));
            
        } catch (Exception e) {
            log.error("获取激活码统计信息发生错误 - 类型: {} - {}", codeType, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取统计信息失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 导出激活码
     * @param params 导出参数
     * @return 导出数据
     */
    @PostMapping("/export")
    public ResponseEntity<?> exportActivationCodes(@RequestBody Map<String, Object> params) {
        log.info("导出激活码请求，参数: {}", params);
        
        try {
            String codeType = (String) params.get("codeType");
            String codeStatus = (String) params.get("codeStatus");
            String batchNumber = (String) params.get("batchNumber");
            
            List<AdminActivationCodeDTO> activationCodes = activationCodeService.exportActivationCodes(
                    codeType, codeStatus, batchNumber);
            
            log.info("导出激活码成功 - 数量: {}", activationCodes.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", activationCodes,
                "message", "导出成功"
            ));
            
        } catch (Exception e) {
            log.error("导出激活码发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "导出激活码失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 生成激活码
     * @param params 生成参数
     * @return 生成的激活码
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateActivationCode(@RequestBody Map<String, Object> params) {
        log.info("生成激活码请求，参数: {}", params);
        
        try {
            String codeType = (String) params.get("codeType");
            Integer length = (Integer) params.get("length");
            
            if (codeType == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "激活码类型不能为空"
                ));
            }
            
            if (length == null) {
                length = 16;
            }
            
            String generatedCode = activationCodeService.generateActivationCode(codeType, length);
            log.info("激活码生成成功: {}", generatedCode);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("code", generatedCode),
                "message", "生成成功"
            ));
            
        } catch (Exception e) {
            log.error("生成激活码发生错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "生成激活码失败，请稍后重试"
            ));
        }
    }
} 