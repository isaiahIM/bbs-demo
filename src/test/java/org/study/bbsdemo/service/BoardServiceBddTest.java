package org.study.bbsdemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.dto.BoardDto;
import org.study.bbsdemo.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoardService BDD 단위 테스트")
class BoardServiceBddTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("Given 게시판 생성 요청이 주어졌을 때, When createBoard를 호출하면, Then 게시판이 정상적으로 저장되고 응답 DTO를 반환한다.")
    void createBoardBdd() {
        // Given
        BoardDto.Request request = new BoardDto.Request("Q&A", "질문과 답변 게시판");
        Board savedBoard = Board.create("Q&A", "질문과 답변 게시판");
        ReflectionTestUtils.setField(savedBoard, "id", 1L);
        ReflectionTestUtils.setField(savedBoard, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(savedBoard, "updatedAt", LocalDateTime.now());

        given(boardRepository.save(any(Board.class))).willReturn(savedBoard);

        // When
        BoardDto.Response response = boardService.createBoard(request);

        // Then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Q&A");
        assertThat(response.description()).isEqualTo("질문과 답변 게시판");
        assertThat(response.createdAt()).isNotNull();
        then(boardRepository).should().save(any(Board.class));
    }

    @Test
    @DisplayName("Given 게시판 ID가 존재할 때, When getBoard를 호출하면, Then 저장된 게시판 정보를 반환한다.")
    void getBoardBdd() {
        // Given
        Long boardId = 1L;
        Board board = Board.create("공지사항", "공지사항 게시판");
        ReflectionTestUtils.setField(board, "id", boardId);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.now());

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // When
        BoardDto.Response response = boardService.getBoard(boardId);

        // Then
        assertThat(response.name()).isEqualTo("공지사항");
        then(boardRepository).should().findById(boardId);
    }
}
