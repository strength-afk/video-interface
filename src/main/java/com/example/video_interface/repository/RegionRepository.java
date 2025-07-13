package com.example.video_interface.repository;

import com.example.video_interface.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地区数据访问接口
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    
    /**
     * 根据启用状态查询地区列表，并按权重降序排序
     * @param enabled 启用状态
     * @return 地区列表
     */
    List<Region> findByEnabledOrderByWeightDesc(Boolean enabled);
    
    /**
     * 统计使用指定地区的电影数量
     * @param regionId 地区ID
     * @return 电影数量
     */
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.region.id = :regionId")
    long countMoviesByRegionId(@Param("regionId") Long regionId);
} 