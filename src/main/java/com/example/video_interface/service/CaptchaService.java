package com.example.video_interface.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 提供验证码生成、验证等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final RedisTemplate<String, String> redisTemplate;

    // 验证码字符集（排除容易混淆的字符）
    private static final String CAPTCHA_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    
    // 验证码配置
    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final int FONT_SIZE = 24;
    private static final int EXPIRE_MINUTES = 5; // 验证码5分钟过期

    /**
     * 生成验证码
     * @param sessionId 会话ID（用于标识验证码归属）
     * @return 验证码图片的Base64编码
     */
    public String generateCaptcha(String sessionId) {
        try {
            // 生成验证码文本
            String captchaText = generateRandomText();
            
            // 存储到Redis，5分钟过期
            String redisKey = "captcha:" + sessionId;
            redisTemplate.opsForValue().set(redisKey, captchaText.toLowerCase(), EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 生成验证码图片
            BufferedImage image = createCaptchaImage(captchaText);
            
            // 转换为Base64
            String base64Image = imageToBase64(image);
            
            log.debug("为会话 {} 生成验证码: {}", sessionId, captchaText);
            
            return base64Image;
            
        } catch (Exception e) {
            log.error("生成验证码失败: {}", e.getMessage(), e);
            throw new RuntimeException("验证码生成失败");
        }
    }

    /**
     * 验证验证码
     * @param sessionId 会话ID
     * @param userInput 用户输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyCaptcha(String sessionId, String userInput) {
        if (sessionId == null || userInput == null) {
            return false;
        }

        try {
            String redisKey = "captcha:" + sessionId;
            String storedCaptcha = redisTemplate.opsForValue().get(redisKey);
            
            if (storedCaptcha == null) {
                log.warn("验证码已过期或不存在: sessionId={}", sessionId);
                return false;
            }
            
            // 验证成功后删除验证码（一次性使用）
            boolean isValid = storedCaptcha.equalsIgnoreCase(userInput.trim());
            if (isValid) {
                redisTemplate.delete(redisKey);
                log.debug("验证码验证成功: sessionId={}", sessionId);
            } else {
                log.warn("验证码验证失败: sessionId={}, 期望={}, 实际={}", sessionId, storedCaptcha, userInput);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("验证码验证异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 刷新验证码（删除旧的）
     * @param sessionId 会话ID
     */
    public void refreshCaptcha(String sessionId) {
        if (sessionId != null) {
            String redisKey = "captcha:" + sessionId;
            redisTemplate.delete(redisKey);
            log.debug("刷新验证码: sessionId={}", sessionId);
        }
    }

    /**
     * 生成随机验证码文本
     */
    private String generateRandomText() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CAPTCHA_CHARS.length());
            captcha.append(CAPTCHA_CHARS.charAt(index));
        }
        
        return captcha.toString();
    }

    /**
     * 创建验证码图片
     */
    private BufferedImage createCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 设置背景色
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
            
            // 生成随机干扰线
            Random random = new Random();
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 8; i++) {
                int x1 = random.nextInt(IMAGE_WIDTH);
                int y1 = random.nextInt(IMAGE_HEIGHT);
                int x2 = random.nextInt(IMAGE_WIDTH);
                int y2 = random.nextInt(IMAGE_HEIGHT);
                g2d.drawLine(x1, y1, x2, y2);
            }
            
            // 绘制验证码文字
            g2d.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
            
            int charWidth = IMAGE_WIDTH / CAPTCHA_LENGTH;
            for (int i = 0; i < captchaText.length(); i++) {
                // 随机颜色
                g2d.setColor(new Color(
                    random.nextInt(100) + 50,  // R: 50-149
                    random.nextInt(100) + 50,  // G: 50-149
                    random.nextInt(100) + 50   // B: 50-149
                ));
                
                // 随机位置和角度
                int x = i * charWidth + 10 + random.nextInt(10);
                int y = IMAGE_HEIGHT / 2 + FONT_SIZE / 3 + random.nextInt(6) - 3;
                
                // 轻微旋转
                double angle = (random.nextDouble() - 0.5) * 0.3;
                g2d.rotate(angle, x, y);
                g2d.drawString(String.valueOf(captchaText.charAt(i)), x, y);
                g2d.rotate(-angle, x, y); // 恢复旋转
            }
            
            // 添加噪点
            for (int i = 0; i < 100; i++) {
                int x = random.nextInt(IMAGE_WIDTH);
                int y = random.nextInt(IMAGE_HEIGHT);
                g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                g2d.fillOval(x, y, 1, 1);
            }
            
            return image;
            
        } finally {
            g2d.dispose();
        }
    }

    /**
     * 将图片转换为Base64编码
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
} 