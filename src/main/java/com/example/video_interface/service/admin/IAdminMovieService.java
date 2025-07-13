package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminMovieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 管理后台电影服务接口
 */
public interface IAdminMovieService {
    
    /**
     * 获取电影列表（分页）
     * @param pageable 分页参数
     * @param keyword 关键词搜索
     * @param categoryId 分类ID
     * @param regionId 地区ID
     * @param status 状态
     * @param chargeType 收费类型
     * @return 电影列表
     */
    Page<AdminMovieDTO> getMovieList(Pageable pageable, String keyword, Long categoryId, 
                                   Long regionId, String status, String chargeType);
    
    /**
     * 获取电影详情
     * @param id 电影ID
     * @return 电影详情
     */
    AdminMovieDTO getMovieById(Long id);
    
    /**
     * 创建电影
     * @param movieDTO 电影信息
     * @return 创建后的电影
     */
    AdminMovieDTO createMovie(AdminMovieDTO movieDTO);
    
    /**
     * 更新电影
     * @param id 电影ID
     * @param movieDTO 电影信息
     * @return 更新后的电影
     */
    AdminMovieDTO updateMovie(Long id, AdminMovieDTO movieDTO);
    
    /**
     * 删除电影
     * @param id 电影ID
     */
    void deleteMovie(Long id);
    
    /**
     * 更新电影状态
     * @param id 电影ID
     * @param status 新状态
     * @return 更新后的电影
     */
    AdminMovieDTO updateMovieStatus(Long id, String status);
    
    /**
     * 设置推荐状态
     * @param id 电影ID
     * @param isRecommended 是否推荐
     * @return 更新后的电影
     */
    AdminMovieDTO setRecommendStatus(Long id, Boolean isRecommended);
    
    /**
     * 设置超级推荐状态
     * @param id 电影ID
     * @param isSuperRecommended 是否超级推荐
     * @return 更新后的电影
     */
    AdminMovieDTO setSuperRecommendStatus(Long id, Boolean isSuperRecommended);
    
    /**
     * 获取超级推荐电影列表
     * @return 超级推荐电影列表
     */
    List<AdminMovieDTO> getSuperRecommendedMovies();
} 