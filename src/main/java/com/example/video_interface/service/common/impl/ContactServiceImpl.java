package com.example.video_interface.service.common.impl;

import com.example.video_interface.dto.common.ContactInfoDTO;
import com.example.video_interface.dto.common.SystemConfigDTO;
import com.example.video_interface.service.common.IContactService;
import com.example.video_interface.service.common.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 联系方式服务实现
 * 提供获取客服联系方式等公开功能
 */
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements IContactService {
    
    private final ISystemConfigService systemConfigService;

    /**
     * 获取联系方式信息
     * @return 联系方式DTO
     */
    @Override
    public ContactInfoDTO getContactInfo() {
        SystemConfigDTO config = systemConfigService.getSystemConfig();
        if (config == null) {
            // 如果没有配置，返回默认值
            ContactInfoDTO defaultInfo = new ContactInfoDTO();
            defaultInfo.setSiteName("久伴视频");
            defaultInfo.setSiteDesc("专业的视频内容平台");
            return defaultInfo;
        }
        
        ContactInfoDTO contactInfo = new ContactInfoDTO();
        contactInfo.setWechat(config.getWechat());
        contactInfo.setTelegram(config.getTelegram());
        contactInfo.setSystemEmail(config.getSystemEmail());
        contactInfo.setSiteName(config.getSiteName());
        contactInfo.setSiteDesc(config.getSiteDesc());
        
        return contactInfo;
    }
} 