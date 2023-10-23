package com.bitbox.board.repository;

import com.bitbox.board.entity.BoardImage;
import com.bitbox.board.entity.BoardImageId;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface BoardImageRepository extends CrudRepository<BoardImage, BoardImageId> {

}
