package org.study.bbsdemo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Comment 도메인 BDD 테스트")
class CommentDomainTest {

    private Post createPost() {
        Board board = Board.create("자유게시판", "설명");
        return Post.create(board, "제목", "내용", "게시글작성자");
    }

    @Nested
    @DisplayName("댓글 생성 시")
    class CreateComment {

        @Test
        @DisplayName("Given 유효한 게시글과 댓글 정보가 주어졌을 때, When 댓글을 생성하면, Then 정상적으로 도메인 객체가 생성되고 게시글에 추가된다.")
        void createCommentSuccess() {
            // Given
            Post post = createPost();
            String content = "좋은 글이네요!";
            String writer = "댓글작성자";

            // When
            Comment comment = Comment.create(post, content, writer);

            // Then
            assertThat(comment.getContent()).isEqualTo(content);
            assertThat(comment.getWriter()).isEqualTo(writer);
            assertThat(comment.getPost()).isEqualTo(post);
            assertThat(post.getComments()).contains(comment);
        }

        @Test
        @DisplayName("Given 게시글이 null일 때, When 댓글 생성을 시도하면, Then IllegalArgumentException 예외가 발생한다.")
        void createCommentFailureWhenPostIsNull() {
            // When & Then
            assertThatThrownBy(() -> Comment.create(null, "내용", "작성자"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("댓글은 소속 게시글이 존재해야 합니다.");
        }

        @Test
        @DisplayName("Given 내용이 빈 값일 때, When 댓글 생성을 시도하면, Then IllegalArgumentException 예외가 발생한다.")
        void createCommentFailureWhenContentIsEmpty() {
            // Given
            Post post = createPost();

            // When & Then
            assertThatThrownBy(() -> Comment.create(post, "  ", "작성자"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("댓글 내용은 필수 입력 항목입니다.");
        }

        @Test
        @DisplayName("Given 작성자가 빈 값일 때, When 댓글 생성을 시도하면, Then IllegalArgumentException 예외가 발생한다.")
        void createCommentFailureWhenWriterIsEmpty() {
            // Given
            Post post = createPost();

            // When & Then
            assertThatThrownBy(() -> Comment.create(post, "내용", "  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("작성자는 필수 입력 항목입니다.");
        }
    }

    @Nested
    @DisplayName("댓글 수정 시")
    class UpdateComment {

        @Test
        @DisplayName("Given 동일한 작성자의 수정 요청이 올 때, When 댓글을 수정하면, Then 성공적으로 변경된다.")
        void updateCommentSuccess() {
            // Given
            Post post = createPost();
            Comment comment = Comment.create(post, "원본 댓글", "댓글작성자");

            // When
            comment.update("수정된 댓글 내용", "댓글작성자");

            // Then
            assertThat(comment.getContent()).isEqualTo("수정된 댓글 내용");
        }

        @Test
        @DisplayName("Given 타인 작성자의 수정 요청이 올 때, When 댓글 수정을 시도하면, Then 권한 예외가 발생한다.")
        void updateCommentUnauthorizedFailure() {
            // Given
            Post post = createPost();
            Comment comment = Comment.create(post, "원본 댓글", "원댓글작성자");

            // When & Then
            assertThatThrownBy(() -> comment.update("수정 시도", "타인"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");
        }

        @Test
        @DisplayName("Given 수정 내용이 빈 값일 때, When 댓글 수정을 시도하면, Then IllegalArgumentException 예외가 발생한다.")
        void updateCommentFailureWhenContentIsEmpty() {
            // Given
            Post post = createPost();
            Comment comment = Comment.create(post, "원본 댓글", "댓글작성자");

            // When & Then
            assertThatThrownBy(() -> comment.update("", "댓글작성자"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("댓글 내용은 필수 입력 항목입니다.");
        }
    }
}
