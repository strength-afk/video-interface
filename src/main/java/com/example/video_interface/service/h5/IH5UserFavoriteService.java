package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5UserFavoriteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * H5端用户收藏服务接口
 */
public interface IH5UserFavoriteService {
    
    /**
     * 添加收藏
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param contentTitle 内容标题
     * @param contentCover 内容封面
     * @return 是否成功
     */
    boolean addFavorite(Long userId, Long contentId, String contentType, String contentTitle, String contentCover);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, Long contentId, String contentType);
    
    /**
     * 检查用户是否已收藏指定内容
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 是否已收藏
     */
    boolean checkUserFavorited(Long userId, Long contentId, String contentType);
    
    /**
     * 获取用户收藏列表（分页）
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 收藏列表
     */
    Page<H5UserFavoriteDTO> getUserFavorites(Long userId, String contentType, Pageable pageable);
    
    /**
     * 获取用户收藏的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 内容ID列表
     */
    List<Long> getUserFavoriteContentIds(Long userId, String contentType);
    
    /**
     * 获取用户收藏总数
     * @param userId 用户ID
     * @return 收藏总数
     */
    long getUserFavoriteCount(Long userId);
    
    /**
     * 获取用户指定类型内容的收藏总数
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 收藏总数
     */
    long getUserFavoriteCountByType(Long userId, String contentType);
    
    /**
     * 获取内容的收藏数量
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 收藏数量
     */
    long getContentFavoriteCount(Long contentId, String contentType);
    
    /**
     * 批量检查用户收藏状态
     * @param userId 用户ID
     * @param contentIds 内容ID列表
     * @param contentType 内容类型
     * @return 已收藏的内容ID列表
     */
    List<Long> batchCheckUserFavorited(Long userId, List<Long> contentIds, String contentType);
} 