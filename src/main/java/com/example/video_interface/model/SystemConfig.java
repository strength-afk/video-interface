package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

/**
 * 系统设置实体，仅用于system_config表ORM映射
 */
@Data
@Entity
@Table(name = "system_config")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键")
    private Long id;

    @Column(name = "site_name")
    @Comment("网站名称")
    private String siteName;

    @Column(name = "site_desc")
    @Comment("网站描述")
    private String siteDesc;

    @Column(name = "wechat")
    @Comment("微信")
    private String wechat;

    @Column(name = "telegram")
    @Comment("Telegram")
    private String telegram;

    @Column(name = "system_email")
    @Comment("系统邮箱")
    private String systemEmail;

    @Column(name = "version")
    @Comment("系统版本号")
    private String version;

    @Column(name = "copyright")
    @Comment("版权信息")
    private String copyright;

    @Column(name = "allow_register")
    @Comment("是否允许注册")
    private Boolean allowRegister;

    @Column(name = "gmt_create", updatable = false)
    @Comment("创建时间")
    private LocalDateTime gmtCreate;

    @Column(name = "gmt_modified")
    @Comment("修改时间")
    private LocalDateTime gmtModified;

    @Column(name = "mail_user")
    @Comment("发件邮箱账号")
    private String mailUser;

    @Column(name = "mail_pass")
    @Comment("发件邮箱密码/授权码")
    private String mailPass;
} 