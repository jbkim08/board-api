package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * attachment 테이블과 매핑되는 도메인 클래스입니다.
 * 게시글에 포함된 개별 첨부파일 정보를 담습니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    private Long id;              // 첨부파일 고유 ID
    private Long boardId;         // 부모 게시글 ID
    private String originalName;  // 사용자가 원래 올렸던 실제 파일 이름 (예: image.png)
    private String storedName;    // 중복을 피하기 위해 서버에 고유하게 저장된 파일 이름 (예: UUID.png)
    private String filePath;      // 웹을 통해 파일에 접근할 수 있는 URL/URI 경로
    private Long fileSize;        // 파일의 크기 (Byte 단위)
    private LocalDateTime createdAt; // 업로드 시각
}
