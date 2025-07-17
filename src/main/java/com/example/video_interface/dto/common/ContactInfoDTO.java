package com.example.video_interface.dto.common;

import lombok.Data;

/**
 * 联系方式DTO
 * 用于返回客服页面的联系方式信息
 */
@Data
public class ContactInfoDTO {
    /** 微信 */
    private String wechat;
    /** Telegram */
    private String telegram;
    /** 系统邮箱 */
    private String systemEmail;
    /** 网站名称 */
    private String siteName;
    /** 网站描述 */
    private String siteDesc;
} 