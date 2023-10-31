package com.bitbox.board.repository;

import static com.bitbox.board.entity.QBoard.*;
import static com.bitbox.board.entity.QCategory.*;

import com.bitbox.board.entity.Board;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  // 카테고리 id가 categoryId이고, 상위카테고리 id가 categoryId인 글 중 삭제되지 않은 글 목록
  @Override
  public Page<Board> findAllByCategoryIdFetchJoin(Long categoryId, Pageable pageable) {
    JPAQuery<Board> query = jpaQueryFactory
        .selectFrom(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.isDeleted.isFalse(),
            board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)),
            board.category.isDeleted.isFalse()
        )
        .orderBy(board.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    List<Board> results = query.fetch();
    long total = query.fetch().size();

    return new PageImpl<>(results, pageable, total);
  }

  @Override
  public Page<Board> findAllByBoardTitleAndCategoryIdFetchJoin(String boardTitle, Long categoryId,
      Pageable pageable) {
    JPAQuery<Board> query = jpaQueryFactory
        .selectFrom(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.isDeleted.isFalse(),
            board.boardTitle.contains(boardTitle),
            board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)),
            board.category.isDeleted.isFalse())
        .orderBy(board.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    List<Board> results = query.fetch();
    long total = query.fetch().size();

    return new PageImpl<>(results, pageable, total);
  }
}
