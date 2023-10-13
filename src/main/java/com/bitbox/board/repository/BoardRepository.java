package com.bitbox.board.repository;

import com.bitbox.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Page<Board> findAllByBoardTitle(String title, Pageable pageable);
}
