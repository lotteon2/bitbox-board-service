package com.bitbox.board.repository;

import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.entity.Board;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardCustomRepository {

  Page<Board> findAllByCategoryIdFetchJoin(Long categoryId, Pageable pageable);
  Page<Board> findAllByBoardTitleAndCategoryIdFetchJoin(String boardTitle, Long categoryId, Pageable pageable);
//  Optional<Board> findDetailById(Long boardId);
}
