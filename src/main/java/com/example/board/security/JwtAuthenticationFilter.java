package com.example.board.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청마다 한 번씩 실행되는 필터.
 * Authorization 헤더의 "Bearer {token}" 을 꺼내 검증하고,
 * 유효하면 SecurityContext에 인증 정보를 채워 넣는다.
 * 이후 Controller에서 @AuthenticationPrincipal 등으로 로그인 사용자를 알 수 있게 된다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;     //토큰 검증용
    private final CustomUserDetailsService userDetailsService; //인증용

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException, IOException {

        String token = resolveToken(request); //HTTP 헤더에서 실제 토큰 문자열만 가져옴

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 이 요청을 처리하는 동안에는 "인증된 사용자"로 취급된다
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    //HTTP 헤더에서 토큰 문자열만 가져옴.
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_NAME);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

