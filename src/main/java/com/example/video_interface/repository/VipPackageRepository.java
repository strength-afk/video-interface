package com.example.video_interface.repository;

import com.example.video_interface.model.VipPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VipPackageRepository extends JpaRepository<VipPackage, Long>, JpaSpecificationExecutor<VipPackage> {
    
    /**
     * 根据套餐名称模糊查询（忽略大小写）
     */
    Page<VipPackage> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * 根据状态查询
     */
    Page<VipPackage> findByStatus(VipPackage.PackageStatus status, Pageable pageable);
    
    /**
     * 根据状态和名称模糊查询（忽略大小写）
     */
    Page<VipPackage> findByStatusAndNameContainingIgnoreCase(VipPackage.PackageStatus status, String name, Pageable pageable);
    
    /**
     * 根据价格查询VIP套餐
     */
    List<VipPackage> findByPrice(java.math.BigDecimal price);
    
    /**
     * 根据名称模糊查询VIP套餐（排除指定状态）
     */
    Page<VipPackage> findByNameContainingIgnoreCaseAndStatusNot(String name, VipPackage.PackageStatus status, Pageable pageable);
    
    /**
     * 查询所有非指定状态的VIP套餐
     */
    Page<VipPackage> findByStatusNot(VipPackage.PackageStatus status, Pageable pageable);
} 