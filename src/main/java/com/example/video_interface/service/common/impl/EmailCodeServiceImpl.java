package com.example.video_interface.service.common.impl;

import com.example.video_interface.service.common.IEmailCodeService;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements IEmailCodeService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final EmailUtil emailUtil;

    @Override
    public void sendEmailCode(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("该邮箱已被绑定");
        }
        String redisKey = "email:code:send:" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            throw new IllegalArgumentException("请勿频繁获取验证码");
        }
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        // 有效期30分钟
        stringRedisTemplate.opsForValue().set("email:code:" + email, code, 30, TimeUnit.MINUTES);
        // 设置1分钟限流标记，防止同一邮箱频繁请求验证码（1分钟内只能请求一次）
        stringRedisTemplate.opsForValue().set(redisKey, "1", 60, TimeUnit.SECONDS);
        emailUtil.sendMail(email, "邮箱绑定验证码", "你的邮箱绑定验证码为：" + code + "，30分钟内有效。请勿泄露给他人。");
    }
} 