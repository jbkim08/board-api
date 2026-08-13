package com.example.board.dto;

import com.example.board.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 댓글 정보를 반환하기 위한 응답 DTO
 */
@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long boardId;
    private String content;
    private String writer;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .boardId(comment.getBoardId())
                .content(comment.getContent())
                .writer(comment.getWriter())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
