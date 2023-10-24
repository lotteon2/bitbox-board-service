package com.bitbox.board.repository;

import com.bitbox.board.entity.BoardImage;
import com.bitbox.board.vo.BoardImageId;
import java.util.List;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface BoardImageRepository extends CrudRepository<BoardImage, BoardImageId> {
  List<BoardImage> findByBoardId(Long boardId);
}
