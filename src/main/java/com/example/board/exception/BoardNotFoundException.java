package com.example.board.exception;

//커스텀 예외 발생 클래스
public class BoardNotFoundException extends RuntimeException{

    public BoardNotFoundException(Long id){
        super("게시글을 찾을 수 없습니다. id=" + id); //에러 메세지
    }
}
