package org.study.bbsdemo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.study.bbsdemo.dto.BoardDto;
import org.study.bbsdemo.dto.CommentDto;
import org.study.bbsdemo.dto.PostDto;
import org.study.bbsdemo.service.BoardService;
import org.study.bbsdemo.service.CommentService;
import org.study.bbsdemo.service.PostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("게시판 - 게시글 - 댓글 전체 BDD 통합 시나리오 테스트")
class BbsBddIntegrationTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Nested
    @DisplayName("시나리오: 게시판 관리 및 게시글/댓글 생애주기 테스트")
    class FullBbsLifecycleScenario {

        @Test
        @DisplayName("Given 게시판이 생성되었을 때, When 게시글과 댓글을 작성하고 수정/삭제를 진행하면, Then 각 BDD 검증 조건이 성공한다.")
        void bbsLifecycleScenario() {
            // -------------------------------------------------------------
            // Step 1: 게시판 생성
            // -------------------------------------------------------------
            // Given: 새로운 게시판 생성 요청 정보
            BoardDto.Request boardRequest = new BoardDto.Request("자유게시판", "자유롭게 소통하는 게시판입니다.");

            // When: 게시판 생성 서비스 호출
            BoardDto.Response createdBoard = boardService.createBoard(boardRequest);

            // Then: 게시판이 정상적으로 생성됨
            assertThat(createdBoard.id()).isNotNull();
            assertThat(createdBoard.name()).isEqualTo("자유게시판");
            assertThat(createdBoard.createdAt()).isNotNull();
            assertThat(createdBoard.updatedAt()).isNotNull();

            // -------------------------------------------------------------
            // Step 2: 게시글 작성
            // -------------------------------------------------------------
            // Given: 생성된 게시판 ID와 게시글 정보
            Long boardId = createdBoard.id();
            PostDto.CreateRequest postRequest = new PostDto.CreateRequest(
                    "BDD 테스트 작성",
                    "Behavior Driven Development 로직 검증",
                    "테스터"
            );

            // When: 게시글 생성 서비스 호출
            PostDto.DetailResponse createdPost = postService.createPost(boardId, postRequest);

            // Then: 게시글이 정상적으로 저장됨
            assertThat(createdPost.id()).isNotNull();
            assertThat(createdPost.title()).isEqualTo("BDD 테스트 작성");
            assertThat(createdPost.writer()).isEqualTo("테스터");

            // -------------------------------------------------------------
            // Step 3: 댓글 작성
            // -------------------------------------------------------------
            // Given: 작성된 게시글 ID와 댓글 정보
            Long postId = createdPost.id();
            CommentDto.CreateRequest commentRequest = new CommentDto.CreateRequest(
                    "첫 번째 BDD 댓글입니다.",
                    "댓글작성자"
            );

            // When: 댓글 생성 서비스 호출
            CommentDto.Response createdComment = commentService.createComment(postId, commentRequest);

            // Then: 댓글이 정상 작성되고, 게시글 상세 조회 시 댓글 목록에 포함됨
            assertThat(createdComment.id()).isNotNull();
            assertThat(createdComment.content()).isEqualTo("첫 번째 BDD 댓글입니다.");

            PostDto.DetailResponse postDetailWithComments = postService.getPostDetail(postId);
            assertThat(postDetailWithComments.comments()).hasSize(1);
            assertThat(postDetailWithComments.comments().get(0).content()).isEqualTo("첫 번째 BDD 댓글입니다.");

            // -------------------------------------------------------------
            // Step 4: 권한 검증 및 수정 시도 (Rich Domain 검증)
            // -------------------------------------------------------------
            // Given: 타인 작성자의 수정 요청 정보
            PostDto.UpdateRequest unauthorizedUpdateRequest = new PostDto.UpdateRequest(
                    "해킹 시도",
                    "해킹 내용",
                    "해커"
            );

            // When & Then: 타인 수정 시 권한 예외 발생
            assertThatThrownBy(() -> postService.updatePost(postId, unauthorizedUpdateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");

            // Given: 정당한 작성자의 수정 요청 정보
            PostDto.UpdateRequest authorizedUpdateRequest = new PostDto.UpdateRequest(
                    "수정된 BDD 제목",
                    "수정된 BDD 내용",
                    "테스터"
            );

            // When: 게시글 수정
            PostDto.DetailResponse updatedPost = postService.updatePost(postId, authorizedUpdateRequest);

            // Then: 게시글 제목과 내용이 업데이트됨
            assertThat(updatedPost.title()).isEqualTo("수정된 BDD 제목");
            assertThat(updatedPost.content()).isEqualTo("수정된 BDD 내용");

            // -------------------------------------------------------------
            // Step 5: 댓글 및 게시글 삭제
            // -------------------------------------------------------------
            // Given: 작성자 정보와 함께 댓글 삭제 요청
            // When: 댓글 삭제
            commentService.deleteComment(createdComment.id(), "댓글작성자");

            // Then: 게시글의 댓글 목록이 비어있음
            assertThat(commentService.getCommentsByPost(postId)).isEmpty();

            // Given: 작성자 정보와 함께 게시글 삭제 요청
            // When: 게시글 삭제
            postService.deletePost(postId, "테스터");

            // Then: 해당 게시판의 게시글 목록이 비어있음
            assertThat(postService.getPostsByBoard(boardId)).isEmpty();
        }
    }
}
