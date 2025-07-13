package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5MovieDTO;
import com.example.video_interface.dto.h5.H5MovieDetailDTO;
import com.example.video_interface.dto.h5.H5MoviePlayRequest;
import com.example.video_interface.dto.h5.H5MoviePlayResponse;
import com.example.video_interface.service.h5.IH5MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端电影控制器
 */
@Slf4j
@RestController
@RequestMapping("/h5/movies")
@RequiredArgsConstructor
public class H5MovieController {
    
    private final IH5MovieService movieService;
    
    /**
     * 获取电影详情（简单版本）
     */
    @GetMapping("/detail-simple")
    public ResponseEntity<H5MovieDTO> getMovieDetail(@RequestParam Long id) {
        log.info("获取电影详情，ID: {}", id);
        H5MovieDTO movie = movieService.getMovieById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }
    
        /**
     * 获取热门电影列表
     */
    @GetMapping("/hot")
    public ResponseEntity<Page<H5MovieDTO>> getHotMovies(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sort) {
        log.info("获取热门电影列表，限制数量: {}, 页码: {}, 排序: {}", limit, page, sort);
        Pageable pageable = PageRequest.of(page, limit);
        Page<H5MovieDTO> movies = movieService.getHotMovies(pageable, sort);
        return ResponseEntity.ok(movies);
    }

    /**
     * 获取最新电影列表
     */
    @GetMapping("/new")
    public ResponseEntity<Page<H5MovieDTO>> getNewMovies(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sort) {
        log.info("获取最新电影列表，限制数量: {}, 页码: {}, 排序: {}", limit, page, sort);
        Pageable pageable = PageRequest.of(page, limit);
        Page<H5MovieDTO> movies = movieService.getNewMovies(pageable, sort);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 获取高评分电影列表
     */
    @GetMapping("/high-rated")
    public ResponseEntity<List<H5MovieDTO>> getHighRatedMovies(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "7.0") Double minRating) {
        log.info("获取高评分电影列表，限制数量: {}, 最低评分: {}", limit, minRating);
        List<H5MovieDTO> movies = movieService.getHighRatedMovies(limit, minRating);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 获取超级推荐电影列表
     */
    @GetMapping("/super-recommended")
    public ResponseEntity<List<H5MovieDTO>> getSuperRecommendedMovies(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("获取超级推荐电影列表，限制数量: {}", limit);
        List<H5MovieDTO> movies = movieService.getSuperRecommendedMovies(limit);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据分类获取电影列表
     */
    @GetMapping("/category")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByCategory(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据分类获取电影列表，分类ID: {}, 页码: {}, 每页数量: {}", categoryId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByCategory(categoryId, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据地区获取电影列表
     */
    @GetMapping("/region")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByRegion(
            @RequestParam Long regionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据地区获取电影列表，地区ID: {}, 页码: {}, 每页数量: {}", regionId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByRegion(regionId, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据年份获取电影列表
     */
    @GetMapping("/year")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByYear(
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据年份获取电影列表，年份: {}, 页码: {}, 每页数量: {}", year, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByYear(year, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据收费类型获取电影列表
     */
    @GetMapping("/charge-type")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByChargeType(
            @RequestParam String chargeType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据收费类型获取电影列表，收费类型: {}, 页码: {}, 每页数量: {}", chargeType, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByChargeType(chargeType, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据VIP状态获取电影列表
     */
    @GetMapping("/vip")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByVipStatus(
            @RequestParam Boolean isVip,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据VIP状态获取电影列表，VIP状态: {}, 页码: {}, 每页数量: {}", isVip, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByVipStatus(isVip, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据免费状态获取电影列表
     */
    @GetMapping("/free")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByFreeStatus(
            @RequestParam Boolean isFree,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        log.info("根据免费状态获取电影列表，免费状态: {}, 页码: {}, 每页数量: {}, 排序: {}", isFree, page, size, sort);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByFreeStatus(isFree, pageable, sort);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据画质获取电影列表
     */
    @GetMapping("/quality")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByQuality(
            @RequestParam String quality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据画质获取电影列表，画质: {}, 页码: {}, 每页数量: {}", quality, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByQuality(quality, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 搜索电影
     */
    @GetMapping("/search")
    public ResponseEntity<Page<H5MovieDTO>> searchMovies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("搜索电影，关键词: {}, 页码: {}, 每页数量: {}", keyword, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.searchMovies(keyword, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 根据标签搜索电影
     */
    @GetMapping("/search/tag")
    public ResponseEntity<Page<H5MovieDTO>> searchMoviesByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("根据标签搜索电影，标签: {}, 页码: {}, 每页数量: {}", tag, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.searchMoviesByTag(tag, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 复合条件查询电影
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<H5MovieDTO>> getMoviesByConditions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String chargeType,
            @RequestParam(required = false) Boolean isVip,
            @RequestParam(required = false) String quality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("复合条件查询电影，分类ID: {}, 地区ID: {}, 年份: {}, 收费类型: {}, VIP状态: {}, 画质: {}, 页码: {}, 每页数量: {}", 
                categoryId, regionId, releaseYear, chargeType, isVip, quality, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getMoviesByConditions(categoryId, regionId, releaseYear, 
                                                                    chargeType, isVip, quality, pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 获取所有电影（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<H5MovieDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("获取所有电影列表，页码: {}, 每页数量: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 获取电影排行榜
     */
    @GetMapping("/ranking")
    public ResponseEntity<Page<H5MovieDTO>> getRankingMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "views") String sortBy,
            @RequestParam(defaultValue = "all") String timeRange) {
        log.info("获取电影排行榜，页码: {}, 每页数量: {}, 排序方式: {}, 时间范围: {}", 
                page, size, sortBy, timeRange);
        Pageable pageable = PageRequest.of(page, size);
        Page<H5MovieDTO> movies = movieService.getRankingMovies(pageable, sortBy, timeRange);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * 记录观看次数
     */
    @PostMapping("/view")
    public ResponseEntity<Void> recordView(@RequestParam Long movieId) {
        log.info("记录电影观看次数，电影ID: {}", movieId);
        movieService.recordView(movieId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 点赞电影
     */
    @PostMapping("/like")
    public ResponseEntity<Boolean> likeMovie(@RequestParam Long movieId, @RequestParam Long userId) {
        log.info("点赞电影，电影ID: {}, 用户ID: {}", movieId, userId);
        boolean success = movieService.likeMovie(movieId, userId);
        return ResponseEntity.ok(success);
    }
    
    /**
     * 取消点赞电影
     */
    @PostMapping("/unlike")
    public ResponseEntity<Boolean> unlikeMovie(@RequestParam Long movieId, @RequestParam Long userId) {
        log.info("取消点赞电影，电影ID: {}, 用户ID: {}", movieId, userId);
        boolean success = movieService.unlikeMovie(movieId, userId);
        return ResponseEntity.ok(success);
    }
    
    /**
     * 收藏电影
     */
    @PostMapping("/favorite")
    public ResponseEntity<Boolean> favoriteMovie(@RequestParam Long movieId, @RequestParam Long userId) {
        log.info("收藏电影，电影ID: {}, 用户ID: {}", movieId, userId);
        boolean success = movieService.favoriteMovie(movieId, userId);
        return ResponseEntity.ok(success);
    }
    
    /**
     * 取消收藏电影
     */
    @PostMapping("/unfavorite")
    public ResponseEntity<Boolean> unfavoriteMovie(@RequestParam Long movieId, @RequestParam Long userId) {
        log.info("取消收藏电影，电影ID: {}, 用户ID: {}", movieId, userId);
        boolean success = movieService.unfavoriteMovie(movieId, userId);
        return ResponseEntity.ok(success);
    }
    
    /**
     * 检查观看权限
     */
    @GetMapping("/permission")
    public ResponseEntity<IH5MovieService.MovieWatchPermission> checkWatchPermission(
            @RequestParam Long movieId, 
            @RequestParam(required = false) Long userId) {
        log.info("检查观看权限，电影ID: {}, 用户ID: {}", movieId, userId);
        IH5MovieService.MovieWatchPermission permission = movieService.checkWatchPermission(movieId, userId);
        return ResponseEntity.ok(permission);
    }
    
    /**
     * 获取筛选选项
     */
    @GetMapping("/filters")
    public ResponseEntity<IH5MovieService.MovieFilterOptions> getFilterOptions() {
        log.info("获取电影筛选选项");
        IH5MovieService.MovieFilterOptions options = movieService.getFilterOptions();
        return ResponseEntity.ok(options);
    }
    
    // ==================== 新增电影详情和播放功能接口 ====================
    
    /**
     * 获取电影详情（用于详情页）
     */
    @GetMapping("/{movieId}/detail-page")
    public ResponseEntity<H5MovieDetailDTO> getMovieDetailForPage(
            @PathVariable Long movieId,
            @RequestParam(required = false) Long userId) {
        log.debug("获取电影详情，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            H5MovieDetailDTO movieDetail = movieService.getMovieDetail(movieId, userId);
            return ResponseEntity.ok(movieDetail);
        } catch (IllegalArgumentException e) {
            log.warn("获取电影详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("获取电影详情异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查播放权限
     */
    @PostMapping("/{movieId}/play-permission")
    public ResponseEntity<H5MoviePlayResponse> checkPlayPermission(
            @PathVariable Long movieId,
            @RequestBody H5MoviePlayRequest request) {
        log.debug("检查播放权限，电影ID: {}, 请求: {}", movieId, request);
        
        // 设置电影ID
        request.setMovieId(movieId);
        
        try {
            H5MoviePlayResponse response = movieService.checkPlayPermission(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("检查播放权限失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("检查播放权限异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取相关推荐电影
     */
    @GetMapping("/{movieId}/related")
    public ResponseEntity<List<H5MovieDetailDTO>> getRelatedMovies(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "4") Integer limit) {
        log.debug("获取相关推荐电影，电影ID: {}, 限制数量: {}", movieId, limit);
        
        try {
            List<H5MovieDetailDTO> relatedMovies = movieService.getRelatedMovies(movieId, limit);
            return ResponseEntity.ok(relatedMovies);
        } catch (IllegalArgumentException e) {
            log.warn("获取相关推荐电影失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("获取相关推荐电影异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 增加观看次数
     */
    @PostMapping("/{movieId}/increment-view")
    public ResponseEntity<Void> incrementViews(@PathVariable Long movieId) {
        log.debug("增加观看次数，电影ID: {}", movieId);
        
        try {
            movieService.incrementViews(movieId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("增加观看次数失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("增加观看次数异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ==================== 试看功能接口 ====================
    
    /**
     * 获取试看视频URL
     */
    @GetMapping("/{movieId}/trial/video")
    public ResponseEntity<IH5MovieService.TrialVideoInfo> getTrialVideoUrl(
            @PathVariable Long movieId,
            @RequestParam(required = false) Long userId) {
        
        log.info("获取试看视频URL，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            IH5MovieService.TrialVideoInfo trialInfo = movieService.getTrialVideoUrl(movieId, userId);
            
            if (trialInfo.getCanTrial()) {
                return ResponseEntity.ok(trialInfo);
            } else {
                return ResponseEntity.badRequest().body(trialInfo);
            }
        } catch (Exception e) {
            log.error("获取试看视频URL异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 检查试看权限
     */
    @GetMapping("/{movieId}/trial/permission")
    public ResponseEntity<Boolean> checkTrialPermission(
            @PathVariable Long movieId,
            @RequestParam(required = false) Long userId) {
        
        log.debug("检查试看权限，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            boolean hasPermission = movieService.hasTrialPermission(movieId, userId);
            return ResponseEntity.ok(hasPermission);
        } catch (Exception e) {
            log.error("检查试看权限异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 