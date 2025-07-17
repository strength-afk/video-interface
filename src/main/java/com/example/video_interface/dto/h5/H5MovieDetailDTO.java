package com.example.video_interface.dto.h5;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * H5端电影详情DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MovieDetailDTO {
    
    /**
     * 电影ID
     */
    private Long id;
    
    /**
     * 电影标题
     */
    private String title;
    
    /**
     * 电影描述
     */
    private String description;
    
    /**
     * 电影封面
     */
    private String cover;
    
    /**
     * 电影封面URL
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
     * 电影时长 (格式: HH:mm:ss)
     */
    private String duration;
    
    /**
     * 电影评分
     */
    private BigDecimal rating;
    
    /**
     * 观看次数
     */
    private Long views;
    
    /**
     * 点赞数
     */
    private Long likes;
    
    /**
     * 收藏数
     */
    private Long favorites;
    
    /**
     * 上映年份
     */
    private Integer releaseYear;
    
    /**
     * 电影分类
     */
    private H5MovieCategoryDTO category;
    
    /**
     * 电影地区
     */
    private H5RegionDTO region;
    
    /**
     * 画质
     */
    private String quality;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 是否VIP专享
     */
    private Boolean isVip;
    
    /**
     * 是否免费
     */
    private Boolean isFree;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 试看时长(秒)
     */
    private Integer trialDuration;
    
    /**
     * 收费类型
     */
    private String chargeType;
    
    /**
     * 收费类型描述
     */
    private String chargeTypeDesc;
    
    /**
     * 电影状态
     */
    private String status;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 是否推荐
     */
    private Boolean isRecommended;
    
    /**
     * 是否超级推荐
     */
    private Boolean isSuperRecommended;
    
    /**
     * 电影文件路径
     */
    private String filePath;
    
    /**
     * 电影文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件格式
     */
    private String fileFormat;
    
    /**
     * 创建时间
     */
    private String createdAt;
    
    /**
     * 更新时间
     */
    private String updatedAt;
    
    /**
     * 用户是否已购买该电影
     */
    private Boolean isPurchased;
    
    /**
     * 用户是否已点赞该电影
     */
    private Boolean isLiked;
    
    /**
     * 用户是否已收藏该电影
     */
    private Boolean isFavorited;
} 