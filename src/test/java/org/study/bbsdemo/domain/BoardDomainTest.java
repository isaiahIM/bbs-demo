package org.study.bbsdemo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Board 도메인 BDD 테스트")
class BoardDomainTest {

    @Nested
    @DisplayName("게시판 생성 시")
    class CreateBoard {

        @Test
        @DisplayName("Given 유효한 게시판 정보가 주어졌을 때, When 게시판을 생성하면, Then 정상적으로 도메인 객체가 생성된다.")
        void createBoardSuccess() {
            // Given
            String name = "자유게시판";
            String description = "자유로운 의견 남기기";

            // When
            Board board = Board.create(name, description);

            // Then
            assertThat(board.getName()).isEqualTo(name);
            assertThat(board.getDescription()).isEqualTo(description);
        }

        @Test
        @DisplayName("Given 이름이 빈 값인 정보가 주어졌을 때, When 게시판을 생성하면, Then IllegalArgumentException 예외가 발생한다.")
        void createBoardFailureWhenNameIsEmpty() {
            // Given
            String emptyName = "  ";

            // When & Then
            assertThatThrownBy(() -> Board.create(emptyName, "설명"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("게시판 이름은 필수 입력 항목입니다.");
        }
    }

    @Nested
    @DisplayName("게시판 수정 시")
    class UpdateBoard {

        @Test
        @DisplayName("Given 기존 게시판이 있을 때, When 유효한 정보로 수정하면, Then 게시판 정보가 업데이트된다.")
        void updateBoardSuccess() {
            // Given
            Board board = Board.create("기존게시판", "기존설명");

            // When
            board.update("수정게시판", "수정설명");

            // Then
            assertThat(board.getName()).isEqualTo("수정게시판");
            assertThat(board.getDescription()).isEqualTo("수정설명");
        }
    }
}
