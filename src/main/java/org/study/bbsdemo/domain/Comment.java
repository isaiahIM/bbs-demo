package org.study.bbsdemo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = 50)
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Comment(Post post, String content, String writer) {
        validatePost(post);
        validateContent(content);
        validateWriter(writer);

        this.post = post;
        this.content = content;
        this.writer = writer;
        post.addComment(this);
    }

    public static Comment create(Post post, String content, String writer) {
        return new Comment(post, content, writer);
    }

    public void update(String content, String requestWriter) {
        validateWriterPermission(requestWriter);
        validateContent(content);

        this.content = content;
    }

    public void validateWriterPermission(String requestWriter) {
        if (requestWriter == null || !this.writer.equals(requestWriter.trim())) {
            throw new IllegalArgumentException("작성자만 수정 및 삭제할 수 있습니다.");
        }
    }

    private void validatePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("댓글은 소속 게시글이 존재해야 합니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 필수 입력 항목입니다.");
        }
    }

    private void validateWriter(String writer) {
        if (writer == null || writer.isBlank()) {
            throw new IllegalArgumentException("작성자는 필수 입력 항목입니다.");
        }
    }
}
