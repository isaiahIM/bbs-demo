package org.study.bbsdemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.domain.Post;
import org.study.bbsdemo.dto.PostDto;
import org.study.bbsdemo.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService BDD 단위 테스트")
class PostServiceBddTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("Given 존재하는 게시판 ID와 게시글 정보가 주어졌을 때, When createPost를 호출하면, Then 게시글이 작성되고 결과를 반환한다.")
    void createPostBdd() {
        // Given
        Long boardId = 1L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", boardId);

        PostDto.CreateRequest request = new PostDto.CreateRequest("제목", "내용", "작성자");
        Post post = Post.create(board, "제목", "내용", "작성자");
        ReflectionTestUtils.setField(post, "id", 10L);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        given(boardService.findBoardById(boardId)).willReturn(board);
        given(postRepository.save(any(Post.class))).willReturn(post);

        // When
        PostDto.DetailResponse response = postService.createPost(boardId, request);

        // Then
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.writer()).isEqualTo("작성자");
        assertThat(response.createdAt()).isNotNull();
        then(boardService).should().findBoardById(boardId);
        then(postRepository).should().save(any(Post.class));
    }

    @Test
    @DisplayName("Given 작성자의 게시글 수정 요청이 주어졌을 때, When updatePost를 호출하면, Then 도메인 로직에 의해 수정된 상세 정보를 반환한다.")
    void updatePostBdd() {
        // Given
        Long postId = 10L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", 1L);

        Post post = Post.create(board, "기존 제목", "기존 내용", "원작성자");
        ReflectionTestUtils.setField(post, "id", postId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        PostDto.UpdateRequest updateRequest = new PostDto.UpdateRequest("새 제목", "새 내용", "원작성자");

        // When
        PostDto.DetailResponse response = postService.updatePost(postId, updateRequest);

        // Then
        assertThat(response.title()).isEqualTo("새 제목");
        assertThat(response.content()).isEqualTo("새 내용");
        then(postRepository).should().findById(postId);
    }
}
