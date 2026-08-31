package org.study.bbsdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.bbsdemo.domain.Comment;
import org.study.bbsdemo.domain.Post;
import org.study.bbsdemo.dto.CommentDto;
import org.study.bbsdemo.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public CommentDto.Response createComment(Long postId, CommentDto.CreateRequest request) {
        Post post = postService.findPostById(postId);

        Comment comment = Comment.create(post, request.content(), request.writer());
        Comment savedComment = commentRepository.save(comment);
        return new CommentDto.Response(savedComment);
    }

    public List<CommentDto.Response> getCommentsByPost(Long postId) {
        postService.findPostById(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommentDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto.Response updateComment(Long commentId, CommentDto.UpdateRequest request) {
        Comment comment = findCommentById(commentId);
        comment.update(request.content(), request.writer());
        return new CommentDto.Response(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String requestWriter) {
        Comment comment = findCommentById(commentId);
        comment.validateWriterPermission(requestWriter);
        comment.getPost().removeComment(comment);
        commentRepository.delete(comment);
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id=" + commentId));
    }
}
