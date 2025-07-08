package com.example.video_interface.repository;

import com.example.video_interface.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    /**
     * 查找管理员角色的用户
     * @param role 用户角色
     * @return 管理员用户数量
     */
    long countByRole(User.UserRole role);
    
    /**
     * 检查是否存在管理员角色的用户
     * @param role 用户角色
     * @return true如果存在管理员，否则返回false
     */
    boolean existsByRole(User.UserRole role);
    
    /**
     * 根据用户名和角色查找用户
     * @param username 用户名
     * @param role 用户角色
     * @return 用户信息，如果不存在则返回空
     */
    Optional<User> findByUsernameAndRole(String username, User.UserRole role);
} 