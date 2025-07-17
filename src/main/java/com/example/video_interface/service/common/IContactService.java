package com.example.video_interface.service.common;

import com.example.video_interface.dto.common.ContactInfoDTO;

/**
 * 联系方式服务接口
 * 提供获取客服联系方式等公开功能
 */
public interface IContactService {
    /**
     * 获取联系方式信息
     * @return 联系方式DTO
     */
    ContactInfoDTO getContactInfo();
} 