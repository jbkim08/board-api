package com.example.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException(){
        super("해당 리소스에 접근할 권한이 없습니다.");
    }

    public UnauthorizedAccessException(String message){
        super(message);
    }
}