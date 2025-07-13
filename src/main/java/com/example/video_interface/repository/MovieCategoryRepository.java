package com.example.video_interface.repository;

import com.example.video_interface.model.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 电影分类数据访问接口
 */
@Repository
public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Long> {
    
    /**
     * 根据启用状态查询分类列表，并按权重降序排序
     * @param enabled 启用状态
     * @return 分类列表
     */
    List<MovieCategory> findByEnabledOrderByWeightDesc(Boolean enabled);
    
    /**
     * 统计使用指定分类的电影数量
     * @param categoryId 分类ID
     * @return 电影数量
     */
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.category.id = :categoryId")
    long countMoviesByCategoryId(@Param("categoryId") Long categoryId);
} 