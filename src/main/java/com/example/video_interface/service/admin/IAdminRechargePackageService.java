package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminRechargePackageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;

/**
 * 充值套餐管理服务接口
 * 定义后台充值套餐的所有管理操作
 */
public interface IAdminRechargePackageService {
    /**
     * 分页查询充值套餐列表
     * @param params 查询参数（支持name、status、分页等）
     * @param pageable 分页对象
     * @return 分页结果
     */
    Page<AdminRechargePackageDTO> getRechargePackageList(Map<String, Object> params, Pageable pageable);

    /**
     * 获取充值套餐详情
     * @param id 套餐ID
     * @return 套餐详情DTO
     */
    AdminRechargePackageDTO getRechargePackageDetail(Long id);

    /**
     * 创建充值套餐
     * @param dto 套餐DTO
     * @return 创建后的DTO
     */
    AdminRechargePackageDTO createRechargePackage(AdminRechargePackageDTO dto);

    /**
     * 更新充值套餐
     * @param dto 套餐DTO
     * @return 更新后的DTO
     */
    AdminRechargePackageDTO updateRechargePackage(AdminRechargePackageDTO dto);

    /**
     * 删除充值套餐（逻辑删除，设为DELETED）
     * @param id 套餐ID
     * @return 是否成功
     */
    boolean deleteRechargePackage(Long id);

    /**
     * 上下架充值套餐（ACTIVE/INACTIVE切换）
     * @param id 套餐ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean toggleRechargePackageStatus(Long id, String status);

    /**
     * 获取所有充值套餐（不分页，供激活码管理使用）
     * @return 套餐DTO列表
     */
    List<AdminRechargePackageDTO> getAllRechargePackages();
} 