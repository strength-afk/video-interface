package com.example.video_interface.service.common.impl;

import com.example.video_interface.service.common.ICaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 使用Java Graphics2D生成图形验证码，Redis存储验证码会话
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements ICaptchaService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int EXPIRE_MINUTES = 5;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成验证码
     * @param sessionId 可选的会话ID，如果为null则创建新会话
     * @return 包含验证码图片Base64和会话ID的Map
     */
    @Override
    public Map<String, String> generateCaptcha(String sessionId) {
        // 生成会话ID
        String newSessionId = sessionId != null ? sessionId : UUID.randomUUID().toString();
        
        // 生成验证码
        String code = generateCode();
        String imageBase64 = generateImage(code);
        
        // 存储验证码
        String key = CAPTCHA_PREFIX + newSessionId;
        redisTemplate.opsForValue().set(key, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("生成验证码 - 会话ID: {}", newSessionId);
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("sessionId", newSessionId);
        result.put("captcha", imageBase64);
        return result;
    }

    /**
     * 刷新验证码
     * @param sessionId 会话ID
     * @return 包含新验证码图片Base64和会话ID的Map
     */
    @Override
    public Map<String, String> refreshCaptcha(String sessionId) {
        return generateCaptcha(sessionId);
    }

    /**
     * 验证验证码
     * @param sessionId 会话ID
     * @param captcha 用户输入的验证码
     * @return 验证是否通过
     */
    @Override
    public boolean verifyCaptcha(String sessionId, String captcha) {
        if (sessionId == null || captcha == null) {
            return false;
        }
        
        String key = CAPTCHA_PREFIX + sessionId;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            log.warn("验证码已过期或不存在 - 会话ID: {}", sessionId);
            return false;
        }
        
        boolean isValid = storedCode.equalsIgnoreCase(captcha.trim());
        
        // 验证后删除验证码
        redisTemplate.delete(key);
        
        if (isValid) {
            log.debug("验证码验证通过 - 会话ID: {}", sessionId);
        } else {
            log.warn("验证码验证失败 - 会话ID: {}", sessionId);
        }
        
        return isValid;
    }

    /**
     * 生成随机验证码
     * @return 验证码字符串
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     * @param code 验证码字符串
     * @return Base64编码的图片数据
     */
    private String generateImage(String code) {
        // 创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        try {
            // 设置背景色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            
            // 绘制干扰线
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 8; i++) {
                int x1 = random.nextInt(WIDTH);
                int y1 = random.nextInt(HEIGHT);
                int x2 = random.nextInt(WIDTH);
                int y2 = random.nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }
            
            // 添加噪点
            for (int i = 0; i < 50; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                image.setRGB(x, y, Color.GRAY.getRGB());
            }
            
            // 设置字体
            g.setFont(new Font("Arial", Font.BOLD, 28));
            
            // 绘制字符
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(random.nextInt(100), 
                                   random.nextInt(100), 
                                   random.nextInt(100)));
                g.drawString(String.valueOf(code.charAt(i)), 
                           20 + i * 25, 
                           30 + random.nextInt(6));
            }
            
            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            
        } catch (Exception e) {
            log.error("生成验证码图片失败: {}", e.getMessage());
            throw new RuntimeException("生成验证码图片失败");
        } finally {
            g.dispose();
        }
    }
} 