package com.example.video_interface.repository;

import com.example.video_interface.model.RechargePackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 充值套餐数据访问层
 * 提供充值套餐的数据库操作接口
 */
@Repository
public interface RechargePackageRepository extends JpaRepository<RechargePackage, Long> {
    
    /**
     * 根据状态查询充值套餐列表
     * @param status 套餐状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<RechargePackage> findByStatus(RechargePackage.PackageStatus status, Pageable pageable);
    
    /**
     * 根据名称模糊查询充值套餐
     * @param name 套餐名称
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<RechargePackage> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * 根据状态和名称模糊查询充值套餐
     * @param status 套餐状态
     * @param name 套餐名称
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<RechargePackage> findByStatusAndNameContainingIgnoreCase(
            RechargePackage.PackageStatus status, 
            String name, 
            Pageable pageable);
    
    /**
     * 根据充值金额查询充值套餐
     * @param rechargeAmount 充值金额
     * @return 套餐列表
     */
    List<RechargePackage> findByRechargeAmount(java.math.BigDecimal rechargeAmount);
    
    /**
     * 查询所有上架的充值套餐（按充值金额升序）
     * @return 套餐列表
     */
    @Query("SELECT rp FROM RechargePackage rp WHERE rp.status = 'ACTIVE' ORDER BY rp.rechargeAmount ASC")
    List<RechargePackage> findAllActiveOrderByAmount();
    
    /**
     * 根据名称模糊查询充值套餐（排除指定状态）
     * @param name 套餐名称
     * @param status 要排除的状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<RechargePackage> findByNameContainingIgnoreCaseAndStatusNot(String name, RechargePackage.PackageStatus status, Pageable pageable);
    
    /**
     * 查询所有非指定状态的充值套餐
     * @param status 要排除的状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<RechargePackage> findByStatusNot(RechargePackage.PackageStatus status, Pageable pageable);
} 