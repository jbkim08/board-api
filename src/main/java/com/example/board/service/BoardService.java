package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.dto.PageResponse;
import com.example.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //기본 읽기 전용, 쓰기 메서드는 개별로 재정의
public class BoardService {

    private final BoardMapper boardMapper;

    /**
     * 목록 조회 (검색 + 페이징)
     * @param keyword 검색어 (null이면 전체 조회)
     * @param page 1부터 시작하는 페이지 번호
     * @param size 페이지당 개수
     */
    public PageResponse<BoardResponse> getBoards(String keyword, int page, int size) {
        int offset = (page - 1) * size; // 현재 페이지에 맞는 offset 계산
        //게시물을 현재 페이지 맞게 size만큼 가져옴
        List<Board> boards = boardMapper.findAll(keyword, offset, size);
        long totalElements = boardMapper.countAll(keyword); //검색된 모든 게시물수

        List<BoardResponse> content = boards.stream()
                .map(board -> BoardResponse.from(board))
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
            //throw new BoardNotFoundException(id);
        }
        boardMapper.increaseViewCount(id);
        board.setViewCount(board.getViewCount() + 1); // 응답에도 반영
        return BoardResponse.from(board);
    }

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(request.getWriter())
                .build();

        boardMapper.insert(board); // insert 후 board.getId()에 PK가 채워짐 (useGeneratedKeys)
        return BoardResponse.from(board);
    }

    @Transactional
    public BoardResponse updateBoard(Long id, BoardUpdateRequest request) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            //throw new BoardNotFoundException(id);
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        boardMapper.update(board);

        return BoardResponse.from(boardMapper.findById(id));
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            //throw new BoardNotFoundException(id);
        }
        boardMapper.deleteById(id);
    }
}
