package com.example.video_interface.repository;

import com.example.video_interface.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 电影数据访问层
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    /**
     * 根据ID和状态查询电影
     */
    Movie findByIdAndStatus(Long id, Movie.MovieStatus status);
    
    /**
     * 根据状态查询热门电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.views DESC, m.likes DESC")
    List<Movie> findHotMoviesByStatus(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态查询最新电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.createdAt DESC")
    List<Movie> findNewMoviesByStatus(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态和最低评分查询高评分电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status AND m.rating >= :minRating ORDER BY m.rating DESC")
    List<Movie> findHighRatedMoviesByStatus(@Param("status") Movie.MovieStatus status, 
                                           @Param("minRating") Double minRating, 
                                           Pageable pageable);
    
    /**
     * 根据分类ID和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.category.id = :categoryId AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByCategoryIdAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("categoryId") Long categoryId, 
                                                                          @Param("status") Movie.MovieStatus status, 
                                                                          Pageable pageable);
    
    /**
     * 根据地区ID和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.region.id = :regionId AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByRegionIdAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("regionId") Long regionId, 
                                                                        @Param("status") Movie.MovieStatus status, 
                                                                        Pageable pageable);
    
    /**
     * 根据年份和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.releaseYear = :year AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByReleaseYearAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("year") Integer year, 
                                                                           @Param("status") Movie.MovieStatus status, 
                                                                           Pageable pageable);
    
    /**
     * 根据收费类型和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.chargeType = :chargeType AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByChargeTypeAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("chargeType") Movie.ChargeType chargeType, 
                                                                          @Param("status") Movie.MovieStatus status, 
                                                                          Pageable pageable);
    
    /**
     * 根据VIP状态和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.isVip = :isVip AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByIsVipAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("isVip") Boolean isVip, 
                                                                     @Param("status") Movie.MovieStatus status, 
                                                                     Pageable pageable);
    
    /**
     * 根据免费状态和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.isFree = :isFree AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByIsFreeAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("isFree") Boolean isFree, 
                                                                      @Param("status") Movie.MovieStatus status, 
                                                                      Pageable pageable);
    
    /**
     * 根据画质和状态查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.quality = :quality AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByQualityAndStatusOrderBySortOrderDescCreatedAtDesc(@Param("quality") String quality, 
                                                                       @Param("status") Movie.MovieStatus status, 
                                                                       Pageable pageable);
    
    /**
     * 根据关键词搜索电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status AND (m.title LIKE %:keyword% OR m.description LIKE %:keyword%) ORDER BY m.sortOrder DESC, m.createdAt DESC")
    List<Movie> searchByKeyword(@Param("status") Movie.MovieStatus status, 
                               @Param("keyword") String keyword, 
                               Pageable pageable);
    
    /**
     * 根据标签搜索电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status AND m.tags LIKE %:tag% ORDER BY m.sortOrder DESC, m.createdAt DESC")
    List<Movie> searchByTag(@Param("status") Movie.MovieStatus status, 
                           @Param("tag") String tag, 
                           Pageable pageable);
    
    /**
     * 根据状态查询所有电影（分页）
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByStatusOrderBySortOrderDescCreatedAtDesc(@Param("status") Movie.MovieStatus status, 
                                                             Pageable pageable);
    
    /**
     * 根据状态查询所有电影（不分页）
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    List<Movie> findAllByStatusOrderBySortOrderDescCreatedAtDesc(@Param("status") Movie.MovieStatus status);
    
    /**
     * 根据条件查询电影列表
     */
    @Query("SELECT m FROM Movie m " +
           "LEFT JOIN m.category c " +
           "LEFT JOIN m.region r " +
           "WHERE (:keyword IS NULL OR m.title LIKE %:keyword% OR m.description LIKE %:keyword%) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "AND (:regionId IS NULL OR r.id = :regionId) " +
           "AND (:status IS NULL OR m.status = :status) " +
           "AND (:chargeType IS NULL OR m.chargeType = :chargeType) " +
           "ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByConditions(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("regionId") Long regionId,
                                @Param("status") Movie.MovieStatus status,
                                @Param("chargeType") Movie.ChargeType chargeType,
                                Pageable pageable);
    
    /**
     * 复合条件查询电影
     */
    @Query("SELECT m FROM Movie m " +
           "LEFT JOIN m.category c " +
           "LEFT JOIN m.region r " +
           "WHERE m.status = :status " +
           "AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "AND (:regionId IS NULL OR r.id = :regionId) " +
           "AND (:releaseYear IS NULL OR m.releaseYear = :releaseYear) " +
           "AND (:chargeType IS NULL OR m.chargeType = :chargeType) " +
           "AND (:isVip IS NULL OR m.isVip = :isVip) " +
           "AND (:quality IS NULL OR m.quality = :quality) " +
           "ORDER BY m.sortOrder DESC, m.createdAt DESC")
    Page<Movie> findByConditions(@Param("status") Movie.MovieStatus status,
                                @Param("categoryId") Long categoryId,
                                @Param("regionId") Long regionId,
                                @Param("releaseYear") Integer releaseYear,
                                @Param("chargeType") Movie.ChargeType chargeType,
                                @Param("isVip") Boolean isVip,
                                @Param("quality") String quality,
                                Pageable pageable);
    
    /**
     * 根据标题查询电影
     */
    boolean existsByTitle(String title);
    
    /**
     * 根据标题查询电影（排除指定ID）
     */
    boolean existsByTitleAndIdNot(String title, Long id);
    
    /**
     * 根据状态统计电影数量
     */
    long countByStatus(Movie.MovieStatus status);
    
    /**
     * 查询超级推荐电影
     */
    @Query("SELECT m FROM Movie m WHERE m.isSuperRecommended = true AND m.status = :status ORDER BY m.sortOrder DESC, m.createdAt DESC")
    List<Movie> findSuperRecommendedMoviesByStatus(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 统计超级推荐电影数量
     */
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.isSuperRecommended = true AND m.status = :status")
    long countSuperRecommendedMoviesByStatus(@Param("status") Movie.MovieStatus status);
    
    /**
     * 根据分类和地区获取相关电影
     */
    @Query("SELECT m FROM Movie m " +
           "WHERE m.status = :status " +
           "AND m.id != :excludeMovieId " +
           "AND (m.category.id = :categoryId OR m.region.id = :regionId) " +
           "ORDER BY m.sortOrder DESC, m.createdAt DESC")
    List<Movie> findRelatedMovies(@Param("categoryId") Long categoryId,
                                 @Param("regionId") Long regionId,
                                 @Param("excludeMovieId") Long excludeMovieId,
                                 @Param("status") Movie.MovieStatus status,
                                 Pageable pageable);
    
    // ==================== 排行榜相关查询方法 ====================
    
    /**
     * 根据状态按观看次数排序查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.views DESC, m.likes DESC")
    Page<Movie> findByStatusOrderByViewsDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态按评分排序查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.rating DESC, m.views DESC")
    Page<Movie> findByStatusOrderByRatingDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态按点赞数排序查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.likes DESC, m.views DESC")
    Page<Movie> findByStatusOrderByLikesDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态按收藏数排序查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.favorites DESC, m.views DESC")
    Page<Movie> findByStatusOrderByFavoritesDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态按创建时间排序查询电影
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.createdAt DESC")
    Page<Movie> findByStatusOrderByCreatedAtDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    // ==================== 热门电影排序查询方法 ====================
    
    /**
     * 根据状态查询热门电影并按评分排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.rating DESC, m.views DESC")
    List<Movie> findHotMoviesByStatusOrderByRatingDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态查询热门电影并按年份排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.releaseYear DESC, m.views DESC")
    List<Movie> findHotMoviesByStatusOrderByReleaseYearDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态查询热门电影并按观看次数排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.views DESC, m.likes DESC")
    List<Movie> findHotMoviesByStatusOrderByViewsDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    // ==================== 最新电影排序查询方法 ====================
    
    /**
     * 根据状态查询最新电影并按评分排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.rating DESC, m.createdAt DESC")
    List<Movie> findNewMoviesByStatusOrderByRatingDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态查询最新电影并按年份排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.releaseYear DESC, m.createdAt DESC")
    List<Movie> findNewMoviesByStatusOrderByReleaseYearDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据状态查询最新电影并按观看次数排序
     */
    @Query("SELECT m FROM Movie m WHERE m.status = :status ORDER BY m.views DESC, m.createdAt DESC")
    List<Movie> findNewMoviesByStatusOrderByViewsDesc(@Param("status") Movie.MovieStatus status, Pageable pageable);
    
    // ==================== 免费电影排序查询方法 ====================
    
    /**
     * 根据免费状态查询电影并按评分排序
     */
    @Query("SELECT m FROM Movie m WHERE m.isFree = :isFree AND m.status = :status ORDER BY m.rating DESC, m.sortOrder DESC")
    Page<Movie> findByIsFreeAndStatusOrderByRatingDesc(@Param("isFree") Boolean isFree, @Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据免费状态查询电影并按年份排序
     */
    @Query("SELECT m FROM Movie m WHERE m.isFree = :isFree AND m.status = :status ORDER BY m.releaseYear DESC, m.sortOrder DESC")
    Page<Movie> findByIsFreeAndStatusOrderByReleaseYearDesc(@Param("isFree") Boolean isFree, @Param("status") Movie.MovieStatus status, Pageable pageable);
    
    /**
     * 根据免费状态查询电影并按观看次数排序
     */
    @Query("SELECT m FROM Movie m WHERE m.isFree = :isFree AND m.status = :status ORDER BY m.views DESC, m.sortOrder DESC")
    Page<Movie> findByIsFreeAndStatusOrderByViewsDesc(@Param("isFree") Boolean isFree, @Param("status") Movie.MovieStatus status, Pageable pageable);
} 