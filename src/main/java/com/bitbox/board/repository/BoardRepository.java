package com.bitbox.board.repository;

import com.bitbox.board.entity.Board;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Optional<Board> findByBoardTitle(String title);
}
