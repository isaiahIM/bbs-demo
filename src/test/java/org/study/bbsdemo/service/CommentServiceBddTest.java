package org.study.bbsdemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.domain.Comment;
import org.study.bbsdemo.domain.Post;
import org.study.bbsdemo.dto.CommentDto;
import org.study.bbsdemo.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService BDD 단위 테스트")
class CommentServiceBddTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("Given 게시글과 댓글 요청 정보가 주어졌을 때, When createComment를 호출하면, Then 댓글이 생성되고 저장된 응답을 반환한다.")
    void createCommentBdd() {
        // Given
        Long postId = 1L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        Comment comment = Comment.create(post, "댓글 내용", "댓글 작성자");
        ReflectionTestUtils.setField(comment, "id", 100L);
        ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.now());

        CommentDto.CreateRequest request = new CommentDto.CreateRequest("댓글 내용", "댓글 작성자");

        given(postService.findPostById(postId)).willReturn(post);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        // When
        CommentDto.Response response = commentService.createComment(postId, request);

        // Then
        assertThat(response.content()).isEqualTo("댓글 내용");
        assertThat(response.writer()).isEqualTo("댓글 작성자");
        assertThat(response.createdAt()).isNotNull();
        then(postService).should().findPostById(postId);
        then(commentRepository).should().save(any(Comment.class));
    }

    @Test
    @DisplayName("Given 게시글 ID가 주어졌을 때, When getCommentsByPost를 호출하면, Then 댓글 목록 응답을 반환한다.")
    void getCommentsByPostBdd() {
        // Given
        Long postId = 1L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        ReflectionTestUtils.setField(post, "id", postId);

        Comment comment1 = Comment.create(post, "댓글1", "작성자1");
        ReflectionTestUtils.setField(comment1, "id", 100L);
        ReflectionTestUtils.setField(comment1, "createdAt", LocalDateTime.now());

        Comment comment2 = Comment.create(post, "댓글2", "작성자2");
        ReflectionTestUtils.setField(comment2, "id", 101L);
        ReflectionTestUtils.setField(comment2, "createdAt", LocalDateTime.now());

        given(postService.findPostById(postId)).willReturn(post);
        given(commentRepository.findByPostIdOrderByCreatedAtAsc(postId)).willReturn(List.of(comment1, comment2));

        // When
        List<CommentDto.Response> responses = commentService.getCommentsByPost(postId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).content()).isEqualTo("댓글1");
        assertThat(responses.get(1).content()).isEqualTo("댓글2");
        then(postService).should().findPostById(postId);
        then(commentRepository).should().findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Test
    @DisplayName("Given 댓글 수정 정보가 주어졌을 때, When updateComment를 호출하면, Then 수정된 댓글 정보를 반환한다.")
    void updateCommentBdd() {
        // Given
        Long commentId = 100L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        Comment comment = Comment.create(post, "원댓글", "댓글작성자");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        CommentDto.UpdateRequest updateRequest = new CommentDto.UpdateRequest("수정댓글", "댓글작성자");

        // When
        CommentDto.Response response = commentService.updateComment(commentId, updateRequest);

        // Then
        assertThat(response.content()).isEqualTo("수정댓글");
        then(commentRepository).should().findById(commentId);
    }

    @Test
    @DisplayName("Given 타인의 댓글 수정 요청이 주어졌을 때, When updateComment를 호출하면, Then 권한 예외가 발생한다.")
    void updateCommentUnauthorizedThrowsException() {
        // Given
        Long commentId = 100L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        Comment comment = Comment.create(post, "원댓글", "댓글작성자");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        CommentDto.UpdateRequest updateRequest = new CommentDto.UpdateRequest("수정댓글", "타인");

        // When & Then
        assertThatThrownBy(() -> commentService.updateComment(commentId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");
        then(commentRepository).should().findById(commentId);
    }

    @Test
    @DisplayName("Given 작성자의 댓글 삭제 요청이 올 때, When deleteComment를 호출하면, Then 댓글이 게시글 및 리포지토리에서 삭제된다.")
    void deleteCommentSuccessBdd() {
        // Given
        Long commentId = 100L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        Comment comment = Comment.create(post, "댓글 내용", "댓글작성자");
        ReflectionTestUtils.setField(comment, "id", commentId);

        assertThat(post.getComments()).contains(comment);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // When
        commentService.deleteComment(commentId, "댓글작성자");

        // Then
        assertThat(post.getComments()).doesNotContain(comment);
        then(commentRepository).should().findById(commentId);
        then(commentRepository).should().delete(comment);
    }

    @Test
    @DisplayName("Given 타인의 댓글 삭제 요청이 올 때, When deleteComment를 호출하면, Then 권한 예외가 발생한다.")
    void deleteCommentUnauthorizedThrowsException() {
        // Given
        Long commentId = 100L;
        Board board = Board.create("게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "작성자");
        Comment comment = Comment.create(post, "댓글 내용", "댓글작성자");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // When & Then
        assertThatThrownBy(() -> commentService.deleteComment(commentId, "타인"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");
        then(commentRepository).should().findById(commentId);
    }

    @Test
    @DisplayName("Given 존재하지 않는 댓글 ID가 주어졌을 때, When 조회를 시도하면, Then IllegalArgumentException 예외가 발생한다.")
    void findCommentByIdNotFoundThrowsException() {
        // Given
        Long nonExistentId = 999L;
        given(commentRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.findCommentById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 댓글입니다. id=" + nonExistentId);
        then(commentRepository).should().findById(nonExistentId);
    }
}
