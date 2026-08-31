package org.study.bbsdemo.dto;

import org.study.bbsdemo.domain.Board;

import java.time.LocalDateTime;

public class BoardDto {

    public record Request(
            String name,
            String description
    ) {}

    public record Response(
            Long id,
            String name,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public Response(Board board) {
            this(
                    board.getId(),
                    board.getName(),
                    board.getDescription(),
                    board.getCreatedAt(),
                    board.getUpdatedAt()
            );
        }
    }
}
