package com.example.video_interface.controller.common;

import com.example.video_interface.dto.common.ContactInfoDTO;
import com.example.video_interface.service.common.IContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 联系方式控制器
 * 提供公开的联系方式获取接口，无需登录
 */
@RestController
@RequestMapping("/common/contact")
@RequiredArgsConstructor
public class ContactController {
    
    private final IContactService contactService;

    /**
     * 获取联系方式信息（公开接口，无需登录）
     */
    @GetMapping("/info")
    public ContactInfoDTO getContactInfo() {
        return contactService.getContactInfo();
    }
} 