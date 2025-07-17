package com.example.video_interface.dto.common;

import lombok.Data;

/**
 * 系统设置DTO
 * 用于系统全局配置的传输对象
 */
@Data
public class SystemConfigDTO {
    /** 网站名称 */
    private String siteName;
    /** 网站描述 */
    private String siteDesc;
    /** 微信 */
    private String wechat;
    /** Telegram */
    private String telegram;
    /** 系统邮箱 */
    private String systemEmail;
    /** 系统版本号 */
    private String version;
    /** 版权信息 */
    private String copyright;
    /** 是否允许注册 */
    private Boolean allowRegister;
    /** 发件邮箱账号 */
    private String mailUser;
    /** 发件邮箱密码/授权码 */
    private String mailPass;
} 