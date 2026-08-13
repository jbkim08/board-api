package com.example.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정적 리소스 핸들러를 관리하여 특정 웹 경로로 요청된 이미지나 파일을
 * 실제 서버 로컬 스토리지 상의 리소스로 매핑해주는 웹 설정 클래스입니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.resource-path}")
    private String resourcePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 웹 브라우저에서 /uploads/파일명.확장자 로 접속하면
        // 실제 로컬 디렉토리인 uploadDir 내의 파일로 맵핑시킵니다.
        // Spring 리소스 로케이션 설정 상 로컬 디렉토리는 'file:' 접두사가 필요합니다.
        registry.addResourceHandler(resourcePath)
                .addResourceLocations("file:" + uploadDir);
    }
}

