package com.bitbox.board.repository;

import static com.bitbox.board.entity.QComment.comment;

import com.bitbox.board.entity.Comment;
import com.bitbox.board.entity.QComment;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Comment> findAllByBoardId(Long boardId) {
    QComment child = new QComment("child");

    JPAQuery<Comment> query = jpaQueryFactory
        .selectFrom(comment)
        .leftJoin(comment.commentList, child)
        .fetchJoin()
        .where(
            comment.board.id.eq(boardId),
            comment.masterComment.isNull(),
            comment.isDeleted.isFalse(),
            child.isNull().or(child.isDeleted.isFalse())
        );
    
    return query.fetch();
  }
}
