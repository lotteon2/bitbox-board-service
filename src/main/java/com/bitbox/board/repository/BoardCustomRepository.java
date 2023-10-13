package com.bitbox.board.repository;

import com.bitbox.board.entity.Board;
import java.util.List;

public interface BoardCustomRepository {

  List<Board> findAllByCategoryIdFetchJoin(Long categoryId);
}
