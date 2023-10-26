package com.bitbox.board.repository;

import com.bitbox.board.entity.Board;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> , BoardCustomRepository{
  Page<Board> findAllByBoardTitle(String title, Pageable pageable);
  Page<Board> findAllByMemberId(String memberId, Pageable pageable);
  // 테스트용
  Optional<Board> findByBoardTitle(String title);
}
