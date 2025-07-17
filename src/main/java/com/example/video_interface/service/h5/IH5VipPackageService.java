package com.example.video_interface.service.h5;

import com.example.video_interface.dto.admin.AdminVipPackageDTO;
import java.util.List;

/**
 * H5端VIP套餐Service接口
 */
public interface IH5VipPackageService {
    /**
     * 获取所有ACTIVE状态的VIP套餐
     * @return 套餐列表
     */
    List<AdminVipPackageDTO> getActiveVipPackages();
} 