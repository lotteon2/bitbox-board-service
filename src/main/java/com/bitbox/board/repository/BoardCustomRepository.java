package com.bitbox.board.repository;

import com.bitbox.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardCustomRepository {

  Page<Board> findAllByCategoryIdFetchJoin(Long categoryId, Pageable pageable);
  Page<Board> findAllByBoardTitleAndCategoryIdFetchJoin(String boardTitle, Long categoryId, Pageable pageable);
}
