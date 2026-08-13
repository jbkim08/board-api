package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.dto.CommentCreateRequest;
import com.example.board.dto.CommentResponse;
import com.example.board.exception.BoardNotFoundException;
import com.example.board.exception.CommentNotFoundException;
import com.example.board.exception.UnauthorizedAccessException;
import com.example.board.mapper.BoardMapper;
import com.example.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentMapper commentMapper;
    private final BoardMapper boardMapper;

    /**
     * 댓글 등록 (로그인 필요)
     */
    @Transactional
    public CommentResponse createComment(Long boardId, CommentCreateRequest request, String writer) {
        // 1. 게시글 존재 여부 검증
        if (boardMapper.findById(boardId) == null) {
            throw new BoardNotFoundException(boardId);
        }

        // 2. 댓글 생성 및 저장
        Comment comment = Comment.builder()
                .boardId(boardId)
                .content(request.getContent())
                .writer(writer)
                .build();

        commentMapper.insert(comment); // useGeneratedKeys로 인해 comment.id가 채워짐
        return CommentResponse.from(comment);
    }

    /**
     * 특정 게시글에 달린 댓글 목록 조회
     */
    public List<CommentResponse> getCommentsByBoardId(Long boardId) {
        return commentMapper.findByBoardId(boardId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    /**
     * 댓글 삭제 (로그인 필요, 본인 댓글만 삭제 가능)
     */
    @Transactional
    public void deleteComment(Long id, String currentNickname) {
        // 1. 댓글 존재 검증
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        // 2. 본인 댓글 여부 검증
        if (!comment.getWriter().equals(currentNickname)) {
            throw new UnauthorizedAccessException("댓글을 삭제할 권한이 없습니다.");
        }

        // 3. 삭제 처리
        commentMapper.deleteById(id);
    }
}
