package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5UserFavoriteDTO;
import com.example.video_interface.model.UserFavorite;
import com.example.video_interface.model.Movie;
import com.example.video_interface.repository.UserFavoriteRepository;
import com.example.video_interface.repository.MovieRepository;
import com.example.video_interface.service.h5.IH5UserFavoriteService;
import com.example.video_interface.service.common.IMinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * H5端用户收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class H5UserFavoriteServiceImpl implements IH5UserFavoriteService {
    
    private final UserFavoriteRepository userFavoriteRepository;
    private final MovieRepository movieRepository;
    private final IMinioService minioService;
    
    @Override
    @Transactional
    public boolean addFavorite(Long userId, Long contentId, String contentType, String contentTitle, String contentCover) {
        log.debug("添加收藏，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
        
        try {
            // 检查是否已收藏（ACTIVE状态）
            if (checkUserFavorited(userId, contentId, contentType)) {
                log.warn("用户已收藏该内容，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
                return false;
            }
            
            // 检查是否存在INACTIVE状态的记录，如果存在则重新激活
            Optional<UserFavorite> existingFavorite = userFavoriteRepository.findByUserIdAndContentIdAndContentTypeAndStatus(
                    userId, contentId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.INACTIVE);
            
            if (existingFavorite.isPresent()) {
                // 重新激活已存在的收藏记录
                UserFavorite favorite = existingFavorite.get();
                favorite.setStatus(UserFavorite.FavoriteStatus.ACTIVE);
                favorite.setContentTitle(contentTitle);
                favorite.setContentCover(contentCover);
                userFavoriteRepository.save(favorite);
                log.info("重新激活收藏记录，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
            } else {
                // 创建新的收藏记录
                UserFavorite favorite = UserFavorite.builder()
                        .userId(userId)
                        .contentId(contentId)
                        .contentType(UserFavorite.ContentType.valueOf(contentType.toUpperCase()))
                        .contentTitle(contentTitle)
                        .contentCover(contentCover)
                        .sortOrder(0)
                        .status(UserFavorite.FavoriteStatus.ACTIVE)
                        .build();
                
                userFavoriteRepository.save(favorite);
                log.info("创建新收藏记录，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
            }
            
            // 如果是电影类型，更新电影的收藏数量
            if ("MOVIE".equals(contentType.toUpperCase())) {
                Movie movie = movieRepository.findById(contentId).orElse(null);
                if (movie != null) {
                    movie.setFavorites(movie.getFavorites() + 1);
                    movieRepository.save(movie);
                    log.debug("更新电影收藏数量，电影ID: {}, 新收藏数: {}", contentId, movie.getFavorites());
                }
            }
            
            log.info("添加收藏成功，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
            return true;
            
        } catch (Exception e) {
            log.error("添加收藏失败，用户ID: {}, 内容ID: {}, 内容类型: {}, 错误: {}", userId, contentId, contentType, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean removeFavorite(Long userId, Long contentId, String contentType) {
        log.debug("取消收藏，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
        
        try {
            // 先检查是否存在ACTIVE状态的收藏记录
            boolean wasActive = userFavoriteRepository.existsByUserIdAndContentIdAndContentTypeAndStatus(
                    userId, contentId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE);
            
            int affectedRows = userFavoriteRepository.softDeleteByUserIdAndContentIdAndContentType(
                    userId, contentId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()));
            
            if (affectedRows > 0) {
                // 如果之前是ACTIVE状态且是电影类型，减少电影的收藏数量
                if (wasActive && "MOVIE".equals(contentType.toUpperCase())) {
                    Movie movie = movieRepository.findById(contentId).orElse(null);
                    if (movie != null && movie.getFavorites() > 0) {
                        movie.setFavorites(movie.getFavorites() - 1);
                        movieRepository.save(movie);
                        log.debug("减少电影收藏数量，电影ID: {}, 新收藏数: {}", contentId, movie.getFavorites());
                    }
                }
                
                log.info("取消收藏成功，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
                return true;
            } else {
                log.warn("收藏记录不存在或已被删除，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
                return false;
            }
            
        } catch (Exception e) {
            log.error("取消收藏失败，用户ID: {}, 内容ID: {}, 内容类型: {}, 错误: {}", userId, contentId, contentType, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean checkUserFavorited(Long userId, Long contentId, String contentType) {
        log.debug("检查用户收藏状态，用户ID: {}, 内容ID: {}, 内容类型: {}", userId, contentId, contentType);
        
        try {
            return userFavoriteRepository.existsByUserIdAndContentIdAndContentTypeAndStatus(
                    userId, contentId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE);
        } catch (Exception e) {
            log.error("检查用户收藏状态失败，用户ID: {}, 内容ID: {}, 内容类型: {}, 错误: {}", userId, contentId, contentType, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Page<H5UserFavoriteDTO> getUserFavorites(Long userId, String contentType, Pageable pageable) {
        log.debug("获取用户收藏列表，用户ID: {}, 内容类型: {}, 页码: {}, 每页数量: {}", userId, contentType, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<UserFavorite> favorites = userFavoriteRepository.findByUserIdAndContentTypeAndStatusOrderBySortOrderDescCreatedAtDesc(
                    userId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE, pageable);
            
            return favorites.map(this::convertToDTO);
            
        } catch (Exception e) {
            log.error("获取用户收藏列表失败，用户ID: {}, 内容类型: {}, 错误: {}", userId, contentType, e.getMessage(), e);
            return Page.empty(pageable);
        }
    }
    
    @Override
    public List<Long> getUserFavoriteContentIds(Long userId, String contentType) {
        log.debug("获取用户收藏的内容ID列表，用户ID: {}, 内容类型: {}", userId, contentType);
        
        try {
            return userFavoriteRepository.findContentIdsByUserIdAndContentTypeAndStatus(
                    userId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE);
        } catch (Exception e) {
            log.error("获取用户收藏的内容ID列表失败，用户ID: {}, 内容类型: {}, 错误: {}", userId, contentType, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public long getUserFavoriteCount(Long userId) {
        log.debug("获取用户收藏总数，用户ID: {}", userId);
        
        try {
            return userFavoriteRepository.countByUserIdAndStatus(userId, UserFavorite.FavoriteStatus.ACTIVE);
        } catch (Exception e) {
            log.error("获取用户收藏总数失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return 0L;
        }
    }
    
    @Override
    public long getUserFavoriteCountByType(Long userId, String contentType) {
        log.debug("获取用户指定类型内容的收藏总数，用户ID: {}, 内容类型: {}", userId, contentType);
        
        try {
            return userFavoriteRepository.countByUserIdAndContentTypeAndStatus(
                    userId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE);
        } catch (Exception e) {
            log.error("获取用户指定类型内容的收藏总数失败，用户ID: {}, 内容类型: {}, 错误: {}", userId, contentType, e.getMessage(), e);
            return 0L;
        }
    }
    
    @Override
    public long getContentFavoriteCount(Long contentId, String contentType) {
        log.debug("获取内容的收藏数量，内容ID: {}, 内容类型: {}", contentId, contentType);
        
        try {
            return userFavoriteRepository.countByContentIdAndContentTypeAndStatus(
                    contentId, UserFavorite.ContentType.valueOf(contentType.toUpperCase()), UserFavorite.FavoriteStatus.ACTIVE);
        } catch (Exception e) {
            log.error("获取内容的收藏数量失败，内容ID: {}, 内容类型: {}, 错误: {}", contentId, contentType, e.getMessage(), e);
            return 0L;
        }
    }
    
    @Override
    public List<Long> batchCheckUserFavorited(Long userId, List<Long> contentIds, String contentType) {
        log.debug("批量检查用户收藏状态，用户ID: {}, 内容ID数量: {}, 内容类型: {}", userId, contentIds.size(), contentType);
        
        try {
            List<Long> userFavoriteIds = getUserFavoriteContentIds(userId, contentType);
            return contentIds.stream()
                    .filter(userFavoriteIds::contains)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("批量检查用户收藏状态失败，用户ID: {}, 内容类型: {}, 错误: {}", userId, contentType, e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * 转换为DTO
     * @param favorite 收藏实体
     * @return 收藏DTO
     */
    private H5UserFavoriteDTO convertToDTO(UserFavorite favorite) {
        String contentCoverUrl = null;
        String rating = null;
        Integer releaseYear = null;
        
        // 根据内容类型生成封面URL和额外信息
        if (favorite.getContentType() == UserFavorite.ContentType.MOVIE) {
            // 对于电影类型，从电影实体获取封面URL、评分和年份
            try {
                Movie movie = movieRepository.findById(favorite.getContentId()).orElse(null);
                if (movie != null) {
                    if (movie.getCover() != null) {
                        contentCoverUrl = minioService.getFileUrl(movie.getCover());
                    }
                    if (movie.getRating() != null) {
                        rating = movie.getRating().toString();
                    }
                    releaseYear = movie.getReleaseYear();
                }
            } catch (Exception e) {
                log.warn("生成电影信息失败，电影ID: {}, 错误: {}", favorite.getContentId(), e.getMessage());
            }
        } else {
            // 对于其他类型，使用收藏记录中的封面信息
            if (favorite.getContentCover() != null) {
                try {
                    contentCoverUrl = minioService.getFileUrl(favorite.getContentCover());
                } catch (Exception e) {
                    log.warn("生成封面URL失败，内容ID: {}, 错误: {}", favorite.getContentId(), e.getMessage());
                }
            }
        }
        
        return H5UserFavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .contentId(favorite.getContentId())
                .contentType(favorite.getContentType().name())
                .contentTypeDesc(favorite.getContentType().getDescription())
                .contentTitle(favorite.getContentTitle())
                .contentCover(favorite.getContentCover())
                .contentCoverUrl(contentCoverUrl)
                .rating(rating)
                .releaseYear(releaseYear)
                .sortOrder(favorite.getSortOrder())
                .status(favorite.getStatus().name())
                .statusDesc(favorite.getStatus().getDescription())
                .createdAt(favorite.getCreatedAt())
                .updatedAt(favorite.getUpdatedAt())
                .build();
    }
} 