package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.dto.LoginRequest;
import com.example.board.dto.SignUpRequest;
import com.example.board.dto.TokenResponse;
import com.example.board.exception.DuplicateUsernameException;
import com.example.board.mapper.UserMapper;
import com.example.board.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public void signUp(SignUpRequest request) {
        // 아이디 중복 체크
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // 반드시 암호화해서 저장
                .nickname(request.getNickname())
                .role("ROLE_USER")
                .build();

        userMapper.insert(user);
    }

    // 로그인 (성공 시 JWT 토큰 문자열 리턴)
    public TokenResponse login(LoginRequest request) {
        try {
            // AuthenticationManager가 내부적으로 CustomUserDetailsService.loadUserByUsername() 을 호출하고
            // PasswordEncoder로 비밀번호를 대조해준다. 실패하면 예외를 던진다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userMapper.findByUsername(request.getUsername());

            // Access Token & Refresh Token 생성
            String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

            // DB에 Refresh Token 저장
            userMapper.updateRefreshToken(user.getUsername(), refreshToken);

            // 토큰 발행 후 전달
            return TokenResponse.of(accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpirationMs(), user.getUsername(), user.getNickname());

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    /**
     * Refresh Token을 검증하고 새로운 Access/Refresh Token을 발급합니다. (Refresh Token Rotation 적용)
     */
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // 1. Refresh Token 자체 유효성 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 2. DB에서 해당 Refresh Token을 가진 사용자 조회
        User user = userMapper.findByRefreshToken(refreshToken);
        if (user == null) {
            throw new BadCredentialsException("유효하지 않거나 만료된 Refresh Token입니다.");
        }

        // 3. 새로운 Access Token 및 Refresh Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // 4. DB의 Refresh Token 갱신
        userMapper.updateRefreshToken(user.getUsername(), newRefreshToken);

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenExpirationMs(),
                user.getUsername(),
                user.getNickname()
        );
    }

}