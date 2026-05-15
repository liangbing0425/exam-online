package org.college.examonline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源（生产环境建议指定具体域名）
        config.addAllowedOriginPattern("*");
        
        // 允许携带认证信息（cookies、authorization headers等）
        config.setAllowCredentials(true);
        
        // 允许所有HTTP方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        
        // 允许所有请求头
        config.addAllowedHeader("*");
        
        // 暴露响应头，让前端可以访问
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        
        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
