package com.bitbox.board.repository;

import com.bitbox.board.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByBoard_Id(Long boardId);
}
