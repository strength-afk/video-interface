package com.example.video_interface.security;

import com.example.video_interface.util.CryptoUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    @Autowired
    private CryptoUtil cryptoUtil;

    private Key key;
    private SecretKeySpec aesKey;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing enhanced JWT security with secret length: {}", jwtSecret.length());
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        // 创建AES密钥用于额外加密
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest((jwtSecret + "_aes_key").getBytes(StandardCharsets.UTF_8));
            this.aesKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            log.error("初始化AES密钥失败", e);
            throw new RuntimeException("AES密钥初始化失败", e);
        }
        
        log.debug("Enhanced JWT security initialized successfully");
    }

    /**
     * 生成增强的JWT Token
     * @param authentication 认证信息
     * @return 加密的JWT Token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        // 获取设备指纹
        String deviceFingerprint = getDeviceFingerprint();
        
        log.debug("Generating enhanced JWT token for user: {} with device: {}", 
            userDetails.getUsername(), deviceFingerprint != null ? deviceFingerprint.substring(0, 8) + "..." : "unknown");
        
        // 创建基础JWT
        String baseToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("device", deviceFingerprint)
                .claim("type", "access_token")
                .claim("version", "2.0")
                .signWith(key)
                .compact();
        
        // 使用AES加密Token
        try {
            return encryptToken(baseToken);
        } catch (Exception e) {
            log.error("Token加密失败，返回未加密版本", e);
            return baseToken;
        }
    }

    /**
     * 从加密的JWT中提取用户名
     * @param encryptedToken 加密的Token
     * @return 用户名
     */
    public String getUsernameFromJWT(String encryptedToken) {
        log.debug("Extracting username from enhanced JWT token");
        try {
            String decryptedToken = decryptToken(encryptedToken);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(decryptedToken)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            log.debug("Token解密失败，尝试直接解析", e);
            // 如果解密失败，尝试直接解析（兼容旧Token）
            return getUsernameFromPlainJWT(encryptedToken);
        }
    }

    /**
     * 验证增强的JWT Token
     * @param encryptedToken 加密的Token
     * @return 验证结果
     */
    public boolean validateToken(String encryptedToken) {
        try {
            log.debug("Validating enhanced JWT token");
            
            // 解密Token
            String decryptedToken = decryptToken(encryptedToken);
            
            // 解析Claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(decryptedToken)
                    .getBody();
            
            // 验证设备指纹
            String tokenDevice = claims.get("device", String.class);
            String currentDevice = getDeviceFingerprint();
            
            if (tokenDevice != null && currentDevice != null && !tokenDevice.equals(currentDevice)) {
                log.warn("设备指纹不匹配: token={}, current={}", 
                    tokenDevice.substring(0, 8) + "...", 
                    currentDevice.substring(0, 8) + "...");
                return false;
            }
            
            // 验证Token类型
            String tokenType = claims.get("type", String.class);
            if (!"access_token".equals(tokenType)) {
                log.warn("无效的Token类型: {}", tokenType);
                return false;
            }
            
            log.debug("Enhanced JWT token validation successful");
            return true;
            
        } catch (Exception e) {
            log.debug("Enhanced token validation failed, trying plain token", e);
            // 如果增强验证失败，尝试普通验证（兼容性）
            return validatePlainToken(encryptedToken);
        }
    }

    /**
     * 获取Token过期时间
     */
    public Date getExpirationDateFromToken(String encryptedToken) {
        try {
            String decryptedToken = decryptToken(encryptedToken);
            return extractClaim(decryptedToken, Claims::getExpiration);
        } catch (Exception e) {
            return extractClaim(encryptedToken, Claims::getExpiration);
        }
    }

    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String encryptedToken) {
        Date expiration = getExpirationDateFromToken(encryptedToken);
        return expiration.before(new Date());
    }

    /**
     * 获取用户详情
     */
    public UserDetails getUserDetails(String encryptedToken) {
        log.debug("Loading user details from enhanced JWT token");
        String username = getUsernameFromJWT(encryptedToken);
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * 获取认证信息
     */
    public Authentication getAuthentication(String encryptedToken) {
        log.info("Getting authentication from enhanced JWT token");
        try {
            String username = getUsernameFromJWT(encryptedToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            log.error("Error getting authentication from enhanced JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * AES加密Token
     */
    private String encryptToken(String token) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES解密Token
     */
    private String decryptToken(String encryptedToken) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 获取设备指纹
     */
    private String getDeviceFingerprint() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                return request.getHeader("X-Device-ID");
            }
        } catch (Exception e) {
            log.debug("无法获取设备指纹: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 兼容性方法：直接解析JWT
     */
    private String getUsernameFromPlainJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 兼容性方法：验证普通Token
     */
    private boolean validatePlainToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 