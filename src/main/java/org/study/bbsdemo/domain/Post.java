package org.study.bbsdemo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Post(Board board, String title, String content, String writer) {
        validateBoard(board);
        validateTitle(title);
        validateContent(content);
        validateWriter(writer);

        this.board = board;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Post create(Board board, String title, String content, String writer) {
        return new Post(board, title, content, writer);
    }

    public void update(String title, String content, String requestWriter) {
        validateWriterPermission(requestWriter);
        validateTitle(title);
        validateContent(content);

        this.title = title;
        this.content = content;
    }

    public void validateWriterPermission(String requestWriter) {
        if (requestWriter == null || !this.writer.equals(requestWriter.trim())) {
            throw new IllegalArgumentException("작성자만 수정 및 삭제할 수 있습니다.");
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    private void validateBoard(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("게시글은 소속 게시판이 존재해야 합니다.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
        }
    }

    private void validateWriter(String writer) {
        if (writer == null || writer.isBlank()) {
            throw new IllegalArgumentException("작성자는 필수 입력 항목입니다.");
        }
    }
}
