package com.example.video_interface.dto.h5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * H5端电影数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MovieDTO {
    
    /**
     * 电影ID
     */
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
     * 电影时长
     */
    private String duration;
    
    /**
     * 评分
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
     * 分类信息
     */
    private H5MovieCategoryDTO category;
    
    /**
     * 地区信息
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
     * 是否为VIP专享
     */
    private Boolean isVip;
    
    /**
     * 是否为免费电影
     */
    private Boolean isFree;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 试看时长（秒）
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
     * 文件大小（格式化显示）
     */
    private String fileSizeFormatted;
    
    /**
     * 文件格式
     */
    private String fileFormat;
    
    /**
     * 是否已收藏（用户相关）
     */
    private Boolean isFavorited;
    
    /**
     * 是否已点赞（用户相关）
     */
    private Boolean isLiked;
    
    /**
     * 是否可以观看（权限相关）
     */
    private Boolean canWatch;
    
    /**
     * 观看权限提示
     */
    private String watchPermissionTip;
    
    /**
     * 是否为超级推荐电影
     */
    private Boolean isSuperRecommended;
} 