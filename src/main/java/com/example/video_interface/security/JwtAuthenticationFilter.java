package com.example.video_interface.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> publicPaths = Arrays.asList(
        "/api/users/register",
        "/api/users/login",
        "/api/users/auth",
        "/api/users/check-username",
        "/api/users/check-email",
        "/api/users/admin/login",
        "/api/users/admin/init",
        "/api/users/admin/check-status",
        "/api/users/admin/test-login",
        "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("JWT过滤器处理请求: {} {}", method, requestPath);

        // 如果是OPTIONS请求，直接放行
        if (HttpMethod.OPTIONS.matches(method)) {
            log.debug("OPTIONS请求，直接放行: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // 如果是公开路径，直接放行
        if (isPublicPath(requestPath)) {
            log.debug("公开路径，直接放行: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("需要认证的路径: {}", requestPath);

        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT认证成功: {}", username);
            } else {
                log.debug("无效或缺失JWT token");
            }
        } catch (Exception ex) {
            log.error("无法设置用户认证: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        boolean isPublic = publicPaths.stream()
            .anyMatch(pattern -> {
                boolean matches = pathMatcher.match(pattern, path);
                log.debug("路径匹配检查: {} vs {} = {}", pattern, path, matches);
                return matches;
            });
        log.debug("路径 {} 是否为公开路径: {}", path, isPublic);
        return isPublic;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 