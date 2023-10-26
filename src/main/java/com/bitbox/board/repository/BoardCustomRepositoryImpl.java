package com.bitbox.board.repository;

import static com.bitbox.board.entity.QBoard.*;

import com.bitbox.board.entity.Board;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  // 카테고리 id가 categoryId이고, 상위카테고리 id가 categoryId인 글 중 삭제되지 않은 글 목록
  @Override
  public Page<Board> findAllByCategoryIdFetchJoin(Long categoryId, Pageable pageable) {
    List<Board> results = jpaQueryFactory
        .selectFrom(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)),
            board.category.isDeleted.isFalse())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 총 갯수
    JPAQuery<Long> count = jpaQueryFactory
        .select(board.count())
        .from(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)),
            board.category.isDeleted.isFalse()
        );

    return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
  }

  @Override
  public Page<Board> findAllByBoardTitleAndCategoryIdFetchJoin(String boardTitle, Long categoryId,
      Pageable pageable) {
    List<Board> results = jpaQueryFactory
        .selectFrom(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.boardTitle.contains(boardTitle),
            board.category.id.eq(categoryId),
            board.category.isDeleted.isFalse())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 총 갯수
    JPAQuery<Long> count = jpaQueryFactory
        .select(board.count())
        .from(board)
        .join(board.category)
        .fetchJoin()
        .where(
            board.boardTitle.eq(boardTitle),
            board.category.id.eq(categoryId).or(board.category.masterCategory.id.eq(categoryId)),
            board.category.isDeleted.isFalse()
        );

    return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
  }
}
