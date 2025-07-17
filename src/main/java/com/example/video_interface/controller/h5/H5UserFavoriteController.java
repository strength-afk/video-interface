package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5UserFavoriteDTO;
import com.example.video_interface.service.h5.IH5UserFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端用户收藏控制器
 */
@Slf4j
@RestController
@RequestMapping("/h5/favorites")
@RequiredArgsConstructor
public class H5UserFavoriteController {
    
    private final IH5UserFavoriteService userFavoriteService;
    
    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public ResponseEntity<Boolean> addFavorite(@RequestBody H5UserFavoriteDTO favoriteDTO) {
        
        log.info("添加收藏，用户ID: {}, 内容ID: {}, 内容类型: {}", favoriteDTO.getUserId(), favoriteDTO.getContentId(), favoriteDTO.getContentType());
        
        try {
            boolean success = userFavoriteService.addFavorite(
                favoriteDTO.getUserId(), 
                favoriteDTO.getContentId(), 
                favoriteDTO.getContentType(), 
                favoriteDTO.getContentTitle(), 
                favoriteDTO.getContentCover()
            );
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            log.error("添加收藏异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(false);
        }
    }
    
    /**
     * 取消收藏
     */
    @PostMapping("/remove")
    public ResponseEntity<Boolean> removeFavorite(@RequestBody H5UserFavoriteDTO favoriteDTO) {
        
        log.info("取消收藏，用户ID: {}, 内容ID: {}, 内容类型: {}", favoriteDTO.getUserId(), favoriteDTO.getContentId(), favoriteDTO.getContentType());
        
        try {
            boolean success = userFavoriteService.removeFavorite(favoriteDTO.getUserId(), favoriteDTO.getContentId(), favoriteDTO.getContentType());
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            log.error("取消收藏异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(false);
        }
    }
    
    /**
     * 检查用户是否已收藏指定内容
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUserFavorited(
            @RequestParam Long userId,
            @RequestParam Long contentId,
            @RequestParam String contentType) {
        
        log.debug("检查用户收藏状态，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
        
        try {
            boolean favorited = userFavoriteService.checkUserFavorited(userId, contentId, contentType);
            return ResponseEntity.ok(favorited);
        } catch (Exception e) {
            log.error("检查用户收藏状态异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(false);
        }
    }
    
    /**
     * 获取用户收藏列表（分页）
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<H5UserFavoriteDTO>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("获取用户收藏列表，用户ID: {}, 内容类型: {}, 页码: {}, 每页数量: {}", userId, contentType, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<H5UserFavoriteDTO> favorites = userFavoriteService.getUserFavorites(userId, contentType, pageable);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            log.error("获取用户收藏列表异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取用户收藏的内容ID列表
     */
    @GetMapping("/user/{userId}/content-ids")
    public ResponseEntity<List<Long>> getUserFavoriteContentIds(
            @PathVariable Long userId,
            @RequestParam String contentType) {
        
        log.debug("获取用户收藏的内容ID列表，用户ID: {}, 内容类型: {}", userId, contentType);
        
        try {
            List<Long> contentIds = userFavoriteService.getUserFavoriteContentIds(userId, contentType);
            return ResponseEntity.ok(contentIds);
        } catch (Exception e) {
            log.error("获取用户收藏的内容ID列表异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取用户收藏总数
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserFavoriteCount(@PathVariable Long userId) {
        log.debug("获取用户收藏总数，用户ID: {}", userId);
        
        try {
            long count = userFavoriteService.getUserFavoriteCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("获取用户收藏总数异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取用户指定类型内容的收藏总数
     */
    @GetMapping("/user/{userId}/count-by-type")
    public ResponseEntity<Long> getUserFavoriteCountByType(
            @PathVariable Long userId,
            @RequestParam String contentType) {
        
        log.debug("获取用户指定类型内容的收藏总数，用户ID: {}, 内容类型: {}", userId, contentType);
        
        try {
            long count = userFavoriteService.getUserFavoriteCountByType(userId, contentType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("获取用户指定类型内容的收藏总数异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取内容的收藏数量
     */
    @GetMapping("/content/{contentId}/count")
    public ResponseEntity<Long> getContentFavoriteCount(
            @PathVariable Long contentId,
            @RequestParam String contentType) {
        
        log.debug("获取内容的收藏数量，内容ID: {}, 内容类型: {}", contentId, contentType);
        
        try {
            long count = userFavoriteService.getContentFavoriteCount(contentId, contentType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("获取内容的收藏数量异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 批量检查用户收藏状态
     */
    @PostMapping("/user/{userId}/batch-check")
    public ResponseEntity<List<Long>> batchCheckUserFavorited(
            @PathVariable Long userId,
            @RequestParam String contentType,
            @RequestBody List<Long> contentIds) {
        
        log.debug("批量检查用户收藏状态，用户ID: {}, 内容ID数量: {}, 内容类型: {}", userId, contentIds.size(), contentType);
        
        try {
            List<Long> favoritedIds = userFavoriteService.batchCheckUserFavorited(userId, contentIds, contentType);
            return ResponseEntity.ok(favoritedIds);
        } catch (Exception e) {
            log.error("批量检查用户收藏状态异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 