package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 电影分类实体类
 */
@Data
@Entity
@Table(name = "movie_categories")
@Comment("电影分类表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("分类ID")
    private Long id;
    
    @Column(nullable = false, length = 50)
    @Comment("分类名称")
    private String name;
    
    @Column(length = 200)
    @Comment("分类描述")
    private String description;
    
    @Column(length = 200)
    @Comment("分类图标URL")
    private String icon;
    
    @Column(nullable = false)
    @Comment("排序权重")
    private Integer weight = 0;
    
    @Column(nullable = false)
    @Comment("是否启用")
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("更新时间")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (weight == null) weight = 0;
        if (enabled == null) enabled = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 