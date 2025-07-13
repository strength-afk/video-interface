package com.example.video_interface.dto.admin;

import com.example.video_interface.model.Movie;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台电影DTO
 */
@Data
public class AdminMovieDTO {
    
    private Long id;
    
    /**
     * 电影标题
     */
    private String title;
    
    /**
     * 剧情简介
     */
    private String description;
    
    /**
     * 封面图片相对路径
     */
    private String cover;
    
    /**
     * 封面图片访问URL（动态生成）
     */
    private String coverUrl;
    
    /**
     * Banner图片相对路径
     */
    private String banner;
    
    /**
     * Banner图片访问URL（动态生成）
     */
    private String bannerUrl;
    
    /**
     * 电影时长，格式：HH:mm:ss
     */
    private String duration;
    
    /**
     * 评分，0.0-10.0
     */
    private BigDecimal rating;
    
    /**
     * 观看次数
     */
    private Long views;
    
    /**
     * 点赞次数
     */
    private Long likes;
    
    /**
     * 收藏次数
     */
    private Long favorites;
    
    /**
     * 发布日期
     */
    private LocalDateTime releaseDate;
    
    /**
     * 发行年份
     */
    private Integer releaseYear;
    
    /**
     * 电影分类ID
     */
    private Long categoryId;
    
    /**
     * 电影分类名称
     */
    private String categoryName;
    
    /**
     * 电影地区ID
     */
    private Long regionId;
    
    /**
     * 电影地区名称
     */
    private String regionName;
    
    /**
     * 画质：720P、1080P、4K等
     */
    private String quality;
    
    /**
     * 标签，JSON格式存储
     */
    private String tags;
    
    /**
     * 是否为VIP免费观看
     */
    private Boolean isVip;
    
    /**
     * 是否为免费电影
     */
    private Boolean isFree;
    
    /**
     * 价格，收费电影的价格
     */
    private BigDecimal price;
    
    /**
     * 试看时长（秒），0表示不允许试看
     */
    private Integer trialDuration;
    
    /**
     * 视频文件路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件格式：mp4、mkv、avi等
     */
    private String fileFormat;
    
    /**
     * 收费类型：FREE-免费，VIP-VIP免费观看
     */
    private Movie.ChargeType chargeType;
    
    /**
     * 状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除
     */
    private Movie.MovieStatus status;
    
    /**
     * 排序权重，数字越大越靠前
     */
    private Integer sortOrder;
    
    /**
     * 是否为超级推荐电影
     */
    private Boolean isSuperRecommended;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 从Movie实体转换为DTO
     */
    public static AdminMovieDTO fromEntity(Movie movie) {
        AdminMovieDTO dto = new AdminMovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setCover(movie.getCover());
        // 封面URL将在服务层动态生成
        dto.setCoverUrl(null);
        dto.setBanner(movie.getBanner());
        // Banner URL将在服务层动态生成
        dto.setBannerUrl(null);
        dto.setDuration(movie.getDuration());
        dto.setRating(movie.getRating());
        dto.setViews(movie.getViews());
        dto.setLikes(movie.getLikes());
        dto.setFavorites(movie.getFavorites());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setReleaseYear(movie.getReleaseYear());
        
        if (movie.getCategory() != null) {
            dto.setCategoryId(movie.getCategory().getId());
            dto.setCategoryName(movie.getCategory().getName());
        }
        
        if (movie.getRegion() != null) {
            dto.setRegionId(movie.getRegion().getId());
            dto.setRegionName(movie.getRegion().getName());
        }
        
        dto.setQuality(movie.getQuality());
        dto.setTags(movie.getTags());
        dto.setIsVip(movie.getIsVip());
        dto.setIsFree(movie.getIsFree());
        dto.setPrice(movie.getPrice());
        dto.setTrialDuration(movie.getTrialDuration());
        dto.setFilePath(movie.getFilePath());
        dto.setFileSize(movie.getFileSize());
        dto.setFileFormat(movie.getFileFormat());
        dto.setChargeType(movie.getChargeType());
        dto.setStatus(movie.getStatus());
        dto.setSortOrder(movie.getSortOrder());
        dto.setIsSuperRecommended(movie.getIsSuperRecommended());
        dto.setCreatedAt(movie.getCreatedAt());
        dto.setUpdatedAt(movie.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 转换为Movie实体
     */
    public Movie toEntity() {
        Movie movie = new Movie();
        movie.setId(this.id);
        movie.setTitle(this.title);
        movie.setDescription(this.description);
        movie.setCover(this.cover);
        movie.setBanner(this.banner);
        movie.setDuration(this.duration);
        movie.setRating(this.rating);
        movie.setViews(this.views);
        movie.setLikes(this.likes);
        movie.setFavorites(this.favorites);
        movie.setReleaseDate(this.releaseDate);
        movie.setReleaseYear(this.releaseYear);
        movie.setQuality(this.quality);
        movie.setTags(this.tags);
        movie.setIsVip(this.isVip);
        movie.setIsFree(this.isFree);
        movie.setPrice(this.price);
        movie.setTrialDuration(this.trialDuration);
        movie.setFilePath(this.filePath);
        movie.setFileSize(this.fileSize);
        movie.setFileFormat(this.fileFormat);
        movie.setChargeType(this.chargeType);
        movie.setStatus(this.status);
        movie.setSortOrder(this.sortOrder);
        movie.setIsSuperRecommended(this.isSuperRecommended);
        
        return movie;
    }
} 