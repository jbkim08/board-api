package com.example.board.controller;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.dto.PageResponse;
import com.example.board.security.UserPrincipal;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*") //CORS 허용
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
     * 게시글 등록 (로그인 필요, 이미지/파일 첨부 지원)
     * POST /api/boards
     * Header: Authorization: Bearer {accessToken}
     * Content-Type: multipart/form-data
     *
     * @RequestPart("board")는 JSON 형식의 게시글 정보를 DTO로 받습니다.
     * @RequestPart("files")는 업로드할 파일 목록을 받습니다.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponse> createBoard(
            @RequestPart("board") @Valid BoardCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        BoardResponse response = boardService.createBoard(request, principal.getNickname(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정
     * PUT /api/boards/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        BoardResponse boardResponse = boardService.updateBoard(id, request, principal.getNickname());
        return ResponseEntity.ok(boardResponse);
    }

    /**
     * 게시글 삭제
     * DELETE /api/boards/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        boardService.deleteBoard(id, principal.getNickname()); //db에서 삭제
        return ResponseEntity.noContent().build(); // 204 보낼 내용이 없음
    }

}
