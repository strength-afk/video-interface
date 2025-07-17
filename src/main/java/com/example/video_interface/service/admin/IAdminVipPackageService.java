package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminVipPackageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;

/**
 * VIP套餐管理服务接口
 * 定义后台VIP套餐的所有管理操作
 */
public interface IAdminVipPackageService {
    /**
     * 分页查询VIP套餐列表
     * @param params 查询参数（支持name、status、分页等）
     * @param pageable 分页对象
     * @return 分页结果
     */
    Page<AdminVipPackageDTO> getVipPackageList(Map<String, Object> params, Pageable pageable);

    /**
     * 获取VIP套餐详情
     * @param id 套餐ID
     * @return 套餐详情DTO
     */
    AdminVipPackageDTO getVipPackageDetail(Long id);

    /**
     * 创建VIP套餐
     * @param dto 套餐DTO
     * @return 创建后的DTO
     */
    AdminVipPackageDTO createVipPackage(AdminVipPackageDTO dto);

    /**
     * 更新VIP套餐
     * @param dto 套餐DTO
     * @return 更新后的DTO
     */
    AdminVipPackageDTO updateVipPackage(AdminVipPackageDTO dto);

    /**
     * 删除VIP套餐（逻辑删除，设为DELETED）
     * @param id 套餐ID
     * @return 是否成功
     */
    boolean deleteVipPackage(Long id);

    /**
     * 上下架VIP套餐（ACTIVE/INACTIVE切换）
     * @param id 套餐ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean toggleVipPackageStatus(Long id, String status);

    /**
     * 获取所有VIP套餐（不分页，供H5端使用）
     * @return 套餐DTO列表
     */
    List<AdminVipPackageDTO> getAllVipPackages();
} 