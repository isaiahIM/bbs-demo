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

import static org.assertj.core.api.Assertions.assertThat;
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
}
