package com.example.board.mapper;

import com.example.board.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * comment 테이블과 SQL을 바인딩하기 위한 MyBatis Mapper 인터페이스
 */
@Mapper
public interface CommentMapper {

    void insert(Comment comment);

    Comment findById(@Param("id") Long id);

    List<Comment> findByBoardId(@Param("boardId") Long boardId);

    void deleteById(@Param("id") Long id);
}
