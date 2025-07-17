package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 */
@Data
@Entity
@Table(name = "user_favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "content_id", "content_type"}, name = "uk_user_content")
})
@Comment("用户收藏表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFavorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("收藏ID，主键，自增")
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;
    
    @Column(name = "content_id", nullable = false)
    @Comment("内容ID（电影ID、漫画ID、小说ID等）")
    private Long contentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", length = 20, nullable = false)
    @Comment("内容类型：MOVIE-电影，MANGA-漫画，NOVEL-小说")
    private ContentType contentType;
    
    @Column(name = "content_title", length = 200)
    @Comment("内容标题（冗余字段，便于查询）")
    private String contentTitle;
    
    @Column(name = "content_cover", length = 500)
    @Comment("内容封面（冗余字段，便于查询）")
    private String contentCover;
    
    @Column(name = "sort_order", nullable = false)
    @Comment("排序权重，数字越大越靠前")
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Comment("状态：ACTIVE-有效，INACTIVE-无效")
    private FavoriteStatus status = FavoriteStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("更新时间")
    private LocalDateTime updatedAt;
    
    /**
     * 内容类型枚举
     */
    public enum ContentType {
        MOVIE("电影"),
        MANGA("漫画"),
        NOVEL("小说");
        
        private final String description;
        
        ContentType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 收藏状态枚举
     */
    public enum FavoriteStatus {
        ACTIVE("有效"),
        INACTIVE("无效");
        
        private final String description;
        
        FavoriteStatus(String description) {
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
        if (sortOrder == null) sortOrder = 0;
        if (status == null) status = FavoriteStatus.ACTIVE;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 