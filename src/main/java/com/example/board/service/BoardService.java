package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.*;
import com.example.board.exception.BoardNotFoundException;
import com.example.board.exception.UnauthorizedAccessException;
import com.example.board.mapper.BoardMapper;
import com.example.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //기본 읽기 전용, 쓰기 메서드는 개별로 재정의
public class BoardService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;

    /**
     * 목록 조회 (검색 + 페이징)
     * @param keyword 검색어 (null이면 전체 조회)
     * @param page 1부터 시작하는 페이지 번호
     * @param size 페이지당 개수
     */
    public PageResponse<BoardResponse> getBoards(String keyword, int page, int size) {
        int offset = (page - 1) * size;

        List<Board> boards = boardMapper.findAll(keyword, offset, size);
        long totalElements = boardMapper.countAll(keyword);

        List<BoardResponse> content = boards.stream()
                .map(BoardResponse::from)
                .toList();

        return PageResponse.of(content, page, size, totalElements);
    }

    /**
     * 단건 조회 (조회수 +1 증가 포함)
     */
    @Transactional
    public BoardResponse getBoard(Long id) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new BoardNotFoundException(id); //예외 발생
        }
        boardMapper.increaseViewCount(id);
        board.setViewCount(board.getViewCount() + 1); // 응답에도 반영
        // 게시글에 작성된 댓글 목록 조회
        List<CommentResponse> commentResponses = commentMapper.findByBoardId(id).stream()
                .map(CommentResponse::from)
                .toList();

        return BoardResponse.from(board, commentResponses);
    }

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request, String writer) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(writer)
                .build(); //롬북 빌더로 객체 만들며 입력하기

        boardMapper.insert(board); // insert 후 board.getId()에 PK가 채워짐 (useGeneratedKeys)
        return BoardResponse.from(board);
    }

    @Transactional
    public BoardResponse updateBoard(Long id, BoardUpdateRequest request,
                                     String currentNickname) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new BoardNotFoundException(id);
        }
        if (!board.getWriter().equals(currentNickname)) {
            throw new UnauthorizedAccessException("게시글을 수정할 권한이 없습니다.");
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        boardMapper.update(board);

        return BoardResponse.from(boardMapper.findById(id));
    }

    @Transactional
    public void deleteBoard(Long id, String currentNickname) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new BoardNotFoundException(id);
        }
        if (!board.getWriter().equals(currentNickname)) {
            throw new UnauthorizedAccessException("게시글을 삭제할 권한이 없습니다.");
        }
        boardMapper.deleteById(id);
    }
}
