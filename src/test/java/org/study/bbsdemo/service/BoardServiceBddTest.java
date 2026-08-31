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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("Given 게시판 목록이 존재할 때, When getAllBoards를 호출하면, Then 전체 게시판 응답 목록을 반환한다.")
    void getAllBoardsBdd() {
        // Given
        Board board1 = Board.create("공지사항", "공지사항 게시판");
        ReflectionTestUtils.setField(board1, "id", 1L);
        Board board2 = Board.create("자유게시판", "자유게시판입니다");
        ReflectionTestUtils.setField(board2, "id", 2L);

        given(boardRepository.findAll()).willReturn(List.of(board1, board2));

        // When
        List<BoardDto.Response> responses = boardService.getAllBoards();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("공지사항");
        assertThat(responses.get(1).name()).isEqualTo("자유게시판");
        then(boardRepository).should().findAll();
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

    @Test
    @DisplayName("Given 수정 정보가 주어졌을 때, When updateBoard를 호출하면, Then 게시판 정보가 수정되고 반환된다.")
    void updateBoardBdd() {
        // Given
        Long boardId = 1L;
        Board board = Board.create("기존게시판", "기존설명");
        ReflectionTestUtils.setField(board, "id", boardId);

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        BoardDto.Request updateRequest = new BoardDto.Request("수정게시판", "수정설명");

        // When
        BoardDto.Response response = boardService.updateBoard(boardId, updateRequest);

        // Then
        assertThat(response.name()).isEqualTo("수정게시판");
        assertThat(response.description()).isEqualTo("수정설명");
        then(boardRepository).should().findById(boardId);
    }

    @Test
    @DisplayName("Given 게시판 ID가 주어졌을 때, When deleteBoard를 호출하면, Then 리포지토리에서 삭제된다.")
    void deleteBoardBdd() {
        // Given
        Long boardId = 1L;
        Board board = Board.create("삭제할게시판", "설명");
        ReflectionTestUtils.setField(board, "id", boardId);

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // When
        boardService.deleteBoard(boardId);

        // Then
        then(boardRepository).should().findById(boardId);
        then(boardRepository).should().delete(board);
    }

    @Test
    @DisplayName("Given 존재하지 않는 게시판 ID가 주어졌을 때, When 조회를 시도하면, Then IllegalArgumentException 예외가 발생한다.")
    void findBoardByIdNotFoundThrowsException() {
        // Given
        Long nonExistentId = 999L;
        given(boardRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> boardService.findBoardById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시판입니다. id=" + nonExistentId);
        then(boardRepository).should().findById(nonExistentId);
    }
}
