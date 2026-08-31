package org.study.bbsdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.bbsdemo.domain.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}
