package org.study.bbsdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.domain.Comment;
import org.study.bbsdemo.domain.Post;
import org.study.bbsdemo.dto.PostDto;
import org.study.bbsdemo.service.PostService;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureRestDocs
@DisplayName("Post REST Controller BDD + REST Docs 테스트")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PostService postService;

    private Board createMockBoard(Long id, String name, String description) {
        Board board = Board.create(name, description);
        ReflectionTestUtils.setField(board, "id", id);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.of(2026, 8, 22, 21, 0, 0));
        ReflectionTestUtils.setField(board, "updatedAt", LocalDateTime.of(2026, 8, 22, 21, 0, 0));
        return board;
    }

    private Post createMockPost(Long id, Board board, String title, String content, String writer) {
        Post post = Post.create(board, title, content, writer);
        ReflectionTestUtils.setField(post, "id", id);
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.of(2026, 8, 22, 21, 10, 0));
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.of(2026, 8, 22, 21, 10, 0));
        return post;
    }

    private Comment createMockComment(Long id, Post post, String content, String writer) {
        Comment comment = Comment.create(post, content, writer);
        ReflectionTestUtils.setField(comment, "id", id);
        ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.of(2026, 8, 22, 21, 15, 0));
        ReflectionTestUtils.setField(comment, "updatedAt", LocalDateTime.of(2026, 8, 22, 21, 15, 0));
        return comment;
    }

    @Test
    @DisplayName("Given 게시판 ID와 게시글 작성 정보가 주어졌을 때, When POST /api/boards/{boardId}/posts를 호출하면, Then 201 Created와 게시글 상세 응답을 반환한다.")
    void createPostBddTest() throws Exception {
        // Given
        Long boardId = 1L;
        PostDto.CreateRequest request = new PostDto.CreateRequest("제목", "내용", "작성자");

        Board board = createMockBoard(boardId, "자유게시판", "설명");
        Post post = createMockPost(10L, board, "제목", "내용", "작성자");
        PostDto.DetailResponse response = new PostDto.DetailResponse(post);

        given(postService.createPost(eq(boardId), any(PostDto.CreateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/boards/{boardId}/posts", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.writer").value("작성자"))
                .andDo(document("post-create",
                        resource(builder()
                                .tag("2. 게시글 API")
                                .summary("게시글 작성")
                                .description("특정 게시판에 게시글을 작성합니다.")
                                .build())));
    }

    @Test
    @DisplayName("Given 게시글 ID가 주어졌을 때, When GET /api/posts/{postId}를 호출하면, Then 200 OK와 게시글 상세 정보를 반환한다.")
    void getPostDetailBddTest() throws Exception {
        // Given
        Long postId = 10L;
        Board board = createMockBoard(1L, "자유게시판", "설명");
        Post post = createMockPost(postId, board, "조회 제목", "조회 내용", "홍길동");
        createMockComment(100L, post, "좋은 글입니다!", "이순신");

        given(postService.getPostDetail(postId)).willReturn(new PostDto.DetailResponse(post));

        // When & Then
        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.title").value("조회 제목"))
                .andExpect(jsonPath("$.writer").value("홍길동"))
                .andExpect(jsonPath("$.comments[0].id").value(100L))
                .andDo(document("post-get-detail",
                        resource(builder()
                                .tag("2. 게시글 API")
                                .summary("게시글 상세 조회")
                                .description("게시글 상세 내용과 댓글 목록을 함께 조회합니다.")
                                .build())));
    }
}
