package com.bitbox.board.repository;

import com.bitbox.board.entity.Comment;
import java.util.List;

public interface CommentCustomRepository {

  List<Comment> findAllByBoardId(Long boardId);
}
