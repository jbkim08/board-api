package com.example.board.dto;

import com.example.board.domain.Attachment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 첨부파일 정보를 클라이언트에게 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
public class AttachmentResponse {

    private Long id;              // 첨부파일 고유 ID
    private String originalName;  // 클라이언트가 업로드한 원본 파일 이름
    private String filePath;      // 브라우저에서 다운로드 또는 출력할 수 있는 웹 경로
    private Long fileSize;        // 파일 용량 (Byte)
    private LocalDateTime createdAt; // 업로드 시간

    public static AttachmentResponse from(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getOriginalName())
                .filePath(attachment.getFilePath())
                .fileSize(attachment.getFileSize())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}