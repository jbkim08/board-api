package com.example.board.controller;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.dto.PageResponse;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회
     * GET /api/boards?keyword=검색어&page=1&size=10
     */
    @GetMapping
    public ResponseEntity<PageResponse<BoardResponse>> getBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BoardResponse> pageResponse = boardService.getBoards(keyword, page, size);
        return ResponseEntity.ok(pageResponse); //서비스에서 가져온 페이지객체를 리턴
    }

    /**
     * 게시글 단건 조회
     * GET /api/boards/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long id) {
        BoardResponse boardResponse = boardService.getBoard(id);
        return ResponseEntity.ok(boardResponse);
    }

    /**
     * 게시글 등록
     * POST /api/boards
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정
     * PUT /api/boards/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request
    ) {
        BoardResponse boardResponse = boardService.updateBoard(id, request);
        return ResponseEntity.ok(boardResponse);
    }

    /**
     * 게시글 삭제
     * DELETE /api/boards/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id); //db에서 삭제
        return ResponseEntity.noContent().build(); // 204 보낼 내용이 없음
    }

}
