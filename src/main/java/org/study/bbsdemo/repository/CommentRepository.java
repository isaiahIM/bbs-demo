package org.study.bbsdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.bbsdemo.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
