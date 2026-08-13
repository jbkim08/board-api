package com.example.board.controller;

import com.example.board.dto.CommentCreateRequest;
import com.example.board.dto.CommentResponse;
import com.example.board.security.UserPrincipal;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 관련 HTTP 요청을 처리하여 응답을 반환하는 컨트롤러 클래스입니다.
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 특정 게시글에 댓글을 등록합니다.
     * POST /api/boards/{boardId}/comments
     * (인증 필수: 헤더에 Authorization: Bearer {accessToken} 필요)
     *
     * @param boardId   댓글을 작성할 게시글의 ID
     * @param request   댓글 내용이 담긴 DTO
     * @param principal Spring Security를 통해 주입받은 인증된 사용자 정보
     * @return 생성된 댓글 정보와 201 Created 응답
     */
    @PostMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        // 인증 객체로부터 사용자의 닉네임을 가져와 작성자로 지정
        CommentResponse response = commentService.createComment(boardId, request, principal.getNickname());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 게시글에 작성된 댓글 목록을 전체 조회합니다.
     * GET /api/boards/{boardId}/comments
     * (인증 불필요: 누구나 조회 가능)
     *
     * @param boardId 조회할 게시글의 ID
     * @return 댓글 목록과 200 OK 응답
     */
    @GetMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long boardId) {
        List<CommentResponse> response = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(response);
    }

    /**
     * 작성된 특정 댓글을 삭제합니다.
     * DELETE /api/comments/{id}
     * (인증 필수: 본인이 작성한 댓글만 삭제 가능)
     *
     * @param id        삭제할 댓글의 ID
     * @param principal Spring Security를 통해 주입받은 인증된 사용자 정보
     * @return 204 No Content 응답
     */
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        // 서비스 단에서 현재 로그인 사용자 닉네임과 댓글 작성자의 일치 여부를 검증한 후 삭제
        commentService.deleteComment(id, principal.getNickname());
        return ResponseEntity.noContent().build();
    }
}
