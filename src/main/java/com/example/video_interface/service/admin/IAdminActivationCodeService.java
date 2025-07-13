package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminActivationCodeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 管理后台激活码服务接口
 */
public interface IAdminActivationCodeService {
    
    /**
     * 分页查询激活码列表
     * @param codeType 激活码类型
     * @param codeStatus 激活码状态
     * @param batchNumber 批次号
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 激活码列表
     */
    Page<AdminActivationCodeDTO> getActivationCodeList(String codeType, String codeStatus, 
                                                      String batchNumber, String keyword, Pageable pageable);
    
    /**
     * 根据ID获取激活码详情
     * @param id 激活码ID
     * @return 激活码详情
     */
    AdminActivationCodeDTO getActivationCodeById(Long id);
    

    
    /**
     * 批量创建激活码
     * @param params 批量创建参数
     * @return 创建结果
     */
    Map<String, Object> batchCreateActivationCodes(Map<String, Object> params);
    
    /**
     * 更新激活码
     * @param activationCodeDTO 激活码信息
     * @return 更新后的激活码
     */
    AdminActivationCodeDTO updateActivationCode(AdminActivationCodeDTO activationCodeDTO);
    
    /**
     * 删除激活码
     * @param id 激活码ID
     * @return 删除结果
     */
    boolean deleteActivationCode(Long id);
    
    /**
     * 批量删除激活码
     * @param ids 激活码ID列表
     * @return 删除结果
     */
    boolean batchDeleteActivationCodes(List<Long> ids);
    
    /**
     * 启用/禁用激活码
     * @param id 激活码ID
     * @return 操作结果
     */
    boolean toggleActivationCodeStatus(Long id);
    
    /**
     * 获取激活码统计信息
     * @return 统计信息
     */
    Map<String, Object> getActivationCodeStatistics();
    
    /**
     * 根据激活码类型获取统计信息
     * @param codeType 激活码类型
     * @return 统计信息
     */
    Map<String, Object> getActivationCodeStatisticsByType(String codeType);
    
    /**
     * 导出激活码
     * @param codeType 激活码类型
     * @param codeStatus 激活码状态
     * @param batchNumber 批次号
     * @return 导出数据
     */
    List<AdminActivationCodeDTO> exportActivationCodes(String codeType, String codeStatus, String batchNumber);
    
    /**
     * 生成激活码
     * @param codeType 激活码类型
     * @param length 激活码长度
     * @return 生成的激活码
     */
    String generateActivationCode(String codeType, int length);
    
    /**
     * 验证激活码格式
     * @param code 激活码
     * @return 是否有效
     */
    boolean validateActivationCode(String code);
} 