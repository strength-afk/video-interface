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
        "/users/register",
        "/users/login",
        "/users/auth",
        "/users/check-username",
        "/users/check-email",
        "/admin/login",
        "/admin/check-status",
        "/error",
        // H5电影相关公开路径
        "/h5/categories/**",
        "/h5/regions/**",
        // 系统配置公开路径
        "/h5/system-config/info",
        // 具体的电影公开路径，不使用通配符
        "/h5/movies/hot",
        "/h5/movies/new",
        "/h5/movies/high-rated",
        "/h5/movies/super-recommended",
        "/h5/movies/category",
        "/h5/movies/region",
        "/h5/movies/year",
        "/h5/movies/charge-type",
        "/h5/movies/vip",
        "/h5/movies/free",
        "/h5/movies/quality",
        "/h5/movies/search",
        "/h5/movies/filter",
        "/h5/movies/list",
        "/h5/movies/view",
        "/h5/movies/like",
        "/h5/movies/unlike",
        "/h5/movies/favorite",
        "/h5/movies/unfavorite",
        "/h5/movies/permission",
        "/h5/movies/filters",
        "/h5/movies/trial",
        "/h5/movies/*/detail-page",
        "/h5/movies/*/related",
        "/h5/movies/*/play-permission",
        "/h5/movies/*/increment-view",
        "/h5/movies/detail-simple"
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
        // 移除/api前缀进行匹配，因为后端配置了context-path=/api
        final String pathWithoutApi = path.startsWith("/api") ? path.substring(4) : path;
        
        boolean isPublic = publicPaths.stream()
            .anyMatch(pattern -> {
                boolean matches = pathMatcher.match(pattern, pathWithoutApi);
                log.debug("路径匹配检查: {} vs {} = {}", pattern, pathWithoutApi, matches);
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