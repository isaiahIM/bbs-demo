package org.study.bbsdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.study.bbsdemo.dto.PostDto;
import org.study.bbsdemo.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/boards/{boardId}/posts")
    public ResponseEntity<PostDto.DetailResponse> createPost(
            @PathVariable Long boardId,
            @RequestBody PostDto.CreateRequest request) {
        PostDto.DetailResponse response = postService.createPost(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/boards/{boardId}/posts")
    public ResponseEntity<List<PostDto.SummaryResponse>> getPostsByBoard(@PathVariable Long boardId) {
        List<PostDto.SummaryResponse> response = postService.getPostsByBoard(boardId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<PostDto.DetailResponse> getPostDetail(@PathVariable Long postId) {
        PostDto.DetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<PostDto.DetailResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostDto.UpdateRequest request) {
        PostDto.DetailResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam String writer) {
        postService.deletePost(postId, writer);
        return ResponseEntity.noContent().build();
    }
}
