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
import org.study.bbsdemo.dto.CommentDto;
import org.study.bbsdemo.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureRestDocs
@DisplayName("Comment REST Controller BDD + REST Docs 테스트")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CommentService commentService;

    private Comment createMockComment(Long id, Post post, String content, String writer) {
        Comment comment = Comment.create(post, content, writer);
        ReflectionTestUtils.setField(comment, "id", id);
        ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.of(2026, 8, 22, 21, 15, 0));
        ReflectionTestUtils.setField(comment, "updatedAt", LocalDateTime.of(2026, 8, 22, 21, 15, 0));
        return comment;
    }

    @Test
    @DisplayName("Given 게시글 ID와 댓글 생성 요청 정보가 주어졌을 때, When POST /api/posts/{postId}/comments를 호출하면, Then 201 Created와 생성된 댓글 응답을 반환한다.")
    void createCommentBddTest() throws Exception {
        // Given
        Long postId = 5L;
        CommentDto.CreateRequest request = new CommentDto.CreateRequest("좋은 글입니다!", "이순신");

        Board board = Board.create("자유게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "홍길동");
        Comment comment = createMockComment(100L, post, "좋은 글입니다!", "이순신");

        given(commentService.createComment(eq(postId), any(CommentDto.CreateRequest.class)))
                .willReturn(new CommentDto.Response(comment));

        // When & Then
        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.content").value("좋은 글입니다!"))
                .andExpect(jsonPath("$.writer").value("이순신"))
                .andDo(document("comment-create",
                        resource(builder()
                                .tag("3. 댓글 API")
                                .summary("댓글 작성")
                                .description("특정 게시글에 댓글을 작성합니다.")
                                .pathParameters(
                                        parameterWithName("postId").description("게시글 ID")
                                )
                                .build())));
    }

    @Test
    @DisplayName("Given 게시글 ID가 주어졌을 때, When GET /api/posts/{postId}/comments를 호출하면, Then 200 OK와 댓글 목록을 반환한다.")
    void getCommentsByPostBddTest() throws Exception {
        // Given
        Long postId = 5L;
        Board board = Board.create("자유게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "홍길동");
        Comment comment = createMockComment(100L, post, "좋은 글입니다!", "이순신");

        given(commentService.getCommentsByPost(postId))
                .willReturn(List.of(new CommentDto.Response(comment)));

        // When & Then
        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[0].content").value("좋은 글입니다!"))
                .andExpect(jsonPath("$[0].writer").value("이순신"))
                .andDo(document("comment-get-by-post",
                        resource(builder()
                                .tag("3. 댓글 API")
                                .summary("게시글별 댓글 목록 조회")
                                .description("특정 게시글에 작성된 모든 댓글 목록을 등록순으로 조회합니다.")
                                .pathParameters(
                                        parameterWithName("postId").description("게시글 ID")
                                )
                                .build())));
    }

    @Test
    @DisplayName("Given 댓글 수정 정보가 주어졌을 때, When PUT /api/comments/{commentId}를 호출하면, Then 200 OK와 수정된 댓글 응답을 반환한다.")
    void updateCommentBddTest() throws Exception {
        // Given
        Long commentId = 100L;
        CommentDto.UpdateRequest request = new CommentDto.UpdateRequest("수정된 댓글 내용입니다.", "이순신");

        Board board = Board.create("자유게시판", "설명");
        Post post = Post.create(board, "제목", "내용", "홍길동");
        Comment comment = createMockComment(commentId, post, "수정된 댓글 내용입니다.", "이순신");

        given(commentService.updateComment(eq(commentId), any(CommentDto.UpdateRequest.class)))
                .willReturn(new CommentDto.Response(comment));

        // When & Then
        mockMvc.perform(put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.content").value("수정된 댓글 내용입니다."))
                .andExpect(jsonPath("$.writer").value("이순신"))
                .andDo(document("comment-update",
                        resource(builder()
                                .tag("3. 댓글 API")
                                .summary("댓글 수정")
                                .description("댓글 내용을 수정합니다. 작성자 확인이 필요합니다.")
                                .pathParameters(
                                        parameterWithName("commentId").description("댓글 ID")
                                )
                                .build())));
    }

    @Test
    @DisplayName("Given 댓글 ID와 작성자명이 주어졌을 때, When DELETE /api/comments/{commentId}를 호출하면, Then 204 No Content를 반환한다.")
    void deleteCommentBddTest() throws Exception {
        // Given
        Long commentId = 100L;
        String writer = "이순신";
        willDoNothing().given(commentService).deleteComment(commentId, writer);

        // When & Then
        mockMvc.perform(delete("/api/comments/{commentId}?writer={writer}", commentId, writer))
                .andExpect(status().isNoContent())
                .andDo(document("comment-delete",
                        resource(builder()
                                .tag("3. 댓글 API")
                                .summary("댓글 삭제")
                                .description("댓글을 삭제합니다. 작성자 확인이 필요합니다.")
                                .pathParameters(
                                        parameterWithName("commentId").description("댓글 ID")
                                )
                                .queryParameters(
                                        parameterWithName("writer").description("작성자 이름 (삭제 권한 검증용)")
                                )
                                .build())));
    }
}
