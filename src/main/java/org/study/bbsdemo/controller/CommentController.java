package org.study.bbsdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.study.bbsdemo.dto.CommentDto;
import org.study.bbsdemo.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentDto.Response> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto.CreateRequest request) {
        CommentDto.Response response = commentService.createComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDto.Response> response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto.UpdateRequest request) {
        CommentDto.Response response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam String writer) {
        commentService.deleteComment(commentId, writer);
        return ResponseEntity.noContent().build();
    }
}
