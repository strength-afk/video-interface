package com.example.video_interface.repository;

import com.example.video_interface.model.UserMoviePurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户电影购买记录Repository接口
 */
@Repository
public interface UserMoviePurchaseRepository extends JpaRepository<UserMoviePurchase, Long> {
    
    /**
     * 根据用户ID和电影ID查询购买记录
     * 
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return 购买记录
     */
    Optional<UserMoviePurchase> findByUserIdAndMovieId(Long userId, Long movieId);
    
    /**
     * 检查用户是否已购买指定电影
     * 
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return 是否已购买
     */
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    
    /**
     * 根据用户ID查询购买记录（分页）
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 购买记录分页结果
     */
    Page<UserMoviePurchase> findByUserIdOrderByPurchaseTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID查询所有购买的电影ID列表
     * 
     * @param userId 用户ID
     * @return 电影ID列表
     */
    @Query("SELECT ump.movie.id FROM UserMoviePurchase ump WHERE ump.user.id = :userId AND ump.status = 'SUCCESS'")
    List<Long> findMovieIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID统计购买的电影数量
     * 
     * @param userId 用户ID
     * @return 购买的电影数量
     */
    @Query("SELECT COUNT(ump) FROM UserMoviePurchase ump WHERE ump.user.id = :userId AND ump.status = 'SUCCESS'")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 根据电影ID统计购买用户数量
     * 
     * @param movieId 电影ID
     * @return 购买用户数量
     */
    @Query("SELECT COUNT(ump) FROM UserMoviePurchase ump WHERE ump.movie.id = :movieId AND ump.status = 'SUCCESS'")
    long countByMovieId(@Param("movieId") Long movieId);
    
    /**
     * 根据用户ID和电影ID删除购买记录
     * 
     * @param userId 用户ID
     * @param movieId 电影ID
     */
    void deleteByUserIdAndMovieId(Long userId, Long movieId);
} 