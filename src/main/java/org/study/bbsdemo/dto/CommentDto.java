package org.study.bbsdemo.dto;

import org.study.bbsdemo.domain.Comment;

import java.time.LocalDateTime;

public class CommentDto {

    public record CreateRequest(
            String content,
            String writer
    ) {}

    public record UpdateRequest(
            String content,
            String writer
    ) {}

    public record Response(
            Long id,
            String content,
            String writer,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public Response(Comment comment) {
            this(
                    comment.getId(),
                    comment.getContent(),
                    comment.getWriter(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        }
    }
}
