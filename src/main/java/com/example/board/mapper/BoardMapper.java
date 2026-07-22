package com.example.board.mapper;

import com.example.board.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    // 검색 + 페이징 목록 조회
    List<Board> findAll(@Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("size") int size);

    // 검색 조건에 맞는 전체 개수 (페이징 계산용)
    long countAll(@Param("keyword") String keyword);

    // 단건 조회
    Board findById(@Param("id") Long id);

    // 등록 (insert 후 board.id 에 auto-increment 된 PK가 자동으로 채워짐)
    int insert(Board board);

    // 수정
    int update(Board board);

    // 삭제
    int deleteById(@Param("id") Long id);

    // 조회수 증가
    int increaseViewCount(@Param("id") Long id);
}
