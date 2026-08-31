package org.study.bbsdemo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Post 도메인 BDD 테스트")
class PostDomainTest {

    @Nested
    @DisplayName("게시글 생성 및 수정 시")
    class PostOperations {

        @Test
        @DisplayName("Given 게시판과 유효한 게시글 정보가 주어졌을 때, When 게시글을 생성하면, Then 정상적으로 게시글 도메인이 생성된다.")
        void createPostSuccess() {
            // Given
            Board board = Board.create("질문게시판", "질문하기");
            String title = "질문있습니다";
            String content = "Spring JPA BDD 테스트 방법";
            String writer = "홍길동";

            // When
            Post post = Post.create(board, title, content, writer);

            // Then
            assertThat(post.getTitle()).isEqualTo(title);
            assertThat(post.getContent()).isEqualTo(content);
            assertThat(post.getWriter()).isEqualTo(writer);
            assertThat(post.getBoard()).isEqualTo(board);
        }

        @Test
        @DisplayName("Given 타인 작성자의 수정 요청이 올 때, When 게시글 수정을 시도하면, Then 권한 예외가 발생한다.")
        void updatePostUnauthorizedFailure() {
            // Given
            Board board = Board.create("자유게시판", "설명");
            Post post = Post.create(board, "원본 제목", "원본 내용", "원작성자");

            // When & Then
            assertThatThrownBy(() -> post.update("수정 제목", "수정 내용", "타인"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");
        }

        @Test
        @DisplayName("Given 원작성자의 수정 요청이 올 때, When 게시글을 수정하면, Then 성공적으로 변경된다.")
        void updatePostSuccess() {
            // Given
            Board board = Board.create("자유게시판", "설명");
            Post post = Post.create(board, "원본 제목", "원본 내용", "원작성자");

            // When
            post.update("수정 제목", "수정 내용", "원작성자");

            // Then
            assertThat(post.getTitle()).isEqualTo("수정 제목");
            assertThat(post.getContent()).isEqualTo("수정 내용");
        }
    }
}
