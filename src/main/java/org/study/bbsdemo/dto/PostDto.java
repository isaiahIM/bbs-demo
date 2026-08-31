package org.study.bbsdemo.dto;

import org.study.bbsdemo.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    public record CreateRequest(
            String title,
            String content,
            String writer
    ) {
    }

    public record UpdateRequest(
            String title,
            String content,
            String writer
    ) {
    }

    public record SummaryResponse(
            Long id,
            String title,
            String writer,
            int commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public SummaryResponse(Post post) {
            this(
                    post.getId(),
                    post.getTitle(),
                    post.getWriter(),
                    post.getComments().size(),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        }
    }

    public record DetailResponse(
            Long id,
            Long boardId,
            String title,
            String content,
            String writer,
            List<CommentDto.Response> comments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public DetailResponse(Post post) {
            this(
                    post.getId(),
                    post.getBoard().getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getWriter(),
                    post.getComments().stream().map(CommentDto.Response::new).toList(),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        }
    }
}
