package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 电影实体类
 */
@Data
@Entity
@Table(name = "movies")
@Comment("电影表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("电影ID，主键，自增")
    private Long id;
    
    @Column(nullable = false, length = 200)
    @Comment("电影标题，不可为空")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @Comment("剧情简介")
    private String description;
    
    @Column(length = 500)
    @Comment("封面图片相对路径")
    private String cover;
    
    @Column(length = 500)
    @Comment("Banner图片相对路径")
    private String banner;
    
    @Column(length = 20)
    @Comment("电影时长，格式：HH:mm:ss")
    private String duration;
    
    @Column(precision = 3, scale = 1)
    @Comment("评分，0.0-10.0")
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Comment("观看次数")
    private Long views = 0L;
    
    @Column(nullable = false)
    @Comment("点赞次数")
    private Long likes = 0L;
    
    @Column(nullable = false)
    @Comment("收藏次数")
    private Long favorites = 0L;
    
    @Column(name = "release_date")
    @Comment("发布日期")
    private LocalDateTime releaseDate;
    
    @Column(name = "release_year")
    @Comment("发行年份")
    private Integer releaseYear;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Comment("电影分类")
    private MovieCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @Comment("电影地区")
    private Region region;
    
    @Column(length = 50)
    @Comment("画质：720P、1080P、4K等")
    private String quality;
    
    @Column(columnDefinition = "JSON")
    @Comment("标签，JSON格式存储")
    private String tags;
    
    @Column(name = "is_vip", nullable = false)
    @Comment("是否为VIP专享")
    private Boolean isVip = false;
    
    @Column(name = "is_free", nullable = false)
    @Comment("是否为免费电影")
    private Boolean isFree = true;
    
    @Column(name = "price", precision = 10, scale = 2)
    @Comment("价格，收费电影的价格")
    private BigDecimal price = BigDecimal.ZERO;
    
    @Column(name = "trial_duration", nullable = false)
    @Comment("试看时长（秒），0表示不允许试看")
    private Integer trialDuration = 0;
    
    @Column(name = "file_path", length = 500)
    @Comment("视频文件路径")
    private String filePath;
    
    @Column(name = "file_size")
    @Comment("文件大小（字节）")
    private Long fileSize;
    
    @Column(name = "file_format", length = 20)
    @Comment("文件格式：mp4、mkv、avi等")
    private String fileFormat;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", length = 20, nullable = false)
    @Comment("收费类型：FREE-免费，VIP-VIP专享")
    private ChargeType chargeType = ChargeType.FREE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Comment("状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除")
    private MovieStatus status = MovieStatus.ACTIVE;
    
    @Column(name = "sort_order", nullable = false)
    @Comment("排序权重，数字越大越靠前")
    private Integer sortOrder = 0;
    
    @Column(name = "is_super_recommended", nullable = false)
    @Comment("是否为超级推荐电影（轮播图显示）")
    private Boolean isSuperRecommended = false;
    
    @Column(name = "is_recommended", nullable = false)
    @Comment("是否为推荐电影")
    private Boolean isRecommended = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("更新时间")
    private LocalDateTime updatedAt;
    
    /**
     * 收费类型枚举
     */
    public enum ChargeType {
        FREE("免费"),
        VIP("VIP免费观看");
        
        private final String description;
        
        ChargeType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 电影状态枚举
     */
    public enum MovieStatus {
        ACTIVE("上架"),
        INACTIVE("下架"),
        DELETED("已删除");
        
        private final String description;
        
        MovieStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (views == null) views = 0L;
        if (likes == null) likes = 0L;
        if (favorites == null) favorites = 0L;
        if (rating == null) rating = BigDecimal.ZERO;
        if (isVip == null) isVip = false;
        if (isFree == null) isFree = true;
        if (price == null) price = BigDecimal.ZERO;
        if (trialDuration == null) trialDuration = 0;
        if (chargeType == null) chargeType = ChargeType.FREE;
        if (status == null) status = MovieStatus.ACTIVE;
        if (sortOrder == null) sortOrder = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 