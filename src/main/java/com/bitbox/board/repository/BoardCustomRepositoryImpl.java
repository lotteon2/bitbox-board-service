package com.bitbox.board.repository;

import static com.bitbox.board.entity.QBoard.*;

import com.bitbox.board.entity.Board;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Board> findAllByCategoryIdFetchJoin(Long categoryId) {
    return jpaQueryFactory.selectFrom(board)
        .join(board.category)
        .fetchJoin()
        .where(board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)), board.category.isDeleted.isFalse())
        .fetch();
  }
}
