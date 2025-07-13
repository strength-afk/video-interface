package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5MovieDTO;
import com.example.video_interface.dto.h5.H5MovieDetailDTO;
import com.example.video_interface.dto.h5.H5MoviePlayRequest;
import com.example.video_interface.dto.h5.H5MoviePlayResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * H5端电影服务接口
 */
public interface IH5MovieService {
    
    /**
     * 获取电影详情
     * @param id 电影ID
     * @return 电影详情
     */
    H5MovieDTO getMovieById(Long id);
    
    /**
     * 获取热门电影列表
     * @param limit 限制数量
     * @return 热门电影列表
     */
    List<H5MovieDTO> getHotMovies(int limit);
    
    /**
     * 获取热门电影列表（分页）
     * @param pageable 分页参数
     * @return 热门电影列表
     */
    Page<H5MovieDTO> getHotMovies(Pageable pageable);
    
    /**
     * 获取热门电影列表（分页，带排序）
     * @param pageable 分页参数
     * @param sort 排序方式
     * @return 热门电影列表
     */
    Page<H5MovieDTO> getHotMovies(Pageable pageable, String sort);
    
    /**
     * 获取最新电影列表
     * @param limit 限制数量
     * @return 最新电影列表
     */
    List<H5MovieDTO> getNewMovies(int limit);
    
    /**
     * 获取最新电影列表（分页）
     * @param pageable 分页参数
     * @return 最新电影列表
     */
    Page<H5MovieDTO> getNewMovies(Pageable pageable);
    
    /**
     * 获取最新电影列表（分页，带排序）
     * @param pageable 分页参数
     * @param sort 排序方式
     * @return 最新电影列表
     */
    Page<H5MovieDTO> getNewMovies(Pageable pageable, String sort);
    
    /**
     * 获取高评分电影列表
     * @param limit 限制数量
     * @param minRating 最低评分
     * @return 高评分电影列表
     */
    List<H5MovieDTO> getHighRatedMovies(int limit, Double minRating);
    
    /**
     * 获取超级推荐电影列表
     * @param limit 限制数量
     * @return 超级推荐电影列表
     */
    List<H5MovieDTO> getSuperRecommendedMovies(int limit);
    
    /**
     * 根据分类获取电影列表
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 根据地区获取电影列表
     * @param regionId 地区ID
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByRegion(Long regionId, Pageable pageable);
    
    /**
     * 根据年份获取电影列表
     * @param year 年份
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByYear(Integer year, Pageable pageable);
    
    /**
     * 根据收费类型获取电影列表
     * @param chargeType 收费类型
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByChargeType(String chargeType, Pageable pageable);
    
    /**
     * 根据VIP状态获取电影列表
     * @param isVip 是否为VIP
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByVipStatus(Boolean isVip, Pageable pageable);
    
    /**
     * 根据免费状态获取电影列表
     * @param isFree 是否为免费
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByFreeStatus(Boolean isFree, Pageable pageable);
    
    /**
     * 根据免费状态获取电影列表（带排序）
     * @param isFree 是否为免费
     * @param pageable 分页参数
     * @param sort 排序方式
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByFreeStatus(Boolean isFree, Pageable pageable, String sort);
    
    /**
     * 根据画质获取电影列表
     * @param quality 画质
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByQuality(String quality, Pageable pageable);
    
    /**
     * 搜索电影
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<H5MovieDTO> searchMovies(String keyword, Pageable pageable);
    
    /**
     * 根据标签搜索电影
     * @param tag 标签
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<H5MovieDTO> searchMoviesByTag(String tag, Pageable pageable);
    
    /**
     * 复合条件查询电影
     * @param categoryId 分类ID
     * @param regionId 地区ID
     * @param releaseYear 发行年份
     * @param chargeType 收费类型
     * @param isVip 是否为VIP
     * @param quality 画质
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getMoviesByConditions(Long categoryId, Long regionId, Integer releaseYear, 
                                          String chargeType, Boolean isVip, String quality, Pageable pageable);
    
    /**
     * 获取所有电影（分页）
     * @param pageable 分页参数
     * @return 电影列表
     */
    Page<H5MovieDTO> getAllMovies(Pageable pageable);
    
    /**
     * 获取电影排行榜
     * @param pageable 分页参数
     * @param sortBy 排序方式：views, rating, likes, favorites, newest
     * @param timeRange 时间范围：all, week, month, year
     * @return 排行榜电影列表
     */
    Page<H5MovieDTO> getRankingMovies(Pageable pageable, String sortBy, String timeRange);
    
    /**
     * 记录观看次数
     * @param movieId 电影ID
     */
    void recordView(Long movieId);
    
    /**
     * 点赞电影
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeMovie(Long movieId, Long userId);
    
    /**
     * 取消点赞电影
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikeMovie(Long movieId, Long userId);
    
    /**
     * 收藏电影
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean favoriteMovie(Long movieId, Long userId);
    
    /**
     * 取消收藏电影
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unfavoriteMovie(Long movieId, Long userId);
    
    /**
     * 检查用户是否可以观看电影
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 观看权限信息
     */
    MovieWatchPermission checkWatchPermission(Long movieId, Long userId);
    
    /**
     * 获取电影筛选选项
     * @return 筛选选项
     */
    MovieFilterOptions getFilterOptions();
    
    // ==================== 新增电影详情和播放功能 ====================
    
    /**
     * 获取电影详情（用于详情页）
     * @param movieId 电影ID
     * @return 电影详情
     */
    H5MovieDetailDTO getMovieDetail(Long movieId);
    
    /**
     * 获取电影详情（用于详情页，带用户ID）
     * @param movieId 电影ID
     * @param userId 用户ID
     * @return 电影详情
     */
    H5MovieDetailDTO getMovieDetail(Long movieId, Long userId);
    
    /**
     * 检查播放权限
     * @param request 播放权限请求
     * @return 播放权限响应
     */
    H5MoviePlayResponse checkPlayPermission(H5MoviePlayRequest request);
    
    /**
     * 获取相关推荐电影
     * @param movieId 电影ID
     * @param limit 限制数量
     * @return 相关推荐电影列表
     */
    List<H5MovieDetailDTO> getRelatedMovies(Long movieId, Integer limit);
    
    /**
     * 增加观看次数
     * @param movieId 电影ID
     */
    void incrementViews(Long movieId);
    
    // ==================== 试看功能 ====================
    
    /**
     * 获取试看视频URL
     * @param movieId 电影ID
     * @param userId 用户ID（可选）
     * @return 试看信息
     */
    TrialVideoInfo getTrialVideoUrl(Long movieId, Long userId);
    
    /**
     * 检查试看权限
     * @param movieId 电影ID
     * @param userId 用户ID（可选）
     * @return 是否有试看权限
     */
    boolean hasTrialPermission(Long movieId, Long userId);
    
    /**
     * 电影观看权限信息
     */
    class MovieWatchPermission {
        private boolean canWatch;
        private String message;
        private Integer trialDuration;
        private String chargeType;
        private String chargeTypeDesc;
        
        // 构造函数、getter、setter
        public MovieWatchPermission(boolean canWatch, String message) {
            this.canWatch = canWatch;
            this.message = message;
        }
        
        public MovieWatchPermission(boolean canWatch, String message, Integer trialDuration, String chargeType, String chargeTypeDesc) {
            this.canWatch = canWatch;
            this.message = message;
            this.trialDuration = trialDuration;
            this.chargeType = chargeType;
            this.chargeTypeDesc = chargeTypeDesc;
        }
        
        public boolean isCanWatch() { return canWatch; }
        public void setCanWatch(boolean canWatch) { this.canWatch = canWatch; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Integer getTrialDuration() { return trialDuration; }
        public void setTrialDuration(Integer trialDuration) { this.trialDuration = trialDuration; }
        public String getChargeType() { return chargeType; }
        public void setChargeType(String chargeType) { this.chargeType = chargeType; }
        public String getChargeTypeDesc() { return chargeTypeDesc; }
        public void setChargeTypeDesc(String chargeTypeDesc) { this.chargeTypeDesc = chargeTypeDesc; }
    }
    
    /**
     * 电影筛选选项
     */
    class MovieFilterOptions {
        private List<YearOption> years;
        private List<QualityOption> qualities;
        private List<ChargeTypeOption> chargeTypes;
        
        // 构造函数、getter、setter
        public MovieFilterOptions() {}
        
        public MovieFilterOptions(List<YearOption> years, List<QualityOption> qualities, List<ChargeTypeOption> chargeTypes) {
            this.years = years;
            this.qualities = qualities;
            this.chargeTypes = chargeTypes;
        }
        
        public List<YearOption> getYears() { return years; }
        public void setYears(List<YearOption> years) { this.years = years; }
        public List<QualityOption> getQualities() { return qualities; }
        public void setQualities(List<QualityOption> qualities) { this.qualities = qualities; }
        public List<ChargeTypeOption> getChargeTypes() { return chargeTypes; }
        public void setChargeTypes(List<ChargeTypeOption> chargeTypes) { this.chargeTypes = chargeTypes; }
    }
    
    class YearOption {
        private Integer year;
        private Long count;
        
        public YearOption(Integer year, Long count) {
            this.year = year;
            this.count = count;
        }
        
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
    class QualityOption {
        private String quality;
        private Long count;
        
        public QualityOption(String quality, Long count) {
            this.quality = quality;
            this.count = count;
        }
        
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
    class ChargeTypeOption {
        private String chargeType;
        private String chargeTypeDesc;
        private Long count;
        
        public ChargeTypeOption(String chargeType, String chargeTypeDesc, Long count) {
            this.chargeType = chargeType;
            this.chargeTypeDesc = chargeTypeDesc;
            this.count = count;
        }
        
        public String getChargeType() { return chargeType; }
        public void setChargeType(String chargeType) { this.chargeType = chargeType; }
        public String getChargeTypeDesc() { return chargeTypeDesc; }
        public void setChargeTypeDesc(String chargeTypeDesc) { this.chargeTypeDesc = chargeTypeDesc; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
    /**
     * 试看视频信息
     */
    class TrialVideoInfo {
        private boolean canTrial;
        private String trialUrl;
        private Integer trialDuration;
        private String message;
        
        public TrialVideoInfo(boolean canTrial, String trialUrl, Integer trialDuration, String message) {
            this.canTrial = canTrial;
            this.trialUrl = trialUrl;
            this.trialDuration = trialDuration;
            this.message = message;
        }
        
        public boolean getCanTrial() { return canTrial; }
        public void setCanTrial(boolean canTrial) { this.canTrial = canTrial; }
        public String getTrialUrl() { return trialUrl; }
        public void setTrialUrl(String trialUrl) { this.trialUrl = trialUrl; }
        public Integer getTrialDuration() { return trialDuration; }
        public void setTrialDuration(Integer trialDuration) { this.trialDuration = trialDuration; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 