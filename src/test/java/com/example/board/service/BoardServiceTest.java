package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.mapper.AttachmentMapper;
import com.example.board.mapper.BoardMapper;
import com.example.board.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private FileStore fileStore;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Test
    void deleteBoardDeletesCommentsBeforeBoard() {
        BoardService boardService = new BoardService(
                boardMapper,
                commentMapper,
                fileStore,
                attachmentMapper
        );
        Board board = Board.builder()
                .id(1L)
                .writer("writer")
                .build();

        when(boardMapper.findById(1L)).thenReturn(board);
        when(attachmentMapper.findByBoardId(1L)).thenReturn(List.of());

        boardService.deleteBoard(1L, "writer");

        InOrder deletionOrder = inOrder(commentMapper, boardMapper);
        deletionOrder.verify(commentMapper).deleteByBoardId(1L);
        deletionOrder.verify(boardMapper).deleteById(1L);
    }
}
