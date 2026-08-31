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
import org.study.bbsdemo.dto.BoardDto;
import org.study.bbsdemo.service.BoardService;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureRestDocs
@DisplayName("Board REST Controller BDD + REST Docs 테스트")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BoardService boardService;

    private Board createMockBoard(Long id, String name, String description) {
        Board board = Board.create(name, description);
        ReflectionTestUtils.setField(board, "id", id);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.of(2026, 8, 22, 21, 0, 0));
        ReflectionTestUtils.setField(board, "updatedAt", LocalDateTime.of(2026, 8, 22, 21, 0, 0));
        return board;
    }

    @Test
    @DisplayName("Given 게시판 생성 요청 정보가 주어졌을 때, When POST /api/boards를 호출하면, Then 201 Created와 생성된 게시판 응답을 반환한다.")
    void createBoardBddTest() throws Exception {
        // Given
        BoardDto.Request request = new BoardDto.Request("자유게시판", "자유 게시판입니다.");
        Board mockBoard = createMockBoard(1L, "자유게시판", "자유 게시판입니다.");
        BoardDto.Response response = new BoardDto.Response(mockBoard);

        given(boardService.createBoard(any(BoardDto.Request.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("자유게시판"))
                .andExpect(jsonPath("$.description").value("자유 게시판입니다."))
                .andDo(document("board-create",
                        resource(builder()
                                .tag("1. 게시판 API")
                                .summary("게시판 생성")
                                .description("새로운 게시판을 생성합니다.")
                                .build())));
    }

    @Test
    @DisplayName("Given 게시판 목록이 존재할 때, When GET /api/boards를 호출하면, Then 200 OK와 게시판 목록을 반환한다.")
    void getAllBoardsBddTest() throws Exception {
        // Given
        Board mockBoard = createMockBoard(1L, "공지사항", "공지사항 게시판");
        given(boardService.getAllBoards()).willReturn(List.of(new BoardDto.Response(mockBoard)));

        // When & Then
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("공지사항"))
                .andDo(document("board-get-all",
                        resource(builder()
                                .tag("1. 게시판 API")
                                .summary("게시판 전체 목록 조회")
                                .description("등록된 모든 게시판 목록을 조회합니다.")
                                .build())));
    }

    @Test
    @DisplayName("Given 게시판 ID가 주어졌을 때, When DELETE /api/boards/{boardId}를 호출하면, Then 204 No Content 상태를 반환한다.")
    void deleteBoardBddTest() throws Exception {
        // Given
        Long boardId = 1L;
        willDoNothing().given(boardService).deleteBoard(boardId);

        // When & Then
        mockMvc.perform(delete("/api/boards/{boardId}", boardId))
                .andExpect(status().isNoContent())
                .andDo(document("board-delete",
                        resource(builder()
                                .tag("1. 게시판 API")
                                .summary("게시판 삭제")
                                .description("게시판 및 하위 게시글을 삭제합니다.")
                                .build())));
    }
}
