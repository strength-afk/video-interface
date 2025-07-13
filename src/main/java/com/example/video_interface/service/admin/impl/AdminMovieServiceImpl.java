package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminMovieDTO;
import com.example.video_interface.model.Movie;
import com.example.video_interface.model.MovieCategory;
import com.example.video_interface.model.Region;
import com.example.video_interface.repository.MovieCategoryRepository;
import com.example.video_interface.repository.MovieRepository;
import com.example.video_interface.repository.RegionRepository;
import com.example.video_interface.service.admin.IAdminMovieService;
import com.example.video_interface.service.common.IMinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理后台电影服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMovieServiceImpl implements IAdminMovieService {
    
    private final MovieRepository movieRepository;
    private final MovieCategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final IMinioService minioService;
    
    @Override
    public Page<AdminMovieDTO> getMovieList(Pageable pageable, String keyword, Long categoryId, 
                                          Long regionId, String status, String chargeType) {
        log.debug("查询电影列表 - 关键词: {}, 分类: {}, 地区: {}, 状态: {}, 收费类型: {}", 
                 keyword, categoryId, regionId, status, chargeType);
        
        Movie.MovieStatus movieStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                movieStatus = Movie.MovieStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                log.warn("无效的电影状态: {}", status);
            }
        }
        
        Movie.ChargeType movieChargeType = null;
        if (chargeType != null && !chargeType.isEmpty()) {
            try {
                movieChargeType = Movie.ChargeType.valueOf(chargeType);
            } catch (IllegalArgumentException e) {
                log.warn("无效的收费类型: {}", chargeType);
            }
        }
        
        Page<Movie> movies = movieRepository.findByConditions(keyword, categoryId, regionId, 
                                                             movieStatus, movieChargeType, pageable);
        
        return movies.map(movie -> {
            AdminMovieDTO dto = AdminMovieDTO.fromEntity(movie);
            // 生成封面URL
            if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
                try {
                    dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
                } catch (Exception e) {
                    log.warn("生成封面URL失败: {}", e.getMessage());
                    dto.setCoverUrl(null);
                }
            }
            // 生成Banner URL
            if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
                try {
                    dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
                } catch (Exception e) {
                    log.warn("生成Banner URL失败: {}", e.getMessage());
                    dto.setBannerUrl(null);
                }
            }
            return dto;
        });
    }
    
    @Override
    public AdminMovieDTO getMovieById(Long id) {
        log.debug("查询电影详情 - ID: {}", id);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        AdminMovieDTO dto = AdminMovieDTO.fromEntity(movie);
        // 生成封面URL
        if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
            try {
                dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
            } catch (Exception e) {
                log.warn("生成封面URL失败: {}", e.getMessage());
                dto.setCoverUrl(null);
            }
        }
        // 生成Banner URL
        if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
            try {
                dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
                dto.setBannerUrl(null);
            }
        }
        return dto;
    }
    
    @Override
    @Transactional
    public AdminMovieDTO createMovie(AdminMovieDTO movieDTO) {
        log.debug("🔍 开始创建电影 - 标题: {}", movieDTO.getTitle());
        log.debug("🔍 电影DTO详情: {}", movieDTO);
        
        // 检查标题是否已存在
        if (movieRepository.existsByTitle(movieDTO.getTitle())) {
            throw new IllegalArgumentException("电影标题已存在");
        }
        
        Movie movie = movieDTO.toEntity();
        log.debug("🔍 转换为实体后的电影: {}", movie);
        
        // 设置关联的分类和地区
        if (movieDTO.getCategoryId() != null) {
            log.debug("🔍 查找分类: {}", movieDTO.getCategoryId());
            MovieCategory category = categoryRepository.findById(movieDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("电影分类不存在"));
            movie.setCategory(category);
            log.debug("🔍 设置分类成功: {}", category.getName());
        }
        
        if (movieDTO.getRegionId() != null) {
            log.debug("🔍 查找地区: {}", movieDTO.getRegionId());
            Region region = regionRepository.findById(movieDTO.getRegionId())
                    .orElseThrow(() -> new IllegalArgumentException("电影地区不存在"));
            movie.setRegion(region);
            log.debug("🔍 设置地区成功: {}", region.getName());
        }
        
        // 设置默认值
        log.debug("🔍 创建电影统计数据 - 前端传递的views: {}, rating: {}", movie.getViews(), movie.getRating());
        if (movie.getViews() == null) {
            movie.setViews(0L);
            log.debug("🔍 设置默认views: 0");
        } else {
            log.debug("🔍 使用前端传递的views: {}", movie.getViews());
        }
        if (movie.getLikes() == null) movie.setLikes(0L);
        if (movie.getFavorites() == null) movie.setFavorites(0L);
        if (movie.getRating() == null) {
            movie.setRating(java.math.BigDecimal.ZERO);
            log.debug("🔍 设置默认rating: 0");
        } else {
            log.debug("🔍 使用前端传递的rating: {}", movie.getRating());
        }
        if (movie.getIsVip() == null) movie.setIsVip(false);
        if (movie.getIsFree() == null) movie.setIsFree(true);
        if (movie.getPrice() == null) movie.setPrice(java.math.BigDecimal.ZERO);
        if (movie.getTrialDuration() == null) movie.setTrialDuration(0);
        if (movie.getChargeType() == null) movie.setChargeType(Movie.ChargeType.FREE);
        if (movie.getStatus() == null) movie.setStatus(Movie.MovieStatus.ACTIVE);
        if (movie.getSortOrder() == null) movie.setSortOrder(0);
        
        // 处理tags字段，确保是有效的JSON格式
        if (movie.getTags() == null || movie.getTags().trim().isEmpty() || movie.getTags().equals("[]")) {
            movie.setTags(null); // 设置为null而不是空数组字符串
        }
        
        log.debug("🔍 保存前的电影实体: {}", movie);
        Movie savedMovie = movieRepository.save(movie);
        log.info("电影创建成功 - ID: {}, 标题: {}", savedMovie.getId(), savedMovie.getTitle());
        
        AdminMovieDTO dto = AdminMovieDTO.fromEntity(savedMovie);
        // 生成封面URL
        if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
            try {
                dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
            } catch (Exception e) {
                log.warn("生成封面URL失败: {}", e.getMessage());
                dto.setCoverUrl(null);
            }
        }
        // 生成Banner URL
        if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
            try {
                dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
                dto.setBannerUrl(null);
            }
        }
        return dto;
    }
    
    @Override
    @Transactional
    public AdminMovieDTO updateMovie(Long id, AdminMovieDTO movieDTO) {
        log.debug("更新电影 - ID: {}, 标题: {}", id, movieDTO.getTitle());
        
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        // 检查标题是否已被其他电影使用
        if (!existingMovie.getTitle().equals(movieDTO.getTitle()) && 
            movieRepository.existsByTitleAndIdNot(movieDTO.getTitle(), id)) {
            throw new IllegalArgumentException("电影标题已存在");
        }
        
        Movie movie = movieDTO.toEntity();
        movie.setId(id);
        movie.setCreatedAt(existingMovie.getCreatedAt());
        
        // 设置关联的分类和地区
        if (movieDTO.getCategoryId() != null) {
            MovieCategory category = categoryRepository.findById(movieDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("电影分类不存在"));
            movie.setCategory(category);
        } else {
            movie.setCategory(null);
        }
        
        if (movieDTO.getRegionId() != null) {
            Region region = regionRepository.findById(movieDTO.getRegionId())
                    .orElseThrow(() -> new IllegalArgumentException("电影地区不存在"));
            movie.setRegion(region);
        } else {
            movie.setRegion(null);
        }
        
        // 保持原有的统计数据（如果前端没有传递新值）
        log.debug("🔍 更新电影统计数据 - 前端传递的views: {}, rating: {}", movie.getViews(), movie.getRating());
        log.debug("🔍 原有统计数据 - views: {}, rating: {}", existingMovie.getViews(), existingMovie.getRating());
        
        if (movie.getViews() == null) {
            movie.setViews(existingMovie.getViews());
            log.debug("🔍 使用原有views: {}", existingMovie.getViews());
        } else {
            log.debug("🔍 使用前端传递的views: {}", movie.getViews());
        }
        if (movie.getLikes() == null) {
            movie.setLikes(existingMovie.getLikes());
        }
        if (movie.getFavorites() == null) {
            movie.setFavorites(existingMovie.getFavorites());
        }
        if (movie.getRating() == null) {
            movie.setRating(existingMovie.getRating());
            log.debug("🔍 使用原有rating: {}", existingMovie.getRating());
        } else {
            log.debug("🔍 使用前端传递的rating: {}", movie.getRating());
        }
        
        // 处理tags字段，确保是有效的JSON格式
        if (movie.getTags() == null || movie.getTags().trim().isEmpty()) {
            movie.setTags("[]"); // 设置为空数组的JSON格式
        }
        
        Movie updatedMovie = movieRepository.save(movie);
        log.info("电影更新成功 - ID: {}, 标题: {}", updatedMovie.getId(), updatedMovie.getTitle());
        
        AdminMovieDTO dto = AdminMovieDTO.fromEntity(updatedMovie);
        // 生成封面URL
        if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
            try {
                dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
            } catch (Exception e) {
                log.warn("生成封面URL失败: {}", e.getMessage());
                dto.setCoverUrl(null);
            }
        }
        // 生成Banner URL
        if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
            try {
                dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
                dto.setBannerUrl(null);
            }
        }
        return dto;
    }
    
    @Override
    @Transactional
    public void deleteMovie(Long id) {
        log.debug("删除电影 - ID: {}", id);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        movieRepository.delete(movie);
        log.info("电影删除成功 - ID: {}, 标题: {}", id, movie.getTitle());
    }
    
    @Override
    @Transactional
    public AdminMovieDTO updateMovieStatus(Long id, String status) {
        log.debug("更新电影状态 - ID: {}, 状态: {}", id, status);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        try {
            Movie.MovieStatus movieStatus = Movie.MovieStatus.valueOf(status);
            movie.setStatus(movieStatus);
            
            Movie updatedMovie = movieRepository.save(movie);
            log.info("电影状态更新成功 - ID: {}, 标题: {}, 状态: {}", 
                    updatedMovie.getId(), updatedMovie.getTitle(), updatedMovie.getStatus());
            
            return AdminMovieDTO.fromEntity(updatedMovie);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的电影状态: " + status);
        }
    }
    
    @Override
    @Transactional
    public AdminMovieDTO setRecommendStatus(Long id, Boolean isRecommended) {
        log.debug("设置推荐状态 - ID: {}, 推荐: {}", id, isRecommended);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        movie.setIsRecommended(isRecommended);
        Movie savedMovie = movieRepository.save(movie);
        
        AdminMovieDTO dto = AdminMovieDTO.fromEntity(savedMovie);
        // 生成封面URL
        if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
            try {
                dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
            } catch (Exception e) {
                log.warn("生成封面URL失败: {}", e.getMessage());
                dto.setCoverUrl(null);
            }
        }
        // 生成Banner URL
        if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
            try {
                dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
                dto.setBannerUrl(null);
            }
        }
        return dto;
    }
    
    @Override
    @Transactional
    public AdminMovieDTO setSuperRecommendStatus(Long id, Boolean isSuperRecommended) {
        log.debug("设置超级推荐状态 - ID: {}, 超级推荐: {}", id, isSuperRecommended);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        // 如果要设置为超级推荐，检查banner图片是否存在
        if (isSuperRecommended) {
            // 检查banner字段是否为空
            if (movie.getBanner() == null || movie.getBanner().trim().isEmpty()) {
                throw new IllegalArgumentException("设置超级推荐失败：该电影没有上传banner图片，请先上传banner图片后再设置超级推荐");
            }
            
            // 检查是否超过5部限制（排除当前电影）
            long currentCount = movieRepository.countSuperRecommendedMoviesByStatus(Movie.MovieStatus.ACTIVE);
            if (!movie.getIsSuperRecommended()) {
                // 如果当前电影不是超级推荐，需要检查是否会超过限制
                if (currentCount >= 5) {
                    throw new IllegalArgumentException("超级推荐电影数量已达上限（最多5部）");
                }
            }
        }
        
        movie.setIsSuperRecommended(isSuperRecommended);
        Movie savedMovie = movieRepository.save(movie);
        
        AdminMovieDTO dto = AdminMovieDTO.fromEntity(savedMovie);
        // 生成封面URL
        if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
            try {
                dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
            } catch (Exception e) {
                log.warn("生成封面URL失败: {}", e.getMessage());
                dto.setCoverUrl(null);
            }
        }
        // 生成Banner URL
        if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
            try {
                dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
                dto.setBannerUrl(null);
            }
        }
        return dto;
    }
    
    @Override
    public List<AdminMovieDTO> getSuperRecommendedMovies() {
        log.debug("获取超级推荐电影列表");
        
        List<Movie> movies = movieRepository.findSuperRecommendedMoviesByStatus(
            Movie.MovieStatus.ACTIVE, 
            org.springframework.data.domain.PageRequest.of(0, 5)
        );
        
        return movies.stream().map(movie -> {
            AdminMovieDTO dto = AdminMovieDTO.fromEntity(movie);
            // 生成封面URL
            if (dto.getCover() != null && !dto.getCover().trim().isEmpty()) {
                try {
                    dto.setCoverUrl(minioService.getFileUrl(dto.getCover()));
                } catch (Exception e) {
                    log.warn("生成封面URL失败: {}", e.getMessage());
                    dto.setCoverUrl(null);
                }
            }
            // 生成Banner URL
            if (dto.getBanner() != null && !dto.getBanner().trim().isEmpty()) {
                try {
                    dto.setBannerUrl(minioService.getFileUrl(dto.getBanner()));
                } catch (Exception e) {
                    log.warn("生成Banner URL失败: {}", e.getMessage());
                    dto.setBannerUrl(null);
                }
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
} 