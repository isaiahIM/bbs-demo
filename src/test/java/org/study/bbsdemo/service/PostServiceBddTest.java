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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("Given 게시판 ID가 주어졌을 때, When getPostsByBoard를 호출하면, Then 게시글 요약 목록을 반환한다.")
    void getPostsByBoardBdd() {
        // Given
        Long boardId = 1L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", boardId);

        Post post1 = Post.create(board, "제목1", "내용1", "작성자1");
        ReflectionTestUtils.setField(post1, "id", 10L);
        ReflectionTestUtils.setField(post1, "createdAt", LocalDateTime.now());

        Post post2 = Post.create(board, "제목2", "내용2", "작성자2");
        ReflectionTestUtils.setField(post2, "id", 11L);
        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now());

        given(boardService.findBoardById(boardId)).willReturn(board);
        given(postRepository.findByBoardIdOrderByCreatedAtDesc(boardId)).willReturn(List.of(post2, post1));

        // When
        List<PostDto.SummaryResponse> responses = postService.getPostsByBoard(boardId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).title()).isEqualTo("제목2");
        assertThat(responses.get(1).title()).isEqualTo("제목1");
        then(boardService).should().findBoardById(boardId);
        then(postRepository).should().findByBoardIdOrderByCreatedAtDesc(boardId);
    }

    @Test
    @DisplayName("Given 존재하는 게시글 ID가 주어졌을 때, When getPostDetail을 호출하면, Then 게시글 상세 정보를 반환한다.")
    void getPostDetailBdd() {
        // Given
        Long postId = 10L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", 1L);

        Post post = Post.create(board, "상세제목", "상세내용", "작성자");
        ReflectionTestUtils.setField(post, "id", postId);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When
        PostDto.DetailResponse response = postService.getPostDetail(postId);

        // Then
        assertThat(response.id()).isEqualTo(postId);
        assertThat(response.title()).isEqualTo("상세제목");
        assertThat(response.content()).isEqualTo("상세내용");
        then(postRepository).should().findById(postId);
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

    @Test
    @DisplayName("Given 작성자가 일치하는 게시글 삭제 요청이 올 때, When deletePost를 호출하면, Then 정상 삭제된다.")
    void deletePostSuccessBdd() {
        // Given
        Long postId = 10L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", 1L);

        Post post = Post.create(board, "제목", "내용", "원작성자");
        ReflectionTestUtils.setField(post, "id", postId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When
        postService.deletePost(postId, "원작성자");

        // Then
        then(postRepository).should().findById(postId);
        then(postRepository).should().delete(post);
    }

    @Test
    @DisplayName("Given 타인의 게시글 삭제 요청이 올 때, When deletePost를 호출하면, Then 권한 예외가 발생한다.")
    void deletePostUnauthorizedThrowsException() {
        // Given
        Long postId = 10L;
        Board board = Board.create("자유게시판", "설명");
        ReflectionTestUtils.setField(board, "id", 1L);

        Post post = Post.create(board, "제목", "내용", "원작성자");
        ReflectionTestUtils.setField(post, "id", postId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(postId, "타인"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("작성자만 수정 및 삭제할 수 있습니다.");
        then(postRepository).should().findById(postId);
    }

    @Test
    @DisplayName("Given 존재하지 않는 게시글 ID가 주어졌을 때, When 조회를 시도하면, Then IllegalArgumentException 예외가 발생한다.")
    void findPostByIdNotFoundThrowsException() {
        // Given
        Long nonExistentId = 999L;
        given(postRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.findPostById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다. id=" + nonExistentId);
        then(postRepository).should().findById(nonExistentId);
    }
}
