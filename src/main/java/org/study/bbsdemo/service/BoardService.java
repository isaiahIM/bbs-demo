package org.study.bbsdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.dto.BoardDto;
import org.study.bbsdemo.repository.BoardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public BoardDto.Response createBoard(BoardDto.Request request) {
        Board board = Board.create(request.name(), request.description());
        Board savedBoard = boardRepository.save(board);
        return new BoardDto.Response(savedBoard);
    }

    public List<BoardDto.Response> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(BoardDto.Response::new)
                .collect(Collectors.toList());
    }

    public BoardDto.Response getBoard(Long boardId) {
        Board board = findBoardById(boardId);
        return new BoardDto.Response(board);
    }

    @Transactional
    public BoardDto.Response updateBoard(Long boardId, BoardDto.Request request) {
        Board board = findBoardById(boardId);
        board.update(request.name(), request.description());
        return new BoardDto.Response(board);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = findBoardById(boardId);
        boardRepository.delete(board);
    }

    public Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다. id=" + boardId));
    }
}
