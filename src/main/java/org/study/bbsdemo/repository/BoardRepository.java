package org.study.bbsdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.bbsdemo.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
