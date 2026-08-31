package org.study.bbsdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.bbsdemo.domain.Board;
import org.study.bbsdemo.domain.Post;
import org.study.bbsdemo.dto.PostDto;
import org.study.bbsdemo.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardService boardService;

    @Transactional
    public PostDto.DetailResponse createPost(Long boardId, PostDto.CreateRequest request) {
        Board board = boardService.findBoardById(boardId);

        Post post = Post.create(board, request.title(), request.content(), request.writer());
        Post savedPost = postRepository.save(post);
        return new PostDto.DetailResponse(savedPost);
    }

    public List<PostDto.SummaryResponse> getPostsByBoard(Long boardId) {
        boardService.findBoardById(boardId);
        return postRepository.findByBoardIdOrderByCreatedAtDesc(boardId).stream()
                .map(PostDto.SummaryResponse::new)
                .collect(Collectors.toList());
    }

    public PostDto.DetailResponse getPostDetail(Long postId) {
        Post post = findPostById(postId);
        return new PostDto.DetailResponse(post);
    }

    @Transactional
    public PostDto.DetailResponse updatePost(Long postId, PostDto.UpdateRequest request) {
        Post post = findPostById(postId);
        post.update(request.title(), request.content(), request.writer());
        return new PostDto.DetailResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, String requestWriter) {
        Post post = findPostById(postId);
        post.validateWriterPermission(requestWriter);
        postRepository.delete(post);
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + postId));
    }
}
