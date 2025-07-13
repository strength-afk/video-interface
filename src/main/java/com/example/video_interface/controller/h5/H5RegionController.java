package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5RegionDTO;
import com.example.video_interface.service.h5.IH5RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * H5端地区控制器
 */
@Slf4j
@RestController
@RequestMapping("/h5/regions")
@RequiredArgsConstructor
public class H5RegionController {

    private final IH5RegionService regionService;

    /**
     * 获取所有启用的地区列表
     * @return 地区列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllRegions() {
        try {
            List<H5RegionDTO> regions = regionService.getAllEnabledRegions();
            log.debug("获取地区列表成功，共{}个地区", regions.size());
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            log.error("获取地区列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取地区列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 根据ID获取地区详情
     * @param id 地区ID
     * @return 地区详情
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getRegionById(@RequestParam Long id) {
        try {
            H5RegionDTO region = regionService.getRegionById(id);
            if (region == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "地区不存在"
                ));
            }
            log.debug("获取地区详情成功: {}", region.getName());
            return ResponseEntity.ok(region);
        } catch (Exception e) {
            log.error("获取地区详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取地区详情失败，请稍后重试"
            ));
        }
    }
} 