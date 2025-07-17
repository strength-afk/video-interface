package com.example.video_interface.repository;

import com.example.video_interface.model.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户收藏Repository
 */
@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    /**
     * 根据用户ID和内容ID查找收藏记录
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 收藏记录
     */
    Optional<UserFavorite> findByUserIdAndContentIdAndContentTypeAndStatus(
            Long userId, Long contentId, UserFavorite.ContentType contentType, UserFavorite.FavoriteStatus status);
    
    /**
     * 检查用户是否已收藏指定内容
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 是否已收藏
     */
    boolean existsByUserIdAndContentIdAndContentTypeAndStatus(
            Long userId, Long contentId, UserFavorite.ContentType contentType, UserFavorite.FavoriteStatus status);
    
    /**
     * 根据用户ID和内容类型分页查询收藏列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param status 状态
     * @param pageable 分页参数
     * @return 收藏列表
     */
    Page<UserFavorite> findByUserIdAndContentTypeAndStatusOrderBySortOrderDescCreatedAtDesc(
            Long userId, UserFavorite.ContentType contentType, UserFavorite.FavoriteStatus status, Pageable pageable);
    
    /**
     * 根据用户ID查询所有收藏的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param status 状态
     * @return 内容ID列表
     */
    @Query("SELECT uf.contentId FROM UserFavorite uf WHERE uf.userId = :userId AND uf.contentType = :contentType AND uf.status = :status")
    List<Long> findContentIdsByUserIdAndContentTypeAndStatus(
            @Param("userId") Long userId, 
            @Param("contentType") UserFavorite.ContentType contentType, 
            @Param("status") UserFavorite.FavoriteStatus status);
    
    /**
     * 根据内容ID和内容类型查询收藏数量
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param status 状态
     * @return 收藏数量
     */
    long countByContentIdAndContentTypeAndStatus(
            Long contentId, UserFavorite.ContentType contentType, UserFavorite.FavoriteStatus status);
    
    /**
     * 根据用户ID查询收藏总数
     * @param userId 用户ID
     * @param status 状态
     * @return 收藏总数
     */
    long countByUserIdAndStatus(Long userId, UserFavorite.FavoriteStatus status);
    
    /**
     * 根据用户ID和内容类型查询收藏总数
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param status 状态
     * @return 收藏总数
     */
    long countByUserIdAndContentTypeAndStatus(
            Long userId, UserFavorite.ContentType contentType, UserFavorite.FavoriteStatus status);
    
    /**
     * 软删除收藏记录（将状态设置为INACTIVE）
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE UserFavorite uf SET uf.status = 'INACTIVE', uf.updatedAt = CURRENT_TIMESTAMP WHERE uf.userId = :userId AND uf.contentId = :contentId AND uf.contentType = :contentType AND uf.status = 'ACTIVE'")
    int softDeleteByUserIdAndContentIdAndContentType(
            @Param("userId") Long userId, 
            @Param("contentId") Long contentId, 
            @Param("contentType") UserFavorite.ContentType contentType);
    
    /**
     * 根据内容ID和内容类型删除所有相关收藏记录（软删除）
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 影响的行数
     */
    @Modifying
    @Query("UPDATE UserFavorite uf SET uf.status = 'INACTIVE', uf.updatedAt = CURRENT_TIMESTAMP WHERE uf.contentId = :contentId AND uf.contentType = :contentType AND uf.status = 'ACTIVE'")
    int softDeleteByContentIdAndContentType(
            @Param("contentId") Long contentId, 
            @Param("contentType") UserFavorite.ContentType contentType);
} 