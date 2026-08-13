package com.example.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 존재하지 않는 댓글을 조회/삭제하려 할 때 발생하는 예외 (404 Not Found)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Long id) {
        super("해당 댓글을 찾을 수 없습니다. (댓글 ID: " + id + ")");
    }
}