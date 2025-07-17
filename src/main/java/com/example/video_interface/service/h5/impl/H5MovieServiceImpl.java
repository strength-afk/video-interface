package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5MovieCategoryDTO;
import com.example.video_interface.dto.h5.H5MovieDTO;
import com.example.video_interface.dto.h5.H5RegionDTO;
import com.example.video_interface.dto.h5.H5MovieDetailDTO;
import com.example.video_interface.dto.h5.H5MoviePlayRequest;
import com.example.video_interface.dto.h5.H5MoviePlayResponse;
import com.example.video_interface.dto.h5.H5MoviePurchaseRequest;
import com.example.video_interface.dto.h5.H5MoviePurchaseResponse;
import com.example.video_interface.model.Movie;
import com.example.video_interface.model.MovieCategory;
import com.example.video_interface.model.Region;
import com.example.video_interface.model.User;
import com.example.video_interface.model.UserMoviePurchase;
import com.example.video_interface.model.Order;
import com.example.video_interface.repository.MovieRepository;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.repository.UserMoviePurchaseRepository;
import com.example.video_interface.repository.OrderRepository;
import com.example.video_interface.service.h5.IH5MovieService;
import com.example.video_interface.service.h5.IH5UserFavoriteService;
import com.example.video_interface.service.common.IMinioService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * H5端电影服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class H5MovieServiceImpl implements IH5MovieService {
    
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final UserMoviePurchaseRepository userMoviePurchaseRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final IMinioService minioService;
    private final IH5UserFavoriteService userFavoriteService;
    
    @Override
    public H5MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findByIdAndStatus(id, Movie.MovieStatus.ACTIVE);
        if (movie == null) {
            return null;
        }
        return convertToDTO(movie);
    }
    
    @Override
    public List<H5MovieDTO> getHotMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movie> movies = movieRepository.findHotMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
        return movies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public Page<H5MovieDTO> getHotMovies(Pageable pageable) {
        // 先获取总数
        long total = movieRepository.countByStatus(Movie.MovieStatus.ACTIVE);
        List<Movie> movies = movieRepository.findHotMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
        List<H5MovieDTO> dtos = movies.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public Page<H5MovieDTO> getHotMovies(Pageable pageable, String sort) {
        if (sort == null || sort.isEmpty() || "default".equals(sort)) {
            return getHotMovies(pageable);
        }
        
        // 先获取总数
        long total = movieRepository.countByStatus(Movie.MovieStatus.ACTIVE);
        List<Movie> movies;
        
        switch (sort.toLowerCase()) {
            case "rating":
                movies = movieRepository.findHotMoviesByStatusOrderByRatingDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "year":
                movies = movieRepository.findHotMoviesByStatusOrderByReleaseYearDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "hot":
                movies = movieRepository.findHotMoviesByStatusOrderByViewsDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            default:
                movies = movieRepository.findHotMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
                break;
        }
        
        List<H5MovieDTO> dtos = movies.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public List<H5MovieDTO> getNewMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movie> movies = movieRepository.findNewMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
        return movies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public Page<H5MovieDTO> getNewMovies(Pageable pageable) {
        // 先获取总数
        long total = movieRepository.countByStatus(Movie.MovieStatus.ACTIVE);
        List<Movie> movies = movieRepository.findNewMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
        List<H5MovieDTO> dtos = movies.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public Page<H5MovieDTO> getNewMovies(Pageable pageable, String sort) {
        if (sort == null || sort.isEmpty() || "default".equals(sort)) {
            return getNewMovies(pageable);
        }
        
        // 先获取总数
        long total = movieRepository.countByStatus(Movie.MovieStatus.ACTIVE);
        List<Movie> movies;
        
        switch (sort.toLowerCase()) {
            case "rating":
                movies = movieRepository.findNewMoviesByStatusOrderByRatingDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "year":
                movies = movieRepository.findNewMoviesByStatusOrderByReleaseYearDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "hot":
                movies = movieRepository.findNewMoviesByStatusOrderByViewsDesc(Movie.MovieStatus.ACTIVE, pageable);
                break;
            default:
                movies = movieRepository.findNewMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
                break;
        }
        
        List<H5MovieDTO> dtos = movies.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, total);
    }
    
    @Override
    public List<H5MovieDTO> getHighRatedMovies(int limit, Double minRating) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movie> movies = movieRepository.findHighRatedMoviesByStatus(Movie.MovieStatus.ACTIVE, minRating, pageable);
        return movies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<H5MovieDTO> getSuperRecommendedMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Movie> movies = movieRepository.findSuperRecommendedMoviesByStatus(Movie.MovieStatus.ACTIVE, pageable);
        return movies.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByCategory(Long categoryId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByCategoryIdAndStatusOrderBySortOrderDescCreatedAtDesc(categoryId, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByRegion(Long regionId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByRegionIdAndStatusOrderBySortOrderDescCreatedAtDesc(regionId, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByYear(Integer year, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByReleaseYearAndStatusOrderBySortOrderDescCreatedAtDesc(year, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByChargeType(String chargeType, Pageable pageable) {
        Movie.ChargeType type = Movie.ChargeType.valueOf(chargeType.toUpperCase());
        Page<Movie> movies = movieRepository.findByChargeTypeAndStatusOrderBySortOrderDescCreatedAtDesc(type, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByVipStatus(Boolean isVip, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByIsVipAndStatusOrderBySortOrderDescCreatedAtDesc(isVip, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByFreeStatus(Boolean isFree, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByIsFreeAndStatusOrderBySortOrderDescCreatedAtDesc(isFree, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByFreeStatus(Boolean isFree, Pageable pageable, String sort) {
        if (sort == null || sort.isEmpty() || "default".equals(sort)) {
            return getMoviesByFreeStatus(isFree, pageable);
        }
        
        Page<Movie> movies;
        
        switch (sort.toLowerCase()) {
            case "rating":
                movies = movieRepository.findByIsFreeAndStatusOrderByRatingDesc(isFree, Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "year":
                movies = movieRepository.findByIsFreeAndStatusOrderByReleaseYearDesc(isFree, Movie.MovieStatus.ACTIVE, pageable);
                break;
            case "hot":
                movies = movieRepository.findByIsFreeAndStatusOrderByViewsDesc(isFree, Movie.MovieStatus.ACTIVE, pageable);
                break;
            default:
                movies = movieRepository.findByIsFreeAndStatusOrderBySortOrderDescCreatedAtDesc(isFree, Movie.MovieStatus.ACTIVE, pageable);
                break;
        }
        
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByQuality(String quality, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByQualityAndStatusOrderBySortOrderDescCreatedAtDesc(quality, Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> searchMovies(String keyword, Pageable pageable) {
        List<Movie> movies = movieRepository.searchByKeyword(Movie.MovieStatus.ACTIVE, keyword, pageable);
        return Page.empty(pageable);
    }
    
    @Override
    public Page<H5MovieDTO> searchMoviesByTag(String tag, Pageable pageable) {
        List<Movie> movies = movieRepository.searchByTag(Movie.MovieStatus.ACTIVE, tag, pageable);
        return Page.empty(pageable);
    }
    
    @Override
    public Page<H5MovieDTO> getMoviesByConditions(Long categoryId, Long regionId, Integer releaseYear, 
                                                  String chargeType, Boolean isVip, String quality, Pageable pageable) {
        Movie.ChargeType type = null;
        if (chargeType != null && !chargeType.isEmpty()) {
            try {
                type = Movie.ChargeType.valueOf(chargeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid charge type: {}", chargeType);
            }
        }
        
        Page<Movie> movies = movieRepository.findByConditions(Movie.MovieStatus.ACTIVE, categoryId, regionId, 
                                                             releaseYear, type, isVip, quality, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getAllMovies(Pageable pageable) {
        Page<Movie> movies = movieRepository.findByStatusOrderBySortOrderDescCreatedAtDesc(Movie.MovieStatus.ACTIVE, pageable);
        return movies.map(this::convertToDTO);
    }
    
    @Override
    public Page<H5MovieDTO> getRankingMovies(Pageable pageable, String sortBy, String timeRange) {
        log.info("获取电影排行榜，排序方式: {}, 时间范围: {}", sortBy, timeRange);
        
        // 根据排序方式和时间范围构建查询
        Page<Movie> movies;
        
        switch (sortBy.toLowerCase()) {
            case "views":
                movies = getRankingByViews(pageable, timeRange);
                break;
            case "rating":
                movies = getRankingByRating(pageable, timeRange);
                break;
            case "likes":
                movies = getRankingByLikes(pageable, timeRange);
                break;
            case "favorites":
                movies = getRankingByFavorites(pageable, timeRange);
                break;
            case "newest":
                movies = getRankingByNewest(pageable, timeRange);
                break;
            default:
                movies = getRankingByViews(pageable, timeRange);
        }
        
        return movies.map(this::convertToDTO);
    }
    
    /**
     * 按观看次数排序
     */
    private Page<Movie> getRankingByViews(Pageable pageable, String timeRange) {
        // 暂时忽略时间范围，直接按观看次数排序
        return movieRepository.findByStatusOrderByViewsDesc(Movie.MovieStatus.ACTIVE, pageable);
    }
    
    /**
     * 按评分排序
     */
    private Page<Movie> getRankingByRating(Pageable pageable, String timeRange) {
        // 暂时忽略时间范围，直接按评分排序
        return movieRepository.findByStatusOrderByRatingDesc(Movie.MovieStatus.ACTIVE, pageable);
    }
    
    /**
     * 按点赞数排序
     */
    private Page<Movie> getRankingByLikes(Pageable pageable, String timeRange) {
        // 暂时忽略时间范围，直接按点赞数排序
        return movieRepository.findByStatusOrderByLikesDesc(Movie.MovieStatus.ACTIVE, pageable);
    }
    
    /**
     * 按收藏数排序
     */
    private Page<Movie> getRankingByFavorites(Pageable pageable, String timeRange) {
        // 暂时忽略时间范围，直接按收藏数排序
        return movieRepository.findByStatusOrderByFavoritesDesc(Movie.MovieStatus.ACTIVE, pageable);
    }
    
    /**
     * 按最新排序
     */
    private Page<Movie> getRankingByNewest(Pageable pageable, String timeRange) {
        // 暂时忽略时间范围，直接按创建时间排序
        return movieRepository.findByStatusOrderByCreatedAtDesc(Movie.MovieStatus.ACTIVE, pageable);
    }
    
    @Override
    @Transactional
    public void recordView(Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie != null && movie.getStatus() == Movie.MovieStatus.ACTIVE) {
            movie.setViews(movie.getViews() + 1);
            movieRepository.save(movie);
        }
    }
    
    @Override
    @Transactional
    public boolean likeMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        
        if (movie == null || user == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
            return false;
        }
        
        movie.setLikes(movie.getLikes() + 1);
        movieRepository.save(movie);
        return true;
    }
    
    @Override
    @Transactional
    public boolean unlikeMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        
        if (movie == null || user == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
            return false;
        }
        
        if (movie.getLikes() > 0) {
            movie.setLikes(movie.getLikes() - 1);
            movieRepository.save(movie);
        }
        return true;
    }
    
    @Override
    public boolean checkUserLiked(Long movieId, Long userId) {
        // 由于当前系统没有用户点赞记录表，我们暂时返回false
        // 在实际项目中，应该查询用户点赞记录表
        log.debug("检查用户点赞状态，电影ID: {}, 用户ID: {}", movieId, userId);
        return false;
    }
    
    @Override
    @Transactional
    public boolean favoriteMovie(Long movieId, Long userId) {
        log.debug("收藏电影，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            
            if (movie == null || user == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
                log.warn("电影或用户不存在，或电影状态异常，电影ID: {}, 用户ID: {}", movieId, userId);
                return false;
            }
            
            // 使用收藏服务添加收藏
            boolean success = userFavoriteService.addFavorite(userId, movieId, "MOVIE", movie.getTitle(), movie.getCover());
            
            if (success) {
                // 增加电影收藏次数
                movie.setFavorites(movie.getFavorites() + 1);
                movieRepository.save(movie);
                log.info("收藏电影成功，电影ID: {}, 用户ID: {}", movieId, userId);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("收藏电影失败，电影ID: {}, 用户ID: {}, 错误: {}", movieId, userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean unfavoriteMovie(Long movieId, Long userId) {
        log.debug("取消收藏电影，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            
            if (movie == null || user == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
                log.warn("电影或用户不存在，或电影状态异常，电影ID: {}, 用户ID: {}", movieId, userId);
                return false;
            }
            
            // 使用收藏服务取消收藏
            boolean success = userFavoriteService.removeFavorite(userId, movieId, "MOVIE");
            
            if (success) {
                // 减少电影收藏次数
                if (movie.getFavorites() > 0) {
                    movie.setFavorites(movie.getFavorites() - 1);
                    movieRepository.save(movie);
                }
                log.info("取消收藏电影成功，电影ID: {}, 用户ID: {}", movieId, userId);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("取消收藏电影失败，电影ID: {}, 用户ID: {}, 错误: {}", movieId, userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public MovieWatchPermission checkWatchPermission(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
            return new MovieWatchPermission(false, "电影不存在或已下架");
        }
        
        if (movie.getIsFree()) {
            return new MovieWatchPermission(true, "可以观看", movie.getTrialDuration(), 
                                          movie.getChargeType().name(), movie.getChargeType().getDescription());
        }
        
        if (userId == null) {
            return new MovieWatchPermission(false, "请先登录", movie.getTrialDuration(), 
                                          movie.getChargeType().name(), movie.getChargeType().getDescription());
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new MovieWatchPermission(false, "用户不存在", movie.getTrialDuration(), 
                                          movie.getChargeType().name(), movie.getChargeType().getDescription());
        }
        
        if (movie.getChargeType() == Movie.ChargeType.VIP) {
            if (user.getIsVip()) {
                return new MovieWatchPermission(true, "VIP用户可以观看", movie.getTrialDuration(), 
                                              movie.getChargeType().name(), movie.getChargeType().getDescription());
            } else {
                // 检查是否已单片购买该电影
                boolean hasPurchased = checkMoviePurchase(userId, movieId);
                if (hasPurchased) {
                    return new MovieWatchPermission(true, "已购买，可以观看", movie.getTrialDuration(), 
                                                  movie.getChargeType().name(), movie.getChargeType().getDescription());
                } else {
                    return new MovieWatchPermission(false, "该电影为VIP电影，请开通VIP或单片购买", movie.getTrialDuration(), 
                                                  movie.getChargeType().name(), movie.getChargeType().getDescription());
                }
            }
        }
        
        return new MovieWatchPermission(false, "无法观看该电影", movie.getTrialDuration(), 
                                      movie.getChargeType().name(), movie.getChargeType().getDescription());
    }
    
    @Override
    public MovieFilterOptions getFilterOptions() {
        List<YearOption> years = movieRepository.findAllByStatusOrderBySortOrderDescCreatedAtDesc(Movie.MovieStatus.ACTIVE)
                .stream()
                .filter(movie -> movie.getReleaseYear() != null)
                .collect(Collectors.groupingBy(Movie::getReleaseYear, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new YearOption(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getYear().compareTo(a.getYear()))
                .collect(Collectors.toList());
        
        List<QualityOption> qualities = movieRepository.findAllByStatusOrderBySortOrderDescCreatedAtDesc(Movie.MovieStatus.ACTIVE)
                .stream()
                .filter(movie -> movie.getQuality() != null && !movie.getQuality().isEmpty())
                .collect(Collectors.groupingBy(Movie::getQuality, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new QualityOption(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        List<ChargeTypeOption> chargeTypes = movieRepository.findAllByStatusOrderBySortOrderDescCreatedAtDesc(Movie.MovieStatus.ACTIVE)
                .stream()
                .collect(Collectors.groupingBy(Movie::getChargeType, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new ChargeTypeOption(entry.getKey().name(), entry.getKey().getDescription(), entry.getValue()))
                .collect(Collectors.toList());
        
        return new MovieFilterOptions(years, qualities, chargeTypes);
    }
    
    private H5MovieDTO convertToDTO(Movie movie) {
        H5MovieDTO dto = H5MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .cover(movie.getCover())
                .coverUrl(null) // 封面URL将在下面生成
                .banner(movie.getBanner())
                .bannerUrl(null) // Banner URL将在下面生成
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .views(movie.getViews())
                .likes(movie.getLikes())
                .favorites(movie.getFavorites())
                .releaseDate(movie.getReleaseDate())
                .releaseYear(movie.getReleaseYear())
                .quality(movie.getQuality())
                .isVip(movie.getIsVip())
                .isFree(movie.getIsFree())
                .price(movie.getPrice())
                .trialDuration(movie.getTrialDuration())
                .chargeType(movie.getChargeType().name())
                .chargeTypeDesc(movie.getChargeType().getDescription())
                .fileSizeFormatted(formatFileSize(movie.getFileSize()))
                .fileFormat(movie.getFileFormat())
                .isSuperRecommended(movie.getIsSuperRecommended())
                .build();
        
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
        
        if (movie.getCategory() != null) {
            dto.setCategory(H5MovieCategoryDTO.builder()
                    .id(movie.getCategory().getId())
                    .name(movie.getCategory().getName())
                    .description(movie.getCategory().getDescription())
                    .icon(movie.getCategory().getIcon())
                    .weight(movie.getCategory().getWeight())
                    .build());
        }
        
        if (movie.getRegion() != null) {
            dto.setRegion(H5RegionDTO.builder()
                    .id(movie.getRegion().getId())
                    .name(movie.getRegion().getName())
                    .description(movie.getRegion().getDescription())
                    .icon(movie.getRegion().getIcon())
                    .weight(movie.getRegion().getWeight())
                    .build());
        }
        
        if (movie.getTags() != null && !movie.getTags().isEmpty()) {
            try {
                List<String> tags = objectMapper.readValue(movie.getTags(), new TypeReference<List<String>>() {});
                dto.setTags(tags);
            } catch (Exception e) {
                log.warn("Failed to parse tags for movie {}: {}", movie.getId(), e.getMessage());
                dto.setTags(List.of());
            }
        }
        
        return dto;
    }
    
    private String formatFileSize(Long fileSize) {
        if (fileSize == null) {
            return "未知";
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return df.format(fileSize / 1024.0) + " KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            return df.format(fileSize / (1024.0 * 1024.0)) + " MB";
        } else {
            return df.format(fileSize / (1024.0 * 1024.0 * 1024.0)) + " GB";
        }
    }
    
    // ==================== 新增电影详情和播放功能实现 ====================
    
    @Override
    public H5MovieDetailDTO getMovieDetail(Long movieId) {
        log.debug("获取电影详情，电影ID: {}", movieId);
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        // 检查电影状态
        if (!"ACTIVE".equals(movie.getStatus().toString())) {
            throw new IllegalArgumentException("电影已下架或不存在");
        }
        
        return convertToDetailDTO(movie, null);
    }
    
    @Override
    public H5MovieDetailDTO getMovieDetail(Long movieId, Long userId) {
        log.debug("获取电影详情，电影ID: {}, 用户ID: {}", movieId, userId);
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        // 检查电影状态
        if (!"ACTIVE".equals(movie.getStatus().toString())) {
            throw new IllegalArgumentException("电影已下架或不存在");
        }
        
        // 验证用户是否存在（如果提供了用户ID）
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }
        }
        
        return convertToDetailDTO(movie, userId);
    }
    
    @Override
    public H5MoviePlayResponse checkPlayPermission(H5MoviePlayRequest request) {
        log.debug("检查播放权限，电影ID: {}, 用户ID: {}, 播放类型: {}", 
                request.getMovieId(), request.getUserId(), request.getPlayType());
        
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        // 检查电影状态
        if (!"ACTIVE".equals(movie.getStatus().toString())) {
            return H5MoviePlayResponse.builder()
                    .permission("NOT_ALLOWED")
                    .permissionDesc("电影已下架或不存在")
                    .canPlay(false)
                    .errorMessage("电影已下架或不存在")
                    .build();
        }
        
        String chargeType = movie.getChargeType().toString();
        Long userId = request.getUserId();
        String playType = request.getPlayType();
        
        // 免费电影：直接允许播放
        if ("FREE".equals(chargeType)) {
            return buildPlayResponse("ALLOWED", "免费电影，可直接观看", true, movie, false);
        }
        
        // 未登录用户处理
        if (userId == null) {
            // VIP电影：未登录用户可以提供试看
            if ("VIP".equals(chargeType)) {
                // 检查是否有试看权限（未登录用户也可以试看）
                if (hasTrialPermission(request.getMovieId(), null)) {
                    // 提供试看
                    return buildPlayResponse("TRIAL_ALLOWED", "VIP电影，提供试看", true, movie, true);
                } else {
                    return H5MoviePlayResponse.builder()
                            .permission("LOGIN_REQUIRED")
                            .permissionDesc("VIP电影，请先登录")
                            .canPlay(false)
                            .chargeType(chargeType)
                            .chargeTypeDesc("VIP免费观看")
                            .errorMessage("VIP电影需要登录后才能观看")
                            .build();
                }
            }
        }
        
        // 已登录用户处理
        if (userId != null) {
            // VIP电影：检查VIP权限、单片购买权限和试看权限
            if ("VIP".equals(chargeType)) {
                User user = userRepository.findById(userId).orElse(null);
                boolean isVip = user != null && Boolean.TRUE.equals(user.getIsVip());
                
                if (!isVip) {
                    // 检查是否已单片购买该电影
                    boolean hasPurchased = checkMoviePurchase(userId, request.getMovieId());
                    
                    if (hasPurchased) {
                        // 用户已购买，允许播放
                        return buildPlayResponse("ALLOWED", "已购买，可完整观看", true, movie, false);
                    } else {
                        // 检查是否有试看权限
                        if (hasTrialPermission(request.getMovieId(), userId)) {
                            // 提供试看
                            return buildPlayResponse("TRIAL_ALLOWED", "VIP电影，提供试看", true, movie, true);
                        } else {
                            return H5MoviePlayResponse.builder()
                                    .permission("VIP_REQUIRED")
                                    .permissionDesc("VIP电影，需要VIP权限或单片购买")
                                    .canPlay(false)
                                    .chargeType(chargeType)
                                    .chargeTypeDesc("VIP免费观看")
                                    .price(movie.getPrice().toString())
                                    .errorMessage("此电影需要VIP权限或单片购买才能观看")
                                    .build();
                        }
                    }
                }
            }
        }
        
        // 试看播放
        if ("TRIAL".equals(playType)) {
            // 检查是否有试看权限
            if (hasTrialPermission(request.getMovieId(), userId)) {
                return buildPlayResponse("TRIAL_ALLOWED", "试看模式", true, movie, true);
            } else {
                return H5MoviePlayResponse.builder()
                        .permission("TRIAL_NOT_ALLOWED")
                        .permissionDesc("该电影不支持试看")
                        .canPlay(false)
                        .errorMessage("该电影不支持试看")
                        .build();
            }
        }
        
        // 完整播放
        if ("FULL".equals(playType)) {
            // 免费电影：直接允许播放
            if ("FREE".equals(chargeType)) {
                return buildPlayResponse("ALLOWED", "免费电影，可直接观看", true, movie, false);
            }
            
            // VIP电影：需要VIP权限或单片购买
            if ("VIP".equals(chargeType)) {
                User user = userRepository.findById(userId).orElse(null);
                boolean isVip = user != null && Boolean.TRUE.equals(user.getIsVip());
                
                if (!isVip) {
                    // 检查是否已单片购买该电影
                    boolean hasPurchased = checkMoviePurchase(userId, request.getMovieId());
                    
                    if (hasPurchased) {
                        // 用户已购买，允许播放
                        return buildPlayResponse("ALLOWED", "已购买，可完整观看", true, movie, false);
                    } else {
                        // 检查是否有试看权限
                        if (hasTrialPermission(request.getMovieId(), userId)) {
                            // 提供试看
                            return buildPlayResponse("TRIAL_ALLOWED", "VIP电影，提供试看", true, movie, true);
                        } else {
                            return H5MoviePlayResponse.builder()
                                    .permission("VIP_REQUIRED")
                                    .permissionDesc("VIP电影，需要VIP权限或单片购买")
                                    .canPlay(false)
                                    .chargeType(chargeType)
                                    .chargeTypeDesc("VIP免费观看")
                                    .price(movie.getPrice().toString())
                                    .errorMessage("此电影需要VIP权限或单片购买才能观看")
                                    .build();
                        }
                    }
                }
            }
        }
        
        // 默认情况：不允许播放
        return H5MoviePlayResponse.builder()
                .permission("NOT_ALLOWED")
                .permissionDesc("无法播放该电影")
                .canPlay(false)
                .errorMessage("无法播放该电影")
                .build();
    }
    
    @Override
    public List<H5MovieDetailDTO> getRelatedMovies(Long movieId, Integer limit, Integer offset) {
        log.debug("获取相关推荐电影，电影ID: {}, 限制数量: {}, 偏移量: {}", movieId, limit, offset);
        
        // 获取当前电影信息
        Movie currentMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        List<Movie> allRelatedMovies = new ArrayList<>();
        
        // 方法1：根据分类和地区获取相关电影
        try {
            List<Movie> categoryRegionMovies = movieRepository.findRelatedMovies(
                    currentMovie.getCategory().getId(),
                    currentMovie.getRegion().getId(),
                    movieId,
                    Movie.MovieStatus.ACTIVE,
                    org.springframework.data.domain.PageRequest.of(0, limit * 2) // 获取更多电影用于随机选择
            );
            allRelatedMovies.addAll(categoryRegionMovies);
        } catch (Exception e) {
            log.warn("根据分类和地区获取相关电影失败: {}", e.getMessage());
        }
        
        // 方法2：如果方法1没有获取到足够的电影，获取热门电影作为备选
        if (allRelatedMovies.size() < limit) {
            try {
                List<Movie> hotMovies = movieRepository.findHotMoviesByStatus(
                        Movie.MovieStatus.ACTIVE,
                        org.springframework.data.domain.PageRequest.of(0, limit * 2)
                );
                
                // 过滤掉当前电影和已经添加的电影
                List<Movie> additionalMovies = hotMovies.stream()
                        .filter(movie -> !movie.getId().equals(movieId))
                        .filter(movie -> allRelatedMovies.stream().noneMatch(rm -> rm.getId().equals(movie.getId())))
                        .limit(limit - allRelatedMovies.size())
                        .collect(Collectors.toList());
                
                allRelatedMovies.addAll(additionalMovies);
            } catch (Exception e) {
                log.warn("获取热门电影作为备选失败: {}", e.getMessage());
            }
        }
        
        // 方法3：如果还是没有足够的电影，获取最新电影
        if (allRelatedMovies.size() < limit) {
            try {
                List<Movie> newMovies = movieRepository.findNewMoviesByStatus(
                        Movie.MovieStatus.ACTIVE,
                        org.springframework.data.domain.PageRequest.of(0, limit * 2)
                );
                
                // 过滤掉当前电影和已经添加的电影
                List<Movie> additionalMovies = newMovies.stream()
                        .filter(movie -> !movie.getId().equals(movieId))
                        .filter(movie -> allRelatedMovies.stream().noneMatch(rm -> rm.getId().equals(movie.getId())))
                        .limit(limit - allRelatedMovies.size())
                        .collect(Collectors.toList());
                
                allRelatedMovies.addAll(additionalMovies);
            } catch (Exception e) {
                log.warn("获取最新电影作为备选失败: {}", e.getMessage());
            }
        }
        
        // 使用偏移量进行随机选择
        List<Movie> finalMovies;
        if (allRelatedMovies.size() >= limit) {
            int startIndex = (offset % allRelatedMovies.size());
            int endIndex = Math.min(startIndex + limit, allRelatedMovies.size());
            finalMovies = allRelatedMovies.subList(startIndex, endIndex);
        } else {
            finalMovies = allRelatedMovies;
        }
        
        log.debug("最终获取到 {} 部相关电影", finalMovies.size());
        
        return finalMovies.stream()
                .map(movie -> convertToDetailDTO(movie, null))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void incrementViews(Long movieId) {
        log.debug("增加观看次数，电影ID: {}", movieId);
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("电影不存在"));
        
        movie.setViews(movie.getViews() + 1);
        movieRepository.save(movie);
    }
    
    /**
     * 构建播放响应
     */
    private H5MoviePlayResponse buildPlayResponse(String permission, String permissionDesc, 
                                                boolean canPlay, Movie movie, boolean isTrial) {
        String playUrl = minioService.getFileUrl(movie.getFilePath());
        
        return H5MoviePlayResponse.builder()
                .permission(permission)
                .permissionDesc(permissionDesc)
                .canPlay(canPlay)
                .playUrl(playUrl)
                .trialDuration(movie.getTrialDuration())
                .isTrial(isTrial)
                .trialEndTime(isTrial ? movie.getTrialDuration() : null)
                .chargeType(movie.getChargeType().toString())
                .chargeTypeDesc(getChargeTypeDesc(movie.getChargeType().toString()))
                .build();
    }
    
    /**
     * 获取收费类型描述
     */
    private String getChargeTypeDesc(String chargeType) {
        switch (chargeType) {
            case "FREE": return "免费";
            case "VIP": return "VIP免费观看";
            default: return "未知";
        }
    }
    
    /**
     * 转换为详情DTO
     */
    private H5MovieDetailDTO convertToDetailDTO(Movie movie, Long userId) {
        // 获取封面URL
        String coverUrl = minioService.getFileUrl(movie.getCover());
        
        // 获取Banner URL
        String bannerUrl = null;
        if (movie.getBanner() != null && !movie.getBanner().trim().isEmpty()) {
            try {
                bannerUrl = minioService.getFileUrl(movie.getBanner());
            } catch (Exception e) {
                log.warn("生成Banner URL失败: {}", e.getMessage());
            }
        }
        
        // 获取文件URL
        String fileUrl = minioService.getFileUrl(movie.getFilePath());
        
        // 检查用户是否已购买该电影
        Boolean isPurchased = null;
        Boolean isLiked = null;
        Boolean isFavorited = null;
        
        if (userId != null) {
            isPurchased = hasUserPurchasedMovie(userId, movie.getId());
            isLiked = checkUserLiked(movie.getId(), userId);
            isFavorited = userFavoriteService.checkUserFavorited(userId, movie.getId(), "MOVIE");
        }
        
        // 根据购买状态确定收费类型描述
        String chargeTypeDesc;
        if (isPurchased != null && isPurchased) {
            chargeTypeDesc = "已购买";
        } else {
            chargeTypeDesc = getChargeTypeDesc(movie.getChargeType().toString());
        }
        
        return H5MovieDetailDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .cover(movie.getCover())
                .coverUrl(coverUrl)
                .banner(movie.getBanner())
                .bannerUrl(bannerUrl)
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .views(movie.getViews())
                .likes(movie.getLikes())
                .favorites(movie.getFavorites())
                .releaseYear(movie.getReleaseYear())
                .category(convertToCategoryDTO(movie.getCategory()))
                .region(convertToRegionDTO(movie.getRegion()))
                .quality(movie.getQuality())
                .tags(parseTags(movie.getTags()))
                .isVip(movie.getIsVip())
                .isFree(movie.getIsFree())
                .price(movie.getPrice())
                .trialDuration(movie.getTrialDuration())
                .chargeType(movie.getChargeType().toString())
                .chargeTypeDesc(chargeTypeDesc)
                .status(movie.getStatus().toString())
                .sortOrder(movie.getSortOrder())
                .isRecommended(movie.getIsRecommended())
                .isSuperRecommended(movie.getIsSuperRecommended())
                .filePath(movie.getFilePath())
                .fileUrl(fileUrl)
                .fileSize(movie.getFileSize())
                .fileFormat(movie.getFileFormat())
                .createdAt(movie.getCreatedAt().toString())
                .updatedAt(movie.getUpdatedAt().toString())
                .isPurchased(isPurchased)
                .isLiked(isLiked)
                .isFavorited(isFavorited)
                .build();
    }
    
    /**
     * 转换为分类DTO
     */
    private H5MovieCategoryDTO convertToCategoryDTO(MovieCategory category) {
        if (category == null) return null;
        
        return H5MovieCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .weight(category.getWeight())
                .build();
    }
    
    /**
     * 转换为地区DTO
     */
    private H5RegionDTO convertToRegionDTO(Region region) {
        if (region == null) return null;
        
        return H5RegionDTO.builder()
                .id(region.getId())
                .name(region.getName())
                .description(region.getDescription())
                .icon(region.getIcon())
                .weight(region.getWeight())
                .build();
    }
    
    /**
     * 解析标签字符串为列表
     */
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        
        try {
            // 尝试解析JSON格式的标签
            if (tags.startsWith("[") && tags.endsWith("]")) {
                return objectMapper.readValue(tags, new TypeReference<List<String>>() {});
            } else {
                // 如果不是JSON格式，按逗号分隔
                return java.util.Arrays.asList(tags.split(","));
            }
        } catch (Exception e) {
            log.warn("解析标签失败: {}", tags);
            return List.of();
        }
    }
    
    // ==================== 试看功能实现 ====================
    
    @Override
    public TrialVideoInfo getTrialVideoUrl(Long movieId, Long userId) {
        log.debug("获取试看视频URL，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) {
                return new TrialVideoInfo(false, null, null, "电影不存在");
            }
            
            if (movie.getStatus() != Movie.MovieStatus.ACTIVE) {
                return new TrialVideoInfo(false, null, null, "电影已下架");
            }
            
            // 检查是否有试看时长
            if (movie.getTrialDuration() == null || movie.getTrialDuration() <= 0) {
                return new TrialVideoInfo(false, null, null, "该电影不支持试看");
            }
            
            // 检查用户权限（如果提供了用户ID）
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && Boolean.TRUE.equals(user.getIsVip())) {
                    return new TrialVideoInfo(false, null, null, "VIP用户无需试看，可直接观看完整内容");
                }
            }
            
            // 生成试看URL（这里可以根据需要实现具体的试看逻辑）
            String trialUrl = generateTrialUrl(movie);
            
            return new TrialVideoInfo(true, trialUrl, movie.getTrialDuration(), "试看可用");
            
        } catch (Exception e) {
            log.error("获取试看视频URL异常: {}", e.getMessage(), e);
            return new TrialVideoInfo(false, null, null, "获取试看信息失败");
        }
    }
    
    @Override
    public boolean hasTrialPermission(Long movieId, Long userId) {
        log.debug("检查试看权限，电影ID: {}, 用户ID: {}", movieId, userId);
        
        try {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null || movie.getStatus() != Movie.MovieStatus.ACTIVE) {
                return false;
            }
            
            // 检查是否有试看时长
            if (movie.getTrialDuration() == null || movie.getTrialDuration() <= 0) {
                return false;
            }
            
            // 检查用户权限（如果提供了用户ID）
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && Boolean.TRUE.equals(user.getIsVip())) {
                    return false; // VIP用户无需试看
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("检查试看权限异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 生成试看URL
     * @param movie 电影信息
     * @return 试看URL
     */
    private String generateTrialUrl(Movie movie) {
        // 这里可以根据实际需求实现试看URL生成逻辑
        // 例如：添加试看参数、时间限制等
        if (movie.getFilePath() != null && !movie.getFilePath().isEmpty()) {
            return "/api/movies/stream/" + movie.getId() + "?trial=true&duration=" + movie.getTrialDuration();
        }
        return null;
    }

    /**
     * 检查用户是否已单片购买电影
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return true if purchased, false otherwise
     */
    private boolean checkMoviePurchase(Long userId, Long movieId) {
        if (userId == null || movieId == null) {
            return false;
        }
        return userMoviePurchaseRepository.existsByUserIdAndMovieId(userId, movieId);
    }
    
    // ==================== 电影购买功能实现 ====================
    
    @Override
    public boolean hasUserPurchasedMovie(Long userId, Long movieId) {
        if (userId == null || movieId == null) {
            return false;
        }
        return userMoviePurchaseRepository.existsByUserIdAndMovieId(userId, movieId);
    }
    
    @Override
    public Page<H5MovieDTO> getUserPurchasedMovies(Long userId, Pageable pageable) {
        if (userId == null) {
            return Page.empty(pageable);
        }
        
        Page<UserMoviePurchase> purchases = userMoviePurchaseRepository.findByUserIdOrderByPurchaseTimeDesc(userId, pageable);
        return purchases.map(purchase -> convertToDTO(purchase.getMovie()));
    }
    
    @Override
    public List<Long> getUserPurchasedMovieIds(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userMoviePurchaseRepository.findMovieIdsByUserId(userId);
    }
} 