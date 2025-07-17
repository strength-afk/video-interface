package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5RechargePackageDTO;
import java.util.List;

/**
 * H5端充值套餐服务接口
 */
public interface IH5RechargePackageService {
    
    /**
     * 获取所有ACTIVE状态的充值套餐
     * @return 充值套餐列表
     */
    List<H5RechargePackageDTO> getActiveRechargePackages();
} 