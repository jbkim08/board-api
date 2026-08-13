package com.example.board.mapper;

import com.example.board.domain.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * attachment 테이블에 액세스하기 위한 MyBatis 매퍼 인터페이스입니다.
 */
@Mapper
public interface AttachmentMapper {

    /**
     * 첨부파일 메타데이터를 데이터베이스에 등록합니다.
     *
     * @param attachment 등록할 첨부파일 정보 도메인 객체
     * @return 영향을 받은 행의 수
     */
    int insert(Attachment attachment);

    /**
     * 특정 게시글(board_id)에 연결된 모든 첨부파일 목록을 조회합니다.
     *
     * @param boardId 게시글 ID
     * @return 첨부파일 객체 리스트
     */
    List<Attachment> findByBoardId(@Param("boardId") Long boardId);

    /**
     * 특정 첨부파일 단건 정보를 조회합니다.
     *
     * @param id 첨부파일 고유 ID
     * @return 첨부파일 객체
     */
    Attachment findById(@Param("id") Long id);

    /**
     * 특정 첨부파일 정보를 데이터베이스에서 삭제합니다.
     *
     * @param id 삭제할 첨부파일 고유 ID
     */
    void deleteById(@Param("id") Long id);
}
