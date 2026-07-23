package com.example.board.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse{
    private LocalDateTime timestamp; // 에러 발생 시각
    private int status;               // HTTP 상태 코드 (예: 400, 404, 500)
    private String error;            // 에러 유형 이름
    private String message;          // 클라이언트에게 보여줄 에러 메시지
}