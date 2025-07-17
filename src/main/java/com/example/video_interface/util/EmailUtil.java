package com.example.video_interface.util;

import com.example.video_interface.dto.common.SystemConfigDTO;
import com.example.video_interface.service.common.ISystemConfigService;
import jakarta.annotation.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 智能邮箱工具类
 * 支持根据邮箱地址自动识别SMTP服务器配置
 */
@Slf4j
@Component
public class EmailUtil {

    @Resource
    private ISystemConfigService systemConfigService;

    /**
     * 邮箱配置信息
     */
    private static class MailConfig {
        private final String host;
        private final int port;
        private final String protocol;
        private final boolean sslEnable;
        private final boolean startTlsEnable;

        public MailConfig(String host, int port, String protocol, boolean sslEnable, boolean startTlsEnable) {
            this.host = host;
            this.port = port;
            this.protocol = protocol;
            this.sslEnable = sslEnable;
            this.startTlsEnable = startTlsEnable;
        }
    }

    /**
     * 主流邮箱SMTP配置映射表
     */
    private static final Map<String, MailConfig> MAIL_CONFIGS = createMailConfigs();

    /**
     * 创建邮箱配置映射表
     */
    private static Map<String, MailConfig> createMailConfigs() {
        Map<String, MailConfig> configs = new HashMap<>();
        
        // 国内邮箱
        configs.put("163.com", new MailConfig("smtp.163.com", 465, "smtps", true, false));
        configs.put("126.com", new MailConfig("smtp.126.com", 465, "smtps", true, false));
        configs.put("qq.com", new MailConfig("smtp.qq.com", 465, "smtps", true, false));
        configs.put("sina.com", new MailConfig("smtp.sina.com", 465, "smtps", true, false));
        configs.put("sohu.com", new MailConfig("smtp.sohu.com", 465, "smtps", true, false));
        configs.put("yeah.net", new MailConfig("smtp.yeah.net", 465, "smtps", true, false));
        
        // 国际邮箱
        configs.put("gmail.com", new MailConfig("smtp.gmail.com", 587, "smtp", false, true));
        configs.put("outlook.com", new MailConfig("smtp-mail.outlook.com", 587, "smtp", false, true));
        configs.put("hotmail.com", new MailConfig("smtp-mail.outlook.com", 587, "smtp", false, true));
        configs.put("live.com", new MailConfig("smtp-mail.outlook.com", 587, "smtp", false, true));
        configs.put("yahoo.com", new MailConfig("smtp.mail.yahoo.com", 587, "smtp", false, true));
        configs.put("yahoo.cn", new MailConfig("smtp.mail.yahoo.cn", 587, "smtp", false, true));
        configs.put("aol.com", new MailConfig("smtp.aol.com", 587, "smtp", false, true));
        
        // 企业邮箱常见域名
        configs.put("foxmail.com", new MailConfig("smtp.qq.com", 465, "smtps", true, false));
        configs.put("139.com", new MailConfig("smtp.139.com", 465, "smtps", true, false));
        
        return configs;
    }

    /**
     * 默认邮箱配置（当无法识别时使用）
     */
    private static final MailConfig DEFAULT_CONFIG = new MailConfig("smtp.163.com", 465, "smtps", true, false);

    /**
     * 发送邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) {
        SystemConfigDTO config = systemConfigService.getSystemConfig();
        if (config == null || config.getMailUser() == null || config.getMailPass() == null) {
            throw new RuntimeException("系统未配置发件邮箱账号和密码");
        }

        // 根据发件邮箱地址自动识别SMTP配置
        MailConfig mailConfig = getMailConfig(config.getMailUser());
        
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailConfig.host);
        sender.setPort(mailConfig.port);
        sender.setUsername(config.getMailUser());
        sender.setPassword(config.getMailPass());
        sender.setProtocol(mailConfig.protocol);

        // 设置邮件属性
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        
        if (mailConfig.sslEnable) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(mailConfig.port));
        }
        
        if (mailConfig.startTlsEnable) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(config.getMailUser());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            sender.send(message);
            log.info("邮件发送成功 - 收件人: {}, 主题: {}, 使用服务器: {}:{}", 
                    to, subject, mailConfig.host, mailConfig.port);
        } catch (Exception e) {
            log.error("邮件发送失败 - 收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据邮箱地址获取对应的SMTP配置
     * @param email 邮箱地址
     * @return 邮箱配置
     */
    private MailConfig getMailConfig(String email) {
        if (email == null || !email.contains("@")) {
            log.warn("无效的邮箱地址: {}, 使用默认配置", email);
            return DEFAULT_CONFIG;
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        MailConfig config = MAIL_CONFIGS.get(domain);
        
        if (config != null) {
            log.info("识别邮箱类型: {} -> {}:{}", domain, config.host, config.port);
            return config;
        } else {
            log.warn("未识别的邮箱域名: {}, 使用默认配置", domain);
            return DEFAULT_CONFIG;
        }
    }

    /**
     * 获取邮箱配置信息（用于前端显示）
     * @param email 邮箱地址
     * @return 配置信息字符串
     */
    public String getMailConfigInfo(String email) {
        MailConfig config = getMailConfig(email);
        return String.format("%s:%d (%s)", config.host, config.port, config.protocol);
    }

    /**
     * 检查邮箱是否支持
     * @param email 邮箱地址
     * @return 是否支持
     */
    public boolean isSupportedEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        return MAIL_CONFIGS.containsKey(domain);
    }
} 