package com.bitbox.board.repository;

import com.bitbox.board.dto.response.CommentResponseDto;
import com.bitbox.board.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findAllByMemberId(String memberId, Pageable pageable);
}
