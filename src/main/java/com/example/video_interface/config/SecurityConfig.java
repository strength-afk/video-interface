package com.example.video_interface.config;

import com.example.video_interface.filter.DecryptionFilter;
import com.example.video_interface.security.CustomUserDetailsService;
import com.example.video_interface.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
// CORS配置统一在SecurityConfig中管理
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DecryptionFilter decryptionFilter;
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${cors.max-age}")
    private long maxAge;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("配置安全过滤链");
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // 允许所有OPTIONS请求（CORS预检请求）
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                
                // 允许错误页面访问
                auth.requestMatchers("/error").permitAll();
                
                // 允许用户认证相关的公开端点（注意：context-path=/api，所以这里路径不需要/api前缀）
                auth.requestMatchers(HttpMethod.POST, "/users/register").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/users/login").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/users/auth").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/users/check-username").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/users/check-email").permitAll();

                // 允许h5端口访问
                auth.requestMatchers(HttpMethod.GET, "/h5/categories/*").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/h5/regions/*").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/h5/system-config/info").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/*").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/*").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/*/trial/*").permitAll();
                
                // 添加更具体的电影详情和播放相关路径
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/*/detail-page").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/*/related").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/*/play-permission").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/*/increment-view").permitAll();
                
                // 购买相关接口需要认证
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/purchase").authenticated();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/*/purchased").authenticated();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/user/*/purchased*").authenticated();
                
                // 点赞相关接口需要认证
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/like").authenticated();
                auth.requestMatchers(HttpMethod.POST, "/h5/movies/unlike").authenticated();
                auth.requestMatchers(HttpMethod.GET, "/h5/movies/liked").authenticated();
                
                // 收藏相关接口需要认证
                auth.requestMatchers("/h5/favorites/**").authenticated();
                
                // 订单相关接口需要认证
                auth.requestMatchers("/orders/**").authenticated();
                
                // 允许支付相关接口匿名访问
                auth.requestMatchers("/payment/idr/order/callback").permitAll();
                auth.requestMatchers("/payment/idr/**").authenticated();
                
                
                // 允许验证码相关的公开端点
                auth.requestMatchers(HttpMethod.GET, "/users/captcha").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/users/captcha/refresh").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/users/captcha/verify").permitAll();
                
                // 允许管理员相关的公开端点
                auth.requestMatchers(HttpMethod.POST, "/admin/login").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/admin/init").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/admin/check-status").permitAll();
                
                // 管理员接口需要认证
                auth.requestMatchers("/admin/**").authenticated();
                
                // 其他所有请求需要认证
                auth.anyRequest().authenticated();
            })
            // 🔓 添加解密过滤器（在JWT过滤器之前）
            .addFilterBefore(decryptionFilter, UsernamePasswordAuthenticationFilter.class)
            // 🔑 添加JWT认证过滤器
            .addFilterAfter(jwtAuthenticationFilter, com.example.video_interface.filter.DecryptionFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("配置CORS规则，允许的源：{}", Arrays.toString(allowedOrigins));
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 使用配置文件中的值
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
} 