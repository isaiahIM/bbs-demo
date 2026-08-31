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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
                                .build())));
    }
}
