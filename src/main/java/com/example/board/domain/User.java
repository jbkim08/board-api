package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * users 테이블과 매핑되는 도메인 클래스.
 * password 필드에는 항상 BCrypt로 암호화된 값만 저장한다 (평문 저장 절대 금지).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password; // 암호화된 비밀번호
    private String nickname;
    private String role;     // "ROLE_USER" 등
    private LocalDateTime createdAt;
}