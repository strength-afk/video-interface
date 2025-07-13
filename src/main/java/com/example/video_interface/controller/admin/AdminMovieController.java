package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminMovieDTO;
import com.example.video_interface.service.admin.IAdminMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台电影控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final IAdminMovieService movieService;

    /**
     * 获取电影列表
     * @param page 页码
     * @param size 页面大小
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @param regionId 地区ID
     * @param status 状态
     * @param chargeType 收费类型
     * @return 电影列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getMovieList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String chargeType) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<AdminMovieDTO> movies = movieService.getMovieList(pageable, keyword, categoryId, 
                                                                  regionId, status, chargeType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", Map.of(
                "items", movies.getContent(),
                "total", movies.getTotalElements(),
                "page", page,
                "size", size,
                "totalPages", movies.getTotalPages()
            ));
            
            log.debug("获取电影列表成功，共{}条记录", movies.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取电影列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取电影列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取电影详情
     * @param id 电影ID
     * @return 电影详情
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getMovieById(@RequestParam Long id) {
        try {
            AdminMovieDTO movie = movieService.getMovieById(id);
            log.debug("获取电影详情成功: {}", movie.getTitle());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", movie
            ));
        } catch (IllegalArgumentException e) {
            log.warn("获取电影详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取电影详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取电影详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 创建电影
     * @param movieDTO 电影信息
     * @return 创建后的电影
     */
    @PostMapping("/create")
    public ResponseEntity<?> createMovie(@RequestBody AdminMovieDTO movieDTO) {
        try {
            log.debug("🔍 接收到创建电影请求: {}", movieDTO);
            log.debug("🔍 电影标题: {}", movieDTO.getTitle());
            log.debug("🔍 电影分类ID: {}", movieDTO.getCategoryId());
            log.debug("🔍 电影地区ID: {}", movieDTO.getRegionId());
            log.debug("🔍 收费类型: {}", movieDTO.getChargeType());
            log.debug("🔍 状态: {}", movieDTO.getStatus());
            
            AdminMovieDTO created = movieService.createMovie(movieDTO);
            log.debug("创建电影成功: {}", created.getTitle());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", created,
                "message", "创建成功"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("创建电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("创建电影失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage() != null ? e.getMessage() : "创建电影失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新电影
     * @param movieDTO 电影信息
     * @return 更新后的电影
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateMovie(@RequestBody AdminMovieDTO movieDTO) {
        try {
            if (movieDTO.getId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "电影ID不能为空"
                ));
            }
            
            AdminMovieDTO updated = movieService.updateMovie(movieDTO.getId(), movieDTO);
            log.debug("更新电影成功: {}", updated.getTitle());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", "更新成功"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("更新电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("更新电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新电影失败，请稍后重试"
            ));
        }
    }

    /**
     * 删除电影
     * @param requestBody 包含id的请求体
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMovie(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            movieService.deleteMovie(id);
            log.debug("删除电影成功: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("删除电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("删除电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除电影失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新电影状态
     * @param requestBody 包含id和status的请求体
     * @return 更新后的电影
     */
    @PostMapping("/status")
    public ResponseEntity<?> updateMovieStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            String status = requestBody.get("status").toString();
            
            AdminMovieDTO updated = movieService.updateMovieStatus(id, status);
            log.debug("更新电影状态成功: {} -> {}", updated.getTitle(), status);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", String.format("电影状态已更新为%s", status)
            ));
        } catch (IllegalArgumentException e) {
            log.warn("更新电影状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("更新电影状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新电影状态失败，请稍后重试"
            ));
        }
    }

    /**
     * 设置推荐状态
     * @param requestBody 包含id和isRecommended的请求体
     * @return 更新后的电影
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> setRecommendStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            Boolean isRecommended = Boolean.valueOf(requestBody.get("isRecommended").toString());
            
            AdminMovieDTO updated = movieService.setRecommendStatus(id, isRecommended);
            log.debug("设置电影推荐状态成功: {} -> {}", updated.getTitle(), isRecommended);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", String.format("电影推荐状态已%s", isRecommended ? "设置" : "取消")
            ));
        } catch (IllegalArgumentException e) {
            log.warn("设置电影推荐状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("设置电影推荐状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "设置电影推荐状态失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 设置超级推荐状态
     * @param requestBody 包含id和isSuperRecommended的请求体
     * @return 更新后的电影
     */
    @PostMapping("/super-recommend")
    public ResponseEntity<?> setSuperRecommendStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            Boolean isSuperRecommended = Boolean.valueOf(requestBody.get("isSuperRecommended").toString());
            
            AdminMovieDTO updated = movieService.setSuperRecommendStatus(id, isSuperRecommended);
            log.debug("设置电影超级推荐状态成功: {} -> {}", updated.getTitle(), isSuperRecommended);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", String.format("电影超级推荐状态已%s", isSuperRecommended ? "设置" : "取消")
            ));
        } catch (IllegalArgumentException e) {
            log.warn("设置电影超级推荐状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("设置电影超级推荐状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "设置电影超级推荐状态失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 获取超级推荐电影列表
     * @return 超级推荐电影列表
     */
    @GetMapping("/super-recommended")
    public ResponseEntity<?> getSuperRecommendedMovies() {
        try {
            List<AdminMovieDTO> movies = movieService.getSuperRecommendedMovies();
            log.debug("获取超级推荐电影列表成功，共{}部电影", movies.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", movies
            ));
        } catch (Exception e) {
            log.error("获取超级推荐电影列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取超级推荐电影列表失败，请稍后重试"
            ));
        }
    }
    

} 