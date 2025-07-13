package com.example.video_interface.repository;

import com.example.video_interface.model.ActivationCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 激活码数据访问层
 */
@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Long> {
    
    /**
     * 根据激活码查询
     */
    Optional<ActivationCode> findByCode(String code);
    
    /**
     * 根据激活码类型查询
     */
    List<ActivationCode> findByCodeType(ActivationCode.CodeType codeType);
    
    /**
     * 根据激活码状态查询
     */
    List<ActivationCode> findByCodeStatus(ActivationCode.CodeStatus codeStatus);
    
    /**
     * 根据批次号查询
     */
    List<ActivationCode> findByBatchNumber(String batchNumber);
    
    /**
     * 根据使用用户ID查询
     */
    List<ActivationCode> findByUsedBy(Long usedBy);
    
    /**
     * 查询过期的激活码
     */
    @Query("SELECT ac FROM ActivationCode ac WHERE ac.expireAt < :now AND ac.codeStatus = 'UNUSED'")
    List<ActivationCode> findExpiredCodes(@Param("now") LocalDateTime now);
    
    /**
     * 根据条件分页查询
     */
    @Query("SELECT ac FROM ActivationCode ac " +
           "WHERE (:codeType IS NULL OR ac.codeType = :codeType) " +
           "AND (:codeStatus IS NULL OR ac.codeStatus = :codeStatus) " +
           "AND (:batchNumber IS NULL OR ac.batchNumber = :batchNumber) " +
           "AND (:keyword IS NULL OR ac.code LIKE %:keyword% OR ac.remark LIKE %:keyword%) " +
           "ORDER BY ac.createdAt DESC")
    Page<ActivationCode> findByConditions(@Param("codeType") ActivationCode.CodeType codeType,
                                         @Param("codeStatus") ActivationCode.CodeStatus codeStatus,
                                         @Param("batchNumber") String batchNumber,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);
    
    /**
     * 统计各状态激活码数量
     */
    @Query("SELECT ac.codeStatus, COUNT(ac) FROM ActivationCode ac GROUP BY ac.codeStatus")
    List<Object[]> countByStatus();
    
    /**
     * 统计各类型激活码数量
     */
    @Query("SELECT ac.codeType, COUNT(ac) FROM ActivationCode ac GROUP BY ac.codeType")
    List<Object[]> countByType();
    
    /**
     * 检查激活码是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 根据激活码类型和状态统计数量
     */
    long countByCodeTypeAndCodeStatus(ActivationCode.CodeType codeType, ActivationCode.CodeStatus codeStatus);
    
    /**
     * 根据激活码类型统计数量
     */
    long countByCodeType(ActivationCode.CodeType codeType);
} 